package com.hndf.shop.dynamic.redis;

import com.hndf.shop.dynamic.redis.provider.RedisDynamicConnectionFactoryProvider;
import com.hndf.shop.dynamic.redis.utils.RedisDynamicConnectionFactoryContextHolder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心动态数据源组件
 */
@Slf4j
@Setter
public class RedisDynamicRoutingConnectionFactory implements InitializingBean, DisposableBean, RedisConnectionFactory, ReactiveRedisConnectionFactory {

    private final Map<String, RedisConnectionFactory> connectionFactoryMap = new ConcurrentHashMap<>();
    @Autowired
    private List<RedisDynamicConnectionFactoryProvider> providers;
    @Value("${spring.redis.dynamic.primary:master}")
    private String primary;
    @Value("${spring.redis.dynamic.strict:false}")
    private Boolean strict;

    public RedisConnectionFactory determineConnectionFactory() {
        String dsKey = RedisDynamicConnectionFactoryContextHolder.peek();
        return getConnectionFactory(dsKey);
    }

    private RedisConnectionFactory determinePrimaryConnectionFactory() {
        log.debug("redis-dynamic-datasource switch to the primary datasource");
        RedisConnectionFactory dataSource = connectionFactoryMap.get(primary);
        if (dataSource != null) {
            return dataSource;
        }
        throw new NullPointerException("redis-dynamic-datasource can not find primary datasource");
    }

    /**
     * 获取所有的数据源
     * @return 当前所有数据源
     */
    public Map<String, RedisConnectionFactory> getConnectionFactoryAll() {
        return connectionFactoryMap;
    }

    /**
     * 获取数据源
     * @param ds 数据源名称
     * @return 数据源
     */
    public synchronized RedisConnectionFactory getConnectionFactory(String ds) {
        if (StringUtils.isEmpty(ds)) {
            return determinePrimaryConnectionFactory();
        } else if (connectionFactoryMap.containsKey(ds)) {
            log.debug("redis-dynamic-datasource switch to the datasource named [{}]", ds);
            return connectionFactoryMap.get(ds);
        }
        if (strict) {
            throw new NullPointerException("redis-dynamic-datasource could not find a datasource named" + ds);
        }
        return determinePrimaryConnectionFactory();
    }

    /**
     * 添加数据源
     * @param ds                数据源名称
     * @param connectionFactory 数据源
     */
    public synchronized void addConnectionFactory(String ds, RedisConnectionFactory connectionFactory) {
        RedisConnectionFactory oldRedisConnectionFactory = connectionFactoryMap.put(ds, connectionFactory);
        // 关闭老的数据源
        if (oldRedisConnectionFactory != null) {
            closeConnectionFactory(ds, oldRedisConnectionFactory);
        }
        log.info("redis-dynamic-datasource - add a datasource named [{}] success", ds);
    }

    /**
     * 删除数据源
     * @param ds 数据源名称
     */
    public synchronized void removeConnectionFactory(String ds) {
        if (!StringUtils.hasText(ds)) {
            throw new RuntimeException("remove parameter could not be empty");
        }
        if (primary.equals(ds)) {
            throw new RuntimeException("could not remove primary datasource");
        }
        if (connectionFactoryMap.containsKey(ds)) {
            RedisConnectionFactory connectionFactory = connectionFactoryMap.remove(ds);
            closeConnectionFactory(ds, connectionFactory);
            log.info("redis-dynamic-datasource - remove the database named [{}] success", ds);
        } else {
            log.warn("redis-dynamic-datasource - could not find a database named [{}]", ds);
        }
    }

    @Override
    public void destroy() throws Exception {
        log.info("redis-dynamic-datasource start closing ....");
        connectionFactoryMap.forEach((k, v) -> closeConnectionFactory(k, v));
        log.info("redis-dynamic-datasource all closed success,bye");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, RedisConnectionFactory> dataSources = new HashMap<>(16);
        for (RedisDynamicConnectionFactoryProvider provider : providers) {
            dataSources.putAll(provider.loadDataSources());
        }

        for (Map.Entry<String, RedisConnectionFactory> dsItem : dataSources.entrySet()) {
            addConnectionFactory(dsItem.getKey(), dsItem.getValue());
        }

        if (connectionFactoryMap.containsKey(primary)) {
            log.info("redis-dynamic-datasource initial loaded [{}] datasource,primary datasource named [{}]", dataSources.size(), primary);
        } else {
            log.warn("redis-dynamic-datasource initial loaded [{}] datasource,Please add your primary datasource or check your configuration", dataSources.size());
        }
    }

    /**
     * close connection
     * @param ds         dsName
     * @param dataSource db
     */
    private void closeConnectionFactory(String ds, RedisConnectionFactory dataSource) {
        try {
            if (Objects.nonNull(dataSource)) {
                    if (dataSource instanceof LettuceConnectionFactory) {
                    LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) dataSource;
                    lettuceConnectionFactory.destroy();
                } else if (dataSource instanceof JedisConnectionFactory) {
                    JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) dataSource;
                    jedisConnectionFactory.destroy();
                }
            }
        } catch (Exception e) {
            log.warn("redis-dynamic-datasource closed datasource named [{}] failed", ds, e);
        }
    }

    @Override
    public ReactiveRedisConnection getReactiveConnection() {
        RedisConnectionFactory factory = determineConnectionFactory();
        if (factory instanceof LettuceConnectionFactory) {
            return ((LettuceConnectionFactory) factory).getReactiveConnection();
        }
        throw new RuntimeException("jedis not support reactiveConnection");
    }

    @Override
    public ReactiveRedisClusterConnection getReactiveClusterConnection() {
        RedisConnectionFactory factory = determineConnectionFactory();
        if (factory instanceof LettuceConnectionFactory) {
            return ((LettuceConnectionFactory) factory).getReactiveClusterConnection();
        }
        throw new RuntimeException("jedis not support reactiveClusterConnection");
    }

    @Override
    public RedisConnection getConnection() {
        return determineConnectionFactory().getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return determineConnectionFactory().getClusterConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return determineConnectionFactory().getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return determineConnectionFactory().getSentinelConnection();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        return determineConnectionFactory().translateExceptionIfPossible(e);
    }
}

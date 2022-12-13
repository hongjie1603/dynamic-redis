package com.hndf.shop.dynamic.redis.provider;

import com.hndf.shop.dynamic.redis.creator.DefaulRedisConnectionFactoryCreator;
import com.hndf.shop.dynamic.redis.config.RedisProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractConnectionFactoryProviderRedis implements RedisDynamicConnectionFactoryProvider {

    @Autowired
    private DefaulRedisConnectionFactoryCreator defaultDataSourceCreator;

    protected Map<String, RedisConnectionFactory> createDataSourceMap(Map<String, RedisProperty> dataSourcePropertiesMap) {
        Map<String, RedisConnectionFactory> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size() * 2);
        dataSourcePropertiesMap.forEach((dsName,dataSourceProperty)->{
            dataSourceMap.put(dsName, defaultDataSourceCreator.createDataSource(dataSourceProperty));
        });
        return dataSourceMap;
    }
}

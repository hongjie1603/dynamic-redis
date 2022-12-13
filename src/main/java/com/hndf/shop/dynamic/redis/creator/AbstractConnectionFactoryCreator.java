package com.hndf.shop.dynamic.redis.creator;

import com.hndf.shop.dynamic.redis.config.RedisProperty;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

public abstract class AbstractConnectionFactoryCreator implements ConnectionFactoryCreator {
    @Autowired
    protected ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration;
    @Autowired
    protected ObjectProvider<RedisClusterConfiguration> clusterConfiguration;
    @Autowired
    protected ObjectProvider<JedisClientConfigurationBuilderCustomizer> jedisBuilderCustomizer;
    @Autowired
    protected ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizer;

    public abstract RedisConnectionFactory doCreateConnectionFactory(RedisProperty dataSourceProperty);


    @Override
    public RedisConnectionFactory createConnectionFactory(RedisProperty dataSourceProperty) {
        return doCreateConnectionFactory(dataSourceProperty);
    }

}

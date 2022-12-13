package com.hndf.shop.dynamic.redis.creator.lettuce;

import com.hndf.shop.dynamic.redis.config.RedisProperty;
import com.hndf.shop.dynamic.redis.creator.AbstractConnectionFactoryCreator;
import com.hndf.shop.dynamic.redis.creator.ConnectionFactoryCreator;
import lombok.SneakyThrows;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Objects;

public class LettuceConnectionFactoryCreator extends AbstractConnectionFactoryCreator implements ConnectionFactoryCreator {

    @SneakyThrows
    @Override
    public RedisConnectionFactory doCreateConnectionFactory(RedisProperty dataSourceProperty) {
        DynamicLettuceConnectionConfiguration lettuceConnectionConfigure = new DynamicLettuceConnectionConfiguration(dataSourceProperty, sentinelConfiguration, clusterConfiguration);
        LettuceConnectionFactory lettuceConnectionFactory = lettuceConnectionConfigure.dynamicRedisConnectionFactory(builderCustomizer, lettuceConnectionConfigure.dynamicLettuceClientResources());
        //因为不是bean注入的方式生成lettuceConnectionFactory，所以需要手动调用
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    @Override
    public boolean support(RedisProperty dataSourceProperty) {
        if (Objects.isNull(dataSourceProperty.getLettuce())) {
            return false;
        }
        return true;
    }
}

package com.hndf.shop.dynamic.redis.creator;

import com.hndf.shop.dynamic.redis.config.RedisProperty;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.List;

@Slf4j
@Setter
public class DefaulRedisConnectionFactoryCreator {

    private List<ConnectionFactoryCreator> creators;

    public RedisConnectionFactory createDataSource(RedisProperty dataSourceProperty) {
        ConnectionFactoryCreator connectionFactoryCreator = null;
        for (ConnectionFactoryCreator creator : this.creators) {
            if (creator.support(dataSourceProperty)) {
                connectionFactoryCreator = creator;
                break;
            }
        }
        if (connectionFactoryCreator == null) {
            throw new IllegalStateException("creator must not be null,please check the DataSourceCreator");
        }
        return connectionFactoryCreator.createConnectionFactory(dataSourceProperty);
    }

}
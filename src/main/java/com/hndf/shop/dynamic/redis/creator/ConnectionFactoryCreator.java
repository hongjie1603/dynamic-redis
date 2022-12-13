package com.hndf.shop.dynamic.redis.creator;

import com.hndf.shop.dynamic.redis.config.RedisProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;

public interface ConnectionFactoryCreator {

    /**
     * 通过属性创建数据源
     *
     * @param dataSourceProperty 数据源属性
     * @return 被创建的数据源
     */
    RedisConnectionFactory createConnectionFactory(RedisProperty dataSourceProperty);

    /**
     * 当前创建器是否支持根据此属性创建
     *
     * @param dataSourceProperty 数据源属性
     * @return 是否支持
     */
    boolean support(RedisProperty dataSourceProperty);

}

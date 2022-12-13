package com.hndf.shop.dynamic.redis.provider;

import com.hndf.shop.dynamic.redis.config.RedisProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Map;

/**
 * YML数据源提供者
 */
@Slf4j
@AllArgsConstructor
public class YmlRedisDynamicConnectionFactoryProvider extends AbstractConnectionFactoryProviderRedis {

    /**
     * 所有数据源
     */
    private final Map<String, RedisProperty> dataSourcePropertiesMap;

    @Override
    public Map<String, RedisConnectionFactory> loadDataSources() {
        return createDataSourceMap(dataSourcePropertiesMap);
    }
}


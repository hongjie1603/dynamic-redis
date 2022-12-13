package com.hndf.shop.dynamic.redis.config;

import com.hndf.shop.dynamic.redis.RedisDynamicRoutingConnectionFactory;
import com.hndf.shop.dynamic.redis.provider.RedisDynamicConnectionFactoryProvider;
import com.hndf.shop.dynamic.redis.provider.YmlRedisDynamicConnectionFactoryProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 动态数据源核心自动配置类
 * @author hongjie
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisDynamicProperties.class)
@Import({RedisDynamicRoutingConnectionFactory.class})
@ConditionalOnProperty(prefix = RedisDynamicProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(RedisConnectionFactory.class)
public class RedisDynamicConnectionFactoryAutoConfiguration {
    private final RedisDynamicProperties properties;

    public RedisDynamicConnectionFactoryAutoConfiguration(RedisDynamicProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RedisDynamicConnectionFactoryProvider YmlDynamicDataSourceProvider() {
        return new YmlRedisDynamicConnectionFactoryProvider(properties.getDatasource());
    }


}

package com.hndf.shop.dynamic.redis.config;

import com.hndf.shop.dynamic.redis.creator.ConnectionFactoryCreator;
import com.hndf.shop.dynamic.redis.creator.DefaulRedisConnectionFactoryCreator;
import com.hndf.shop.dynamic.redis.creator.lettuce.LettuceConnectionFactoryCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.lettuce.LettuceClusterConnection;

import java.util.List;

/**
 * ConnectionFactory创建器配置
 */
@Configuration
public class RedisDynamicConnectionFactoryCreatorAutoConfiguration {

    public static final int LETTUCE_ORDER = 2000;

    @Primary
    @Bean
    @ConditionalOnMissingBean
    public DefaulRedisConnectionFactoryCreator redisConnectionFactoryCreator(List<ConnectionFactoryCreator> connectionFactoryCreators) {
        DefaulRedisConnectionFactoryCreator defaulRedisConnectionFactoryCreator = new DefaulRedisConnectionFactoryCreator();
        defaulRedisConnectionFactoryCreator.setCreators(connectionFactoryCreators);
        return defaulRedisConnectionFactoryCreator;
    }


    /**
     * 存在lettuce客户端时, 加入创建器
     */
    @ConditionalOnClass(LettuceClusterConnection.class)
    @Configuration
    static class LettuceDataSourCreatorConfiguration {

        @Bean
        @Order(LETTUCE_ORDER)
        public LettuceConnectionFactoryCreator lettuceConnectionFactoryCreator() {
            return new LettuceConnectionFactoryCreator();
        }
    }
}

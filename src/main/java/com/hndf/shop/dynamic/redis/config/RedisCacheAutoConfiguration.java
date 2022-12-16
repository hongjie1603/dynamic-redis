package com.hndf.shop.dynamic.redis.config;

import com.hndf.shop.dynamic.redis.interceptor.CacheInterceptor;
import com.hndf.shop.dynamic.redis.method.DeafultRedisMethod;
import com.hndf.shop.dynamic.redis.method.RedisMethod;
import com.hndf.shop.dynamic.redis.opshandle.RedisOpsHandle;
import com.hndf.shop.dynamic.redis.opshandle.RedisOpsHashHandle;
import com.hndf.shop.dynamic.redis.opshandle.RedisOpsStringHandle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis限流配置
 */
@Configuration
@ConditionalOnClass(RedisTemplate.class)
public class RedisCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisOpsHandle deafultRedisOpsStringHandle() {
        return new RedisOpsStringHandle();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisOpsHandle deafultRedisOpsHashHandle() {
        return new RedisOpsHashHandle();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisMethod deafultRedisMethod() {
        return new DeafultRedisMethod();
    }

    @Bean
    @Qualifier("hndfCacheInterceptor")
    public CacheInterceptor cacheInterceptor(){
        return new CacheInterceptor();
    }

}

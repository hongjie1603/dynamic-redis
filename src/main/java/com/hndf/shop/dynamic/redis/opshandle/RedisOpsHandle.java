package com.hndf.shop.dynamic.redis.opshandle;

import com.hndf.shop.dynamic.redis.factory.RedisHandleFactory;
import com.hndf.shop.dynamic.redis.interceptor.CacheInterceptor;
import org.springframework.beans.factory.InitializingBean;

/**
 * 负责处理redis数据
 */
public interface RedisOpsHandle extends InitializingBean {

    Object get(CacheInterceptor.Context context);

    <T> void set(T value,long expiredTime, String... key);

    boolean delete(String... key);

    @Override
    default void afterPropertiesSet() {
        RedisHandleFactory.registry(this.getClass().getSimpleName(), this);
    }


}

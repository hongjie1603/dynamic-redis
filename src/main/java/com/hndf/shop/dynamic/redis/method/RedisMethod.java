package com.hndf.shop.dynamic.redis.method;

import com.hndf.shop.dynamic.redis.factory.RedisMethodHandleFactory;
import com.hndf.shop.dynamic.redis.interceptor.CacheInterceptor;
import org.springframework.beans.factory.InitializingBean;

/**
 * 主要逻辑处理的类
 */
public interface RedisMethod extends InitializingBean{


    Object execute(CacheInterceptor.Context context);

    default void afterPropertiesSet(){
        RedisMethodHandleFactory.register(this.getClass().getSimpleName(),this);
    }
}

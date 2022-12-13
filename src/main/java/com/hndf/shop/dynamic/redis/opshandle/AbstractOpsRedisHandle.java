package com.hndf.shop.dynamic.redis.opshandle;

import com.google.gson.Gson;
import com.hndf.shop.dynamic.redis.interceptor.CacheInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class AbstractOpsRedisHandle<T> implements RedisOpsHandle {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    protected final Gson gson = new Gson();


    public abstract T doGet(RedisTemplate<String, String> redisTemplate, String... key);


    public abstract <T> void doSet(RedisTemplate<String, String> redisTemplate, T value, String... key);

    public abstract Object parse(T t, Type retrunType);

    @Override
    public final Object get(CacheInterceptor.Context context) {

        T t = this.doGet(redisTemplate, context.getKey1(), context.getKey2());
        if (context.getRenewal())
            redisTemplate.expire(context.getKey1(), context.getExpiredTime(), TimeUnit.MILLISECONDS);
        return parse(t, context.getReturnType());
    }

    @Override
    public final <T> void set(T value, long expiredTime, String... key) {
        //不缓存null，后续用布隆过滤器
        if (Objects.isNull(value))
            return;
        this.doSet(redisTemplate, value, key);
        if (Objects.nonNull(expiredTime) && expiredTime > 0)
            redisTemplate.expire(key[0], expiredTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public final boolean delete(String... key) {
        return redisTemplate.delete(key[0]);
    }
}

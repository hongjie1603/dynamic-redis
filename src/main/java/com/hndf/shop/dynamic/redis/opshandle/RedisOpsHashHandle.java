package com.hndf.shop.dynamic.redis.opshandle;

import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Type;

public class RedisOpsHashHandle extends AbstractOpsRedisHandle<Object> {


    @Override
    public Object doGet(RedisTemplate<String, String> redisTemplate, String... key) {
        return redisTemplate.opsForHash().get(key[0], key[1]);
    }

    @Override
    public <T> void doSet(RedisTemplate<String, String> redisTemplate, T value, String... key) {

    }

    @Override
    public Object parse(Object t, Type retrunType) {
       return gson.fromJson((String) t, retrunType);
    }
}

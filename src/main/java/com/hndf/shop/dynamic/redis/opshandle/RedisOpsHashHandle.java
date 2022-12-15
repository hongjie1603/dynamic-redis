package com.hndf.shop.dynamic.redis.opshandle;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class RedisOpsHashHandle extends AbstractOpsRedisHandle<Object> {


    @Override
    public Object doGet(RedisTemplate<String, String> redisTemplate, String... key) {
        return redisTemplate.opsForHash().get(key[0], key[1]);
    }

    @Override
    public <T> void doSet(RedisTemplate<String, String> redisTemplate, T value, String... key) {
         redisTemplate.opsForHash().put(key[0], key[1], value);
    }

    @Override
    public Object parse(Object t, Type retrunType) {
       return gson.fromJson((String) t, retrunType);
    }
}

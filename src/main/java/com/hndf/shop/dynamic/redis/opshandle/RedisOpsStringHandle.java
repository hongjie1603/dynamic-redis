package com.hndf.shop.dynamic.redis.opshandle;

import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Type;
import java.util.Objects;

public class RedisOpsStringHandle extends AbstractOpsRedisHandle<String> {

    @Override
    public String doGet(RedisTemplate<String, String> redisTemplate, String... key) {
        return redisTemplate.opsForValue().get(key[0]);
    }

    @Override
    public <T> void doSet(RedisTemplate<String, String> redisTemplate, T value, String... key) {
        redisTemplate.opsForValue().set(key[0], gson.toJson(value));
    }

    @Override
    public Object parse(String t, Type cla) {
        if (Objects.isNull(t))
            return null;
        return gson.fromJson(t, cla);
    }
}

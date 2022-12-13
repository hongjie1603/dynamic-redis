package com.hndf.shop.dynamic.redis.factory;

import com.hndf.shop.dynamic.redis.method.RedisMethod;

import java.util.HashMap;
import java.util.Map;

public class RedisMethodHandleFactory{
    public static final Map<String, RedisMethod> methodMap = new HashMap<>();


    public static void register(String name, RedisMethod redisMethod) {
        methodMap.put(name, redisMethod);
    }

    public static RedisMethod getRedisMethod(String name){
        return methodMap.get(name);
    }
}

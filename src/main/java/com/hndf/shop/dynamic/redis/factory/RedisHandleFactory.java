package com.hndf.shop.dynamic.redis.factory;

import com.hndf.shop.dynamic.redis.opshandle.RedisOpsHandle;

import java.util.HashMap;
import java.util.Map;

public class RedisHandleFactory {

    private final static Map<String, RedisOpsHandle> redisOpsHandleMap = new HashMap<>();


    public static RedisOpsHandle getHandle(String name) {
        return redisOpsHandleMap.get(name);
    }

    public static void registry(String name, RedisOpsHandle redisOpsHandle) {
        redisOpsHandleMap.put(name, redisOpsHandle);
    }
}

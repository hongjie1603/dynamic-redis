package com.hndf.shop.dynamic.redis.factory;

import com.hndf.shop.dynamic.redis.opshandle.RedisOpsHandle;

import java.util.HashMap;
import java.util.Map;

public class RedisHandleFactory {

    private final static Map<String, RedisOpsHandle> redisOpsHandleMap = new HashMap<>();


    public static RedisOpsHandle getHandle(String type) {
        return redisOpsHandleMap.get(type);
    }

    public static void registry(RedisOpsHandle redisOpsHandle) {
        redisOpsHandleMap.put(redisOpsHandle.getType(), redisOpsHandle);
    }
}

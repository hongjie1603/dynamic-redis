package com.hndf.shop.dynamic.redis.method;

import com.hndf.shop.dynamic.redis.factory.RedisHandleFactory;
import com.hndf.shop.dynamic.redis.interceptor.CacheInterceptor;
import com.hndf.shop.dynamic.redis.opshandle.RedisOpsHandle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
@Slf4j
public class DeafultRedisMethod implements RedisMethod {


    @SneakyThrows
    @Override
    public Object execute(CacheInterceptor.Context context) {
        Object result;
        //查询redis
        RedisOpsHandle redisOpsHandle = RedisHandleFactory.getHandle(context.getHandleType().getSimpleName());
        result = redisOpsHandle.get(context);
        if (!Objects.isNull(result)) {
            return result;
        }

        result = context.getPjp().proceed();
        redisOpsHandle.set(result, context.getExpiredTime(), context.getKey1(), context.getKey2());
        return result;
    }

}

package com.hndf.shop.dynamic.redis.method;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.hndf.shop.dynamic.redis.factory.RedisHandleFactory;
import com.hndf.shop.dynamic.redis.interceptor.CacheInterceptor;
import com.hndf.shop.dynamic.redis.opshandle.RedisOpsHandle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RateLimitRedisMethod implements RedisLimitMethod {

    private ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
    private LoadingCache<String, RateLimiter> loadingCache = Caffeine.newBuilder().recordStats().maximumSize(10000).expireAfterAccess(3 * 60, TimeUnit.SECONDS).build(key -> addLimit(threadLocal.get()));

    public RateLimiter addLimit(int rate) {
        RateLimiter rateLimiter = RateLimiter.create(rate);
        return rateLimiter;
    }

    @SneakyThrows
    @Override
    public Object execute(CacheInterceptor.Context context) {
        try {
            //查询redis
            RedisOpsHandle redisOpsHandle = RedisHandleFactory.getHandle(context.getHandleType().getSimpleName());
            Object result;
            result = redisOpsHandle.get(context);
            if (!Objects.isNull(result)) {
                return result;
            }

            //获取锁
            threadLocal.set(context.getRate());
            RateLimiter rateLimiter = loadingCache.get(context.getKey1());
            boolean tryAcquire = rateLimiter.tryAcquire(2, TimeUnit.SECONDS);
            if (!tryAcquire)
                return null;
            //DCL
            result = redisOpsHandle.get(context);
            if (!Objects.isNull(result)) {
                return result;
            }
            result = context.getPjp().proceed();
            redisOpsHandle.set(result, context.getExpiredTime(), context.getKey1(), context.getKey2());
            return result;
        } finally {
            threadLocal.remove();
        }
    }

}

package com.hndf.shop.dynamic.redis.interceptor;


import com.hndf.shop.dynamic.redis.annotation.Cache;
import com.hndf.shop.dynamic.redis.factory.RedisMethodHandleFactory;
import com.hndf.shop.dynamic.redis.method.RedisMethod;
import com.hndf.shop.dynamic.redis.opshandle.RedisOpsHandle;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @Author hongj
 * @Date 2022-01-13 10:22
 * 缓存中间件
 */
@Slf4j
@Component
@Aspect
public class CacheInterceptor {

    @Pointcut("@annotation(com.hndf.shop.dynamic.redis.annotation.Cache)")
    public void controllerPointCut() {
    }

    @Around("controllerPointCut()")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
        Context context = new Context(pjp);
        RedisMethod redisMethod = context.getRedisMethod();
        Object result = redisMethod.execute(context);
        return result;
    }


    @Data
    public static class Context {
        private final String prefix;
        private final String key1;
        private final String key2;
        private final Type returnType;
        private final Integer rate;
        private final Long expiredTime;
        private final Boolean limit;
        private final Class<? extends RedisOpsHandle> handleType;
        private final Boolean renewal;
        private final RedisMethod redisMethod;
        private final ProceedingJoinPoint pjp;

        public Context(ProceedingJoinPoint pjp) {
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            Cache cache = signature.getMethod().getAnnotation(Cache.class);
            Method method = signature.getMethod();
            //解析key
            Object[] args = pjp.getArgs();
            this.prefix = cache.prefix();
            String key1EL = cache.key1();
            String key2EL = cache.key2();
            this.key1 = getKey(args, method, prefix, key1EL);
            this.key2 = getKey(args, method, prefix, key2EL);
            this.rate = cache.limitRate();
            this.expiredTime = cache.expiredTime();
            this.handleType = cache.handleType();
            this.limit = cache.limit();
            this.renewal = cache.renewal();
            this.returnType = method.getGenericReturnType();
            this.redisMethod = RedisMethodHandleFactory.getRedisMethod(cache.limit() ? cache.limitMethod().getSimpleName() : cache.method().getSimpleName());
             this.pjp = pjp;
        }


        /**
         * 解析key
         *
         * @param args   参数
         * @param method 代理的方法
         * @param prefix 前缀
         * @param key    后缀
         * @return
         */
        private String getKey(Object[] args, Method method, String prefix, String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }

            String[] keys = key.split(",");
            for (String k : keys) {
                //解析SpEl
                ExpressionParser parser = new SpelExpressionParser();
                Expression expression = parser.parseExpression(k);
                EvaluationContext context = new StandardEvaluationContext();
                DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
                String[] parameterNames = discoverer.getParameterNames(method);
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }

                String param = expression.getValue(context).toString();
                prefix = StringUtils.replace(prefix, "?", param, 1);
            }
            return prefix;
        }
    }
}

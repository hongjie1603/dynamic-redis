package com.hndf.shop.dynamic.redis.interceptor;

import com.hndf.shop.dynamic.redis.annotation.RDS;
import com.hndf.shop.dynamic.redis.utils.RedisDynamicConnectionFactoryContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RDSInterceptor {

    @Pointcut("@annotation(com.hndf.shop.dynamic.redis.annotation.RDS)")
    public void controllerPointCut() {
    }

    @SneakyThrows
    @Around("controllerPointCut()")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Exception {
        String pushedDataSource = null;
        try {
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            RDS rds = signature.getMethod().getAnnotation(RDS.class);
            pushedDataSource = RedisDynamicConnectionFactoryContextHolder.push(rds.value());
            return pjp.proceed();
        } finally {
            if (pushedDataSource != null) {
                RedisDynamicConnectionFactoryContextHolder.poll();
            }
        }
    }
}

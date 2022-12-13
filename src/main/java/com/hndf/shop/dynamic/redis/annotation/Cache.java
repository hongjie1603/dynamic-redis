package com.hndf.shop.dynamic.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用方法：
 * (1)1个key的结构 如STRING
 * @Cache(prefix = "coupon_goods_?_brand_?", key1 = "#param.goodIds,#param.brandId")
 * 假设#param.goodIds = 1 #param.brandId = 2,则生成的key:coupon_goods_1_brand_2
 * (2)2个key的结构 如HASH、BIGMAP
 * @Cache(prefix = "user_?", key1 = "#param.userId",key2="#param.userName")
 * 假设#param.userId = 1 #param.userName = aaa，则hk:user_1 hv:aaa
 * @Author hongj
 * @Date 2022/10/12 5:12 下午

 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    /**
     * key前缀
     * @return
     */
    String prefix() default "";
    /**
     * key1  reids的key 支持spel表达式
     * @return
     */
    String key1() default "";

    /**
     * key2   数据类型为hash、bigmap等时使用 支持spel表达式
     * @return
     */
    String key2() default "";

    /**
     * 锁失效时间 毫秒级   0表示永不过期
     *
     * @return
     */
    long expiredTime() default 10 * 60 * 1000;

    /**
     * 空是否要缓存，默认否
     *
     * @return
     */
    boolean nullCache() default false;

    String handleType() default "RedisOpsStringHandle";

    /**
     * 是否开启限流
     * @return
     */
    boolean limit() default false;

    /**
     * 限流方法  limit为true的时候才生效
     */
    String limitMethod() default "RateLimitRedisMethod";

    String method() default "DeafultRedisMethod";

    /**
     * 限流速率
     * @return
     */
    int limitRate() default 50;

    /**
     * 是否开启过期时间续费  true表示查询时将自动续费过期时间，续费时间为expiredTime的值
     * @return
     */
    boolean renewal() default false;

}

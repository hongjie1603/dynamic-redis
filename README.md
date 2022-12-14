# 简介

dynamic-redis 目前有2个功能

1.动态切换redis数据源

2.基于@Cache注解的方式快速生成redis缓存

# 多数据源

## 说明

1. 只做 **切换数据源** 这件核心的事情，并**不限制你的具体操作**，切换了数据源可以做任何CRUD。
2. 默认的数据源名称为 **master** ，你可以通过 `spring.redis.datasource.dynamic.primary` 修改。

## 使用方法

### 引入dynamic-datasource-spring-boot-starter

```
<dependency>
  <groupId>com.hndf.shop.dynamic-redis</groupId>
  <artifactId>dynamic-redis</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### 配置数据源

```yaml
spring:
  redis:
    dynamic:
      enabled: true #开启多数据源
      datasource:
        master: #主数据源
          database: 0
          host: localhost
          port: 6379
          password: 123
          lettuce:
            pool:
              max-active: 1
              max-wait: 1
              max-idle: 1
              min-idle: 0
        slave: #数据源2
          database: 1
          host: localhost
          port: 6379
          password: 123
          lettuce:
            pool:
              max-active: 1
              max-wait: 1
              max-idle: 1
              min-idle: 0
      strict: true
```

### 使用 **@DS** 切换数据源

**@DS** 可以注解在方法上或类上，**同时存在就近原则 方法上注解 优先于 类上注解**。

|      注解      |    结果    |
| :------------: | :--------: |
|    没有@RDS    | 默认数据源 |
| @RDS("dsName") |   连接名   |

```java
package com.hndf.shop.dynamic.redis;

import com.hndf.shop.dynamic.redis.annotation.Cache;
import com.hndf.shop.dynamic.redis.annotation.RDS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Controller {
    @Autowired
    private StringRedisTemplate redisTemplate;
  
    @RDS("slave1")
    @GetMapping("/slave1")
    public void test(){
        redisTemplate.opsForSet().add("user","1222223211");
    }
}

```

### 手动切换数据源

```java
package com.hndf.shop.dynamic.redis;

import com.hndf.shop.dynamic.redis.annotation.Cache;
import com.hndf.shop.dynamic.redis.annotation.RDS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Controller {
    @Autowired
    private StringRedisTemplate redisTemplate;
 
    @GetMapping("/slave1")
    public void test(){
        //切换数据源
        RedisDynamicConnectionFactoryContextHolder.push("ds");
        redisTemplate.opsForSet().add("user","1222223211");
    }
}


```

需要注意的是手动切换的数据源，最好自己在合适的位置 调用DynamicDataSourceContextHolder.clear()清空当前线程的数据源信息。

如果你不太清楚什么时候调用，那么可以参考下面写一个拦截器，注册进spring里即可。

@Slf4j
public class DynamicDatasourceClearInterceptor implements HandlerInterceptor {

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    return true;
}

@Override
public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
}

@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    RedisDynamicConnectionFactoryContextHolder.clear();
}
}
```

## 注意事项

目前只对多个单机部署的redis进行了简单的动态切换测试，redis集群、主从等模式还没有进行测试，另外事务、消息订阅等功能也没有进行测试

# Cache注解生成缓存

## 说明

开发中经常会使用redis来给一些结果做缓存，通常的步骤是首先查询redis，如果redis里存在则返回redis，如果不存在则从数据库里查询，并且将查询结果写入缓存。为了避免重复编码，所以增加了cache注解来处理此类编码。

## 使用方法

```java
@RestController
@RequestMapping("/test")
public class Controller {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/slave1")
    @Cache(prefix = "user_?",key1 = "#userId")
    public String test(String userId){
        //切换数据源
        return "hello wrold";
    }
}
```

## 属性

| 属性   | 类型   | 必须指定 | 默认值 | 描述                                                         |
| :----- | :----- | :------- | :----- | :----------------------------------------------------------- |
| prefix | String | 是       |        | key前缀，可以使用?表示占位符，可以有多个占位符               |
| key1   | String | 否       | ""     | 支持spel表达式，该属性的值会替换prefix里的占位符，如果有多个占位符，可以使用逗号进行分割 |
| key2   | String | 否       | ""     | 数据类型为hash、bigmap等时使用 支持spel表达式                |
| ...    | ...    | 否       | ...    | 其他的参考属性参考@Cache类                                   |
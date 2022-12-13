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
    @Autowired
    private RedisTemplate<String,String> redisTemplates;
    @Autowired
    private  Controller self;

    @GetMapping("/slave")
    @Cache(prefix = "user_?",key1 = "1")
    public String test(){
        return "hhhhhhh";
    }

    @RDS("slave1")
    @GetMapping("/slave1")
    public void test2(){
        redisTemplates.opsForSet().add("user","1222223211");
    }
}

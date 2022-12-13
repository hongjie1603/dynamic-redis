package com.hndf.shop.dynamic.redis.config;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = RedisDynamicProperties.PREFIX)
public class RedisDynamicProperties {
    public static final String PREFIX = "spring.redis.dynamic";

    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";
    /**
     * 是否启用严格模式,默认不启动. 严格模式下未匹配到数据源直接报错, 非严格模式下则使用默认数据源primary所设置的数据源
     */
    private Boolean strict = false;

    /**
     * 是否懒加载数据源
     */
    private Boolean lazy = false;

    /**
     * 每一个数据源
     */
    private Map<String, RedisProperty> datasource = new LinkedHashMap<>();
//    /**
//     * 多数据源选择算法clazz，默认负载均衡算法
//     */
//    private Class<? extends DynamicDataSourceStrategy> strategy = LoadBalanceDynamicDataSourceStrategy.class;
}

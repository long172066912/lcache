package com.lcache.spring;

import com.lcache.core.constant.CacheConfigSourceTypeEnum;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheProperties
 * @Description: cache组件配置
 * @date 2022/8/2 7:22 PM
 */
@Data
@Component
@ConfigurationProperties(prefix = LcacheProperties.PREFIX)
public class LcacheProperties {

    public static final String PREFIX = "wb.cache";

    /**
     * 是否开启
     */
    private boolean enabled = false;

    /**
     * cacheType与配置方式，默认 DB+Lettuce+Simple单连接 方式
     */
    private Map<String, CacheTypeConfig> cacheTypes;

    @Data
    public static class CacheTypeConfig {
        public CacheTypeConfig(){}
        /**
         * 配置方式，默认redissource
         */
        private CacheConfigSourceTypeEnum sourceType = CacheConfigSourceTypeEnum.DB;
        /**
         * 客户端，默认使用Lettuce
         */
        private int clientType = RedisClientConstants.LETTUCE;
        /**
         * 连接方式，默认SIMPLE单连接
         */
        private ConnectTypeEnum connectType = ConnectTypeEnum.SIMPLE;
        /**
         * 是否懒加载，默认帮助懒加载
         */
        private boolean lazyLoading = true;
        /**
         * 是否使用分布式锁，如果使用，也帮助懒加载
         */
        private boolean useLock = false;
        /**
         * 连接资源配置，只有配置Custom有效，默认连本地127.0.0.1
         */
        private List<SourceConfig> sourceConfig = Arrays.asList(new SourceConfig());
    }

    /**
     * 连接资源配置
     */
    @Data
    public static class SourceConfig {
        private String host = "127.0.0.1";
        private int port = 6379;
        private String password = null;
        private int connectTimeout = 2000;
        private int soTimeout = 1500;
        private int database = 0;
    }
}
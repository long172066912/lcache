package com.lcache.client;

import com.lcache.config.BaseCacheConfig;
import com.lcache.config.CacheBasicConfig;
import com.lcache.config.RedisConfigBuilder;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.redis.lua.RedisLuaInterface;
import com.lcache.core.constant.CacheConfigSourceTypeEnum;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.UseTypeEnum;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.core.monitor.MonitorConfig;
import com.lcache.executor.CacheExecutorFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheClientFactory
 * @Description: 缓存实例工厂，获取缓存执行器
 * @date 2021/1/19 4:17 PM
 */
public class CacheClientFactory {

    /**
     * 获取缓存执行器
     * 只传入cacheType
     *
     * @param cacheType
     * @return
     */
    public static BaseCacheExecutor getCacheExecutor(String cacheType) {
        CacheConfigModel cacheConfigModel = CacheConfigModel.newCache(cacheType);
        return CacheExecutorFactory.getCacheExecutor(RedisConfigBuilder.builder(cacheConfigModel).build(), cacheConfigModel);
    }

    /**
     * 获取Apollo配置方式缓存执行器
     *
     * @param cacheType
     * @return
     */
    public static BaseCacheExecutor getApolloCacheExecutor(String cacheType) {
        CacheConfigModel cacheConfigModel = CacheConfigModel.newCache(cacheType).setConfigSourceType(CacheConfigSourceTypeEnum.APOLLO);
        return CacheExecutorFactory.getCacheExecutor(RedisConfigBuilder.builder(cacheConfigModel).build(), cacheConfigModel);
    }

    /**
     * 获取缓存执行器
     * 传入CacheConfigModel
     *
     * @param cacheConfigModel
     * @return
     */
    public static BaseCacheExecutor getCacheExecutor(CacheConfigModel cacheConfigModel) {
        return CacheExecutorFactory.getCacheExecutor(RedisConfigBuilder.builder(cacheConfigModel).build(), cacheConfigModel);
    }

    /**
     * 自定义配置获取缓存执行器
     *
     * @param cacheType
     * @param config
     * @return
     */
    public static BaseCacheExecutor getCacheExecutor(String cacheType, BaseCacheConfig config) {
        CacheConfigModel cacheConfigModel = new CacheConfigModel();
        cacheConfigModel.setConfigSourceType(CacheConfigSourceTypeEnum.CUSTOM);
        cacheConfigModel.setCacheType(cacheType);
        return CacheExecutorFactory.getCacheExecutor(config, cacheConfigModel);
    }

    /**
     * 获取缓存执行器
     * 传入自定义配置
     *
     * @param cacheConfigModel
     * @param config
     * @return
     */
    public static BaseCacheExecutor getCacheExecutor(CacheConfigModel cacheConfigModel, BaseCacheConfig config) {
        cacheConfigModel.setConfigSourceType(CacheConfigSourceTypeEnum.CUSTOM);
        return CacheExecutorFactory.getCacheExecutor(config, cacheConfigModel);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * builder
     */
    public static class Builder {
        /**
         * cacheType
         */
        private String cacheType;
        /**
         * 客户端类型，默认Lettuce
         */
        private Integer clientType;
        /**
         * 默认连接池方式
         */
        private ConnectTypeEnum connectType;
        /**
         * 用途
         */
        private UseTypeEnum useType = CacheBasicConfig.useType;
        /**
         * 配置方式
         */
        private CacheConfigSourceTypeEnum configSourceType;
        /**
         * 是否开启monitor监控，默认开启
         */
        private Boolean isOpenMonitor;
        /**
         * 连接资源配置
         */
        private BaseCacheConfig cacheConfig;
        /**
         * 配置需要注入的lua脚本
         */
        private List<RedisLuaInterface> luas;
        /**
         * 是否本地缓存，默认不开启
         */
        private Boolean isLocalCache;
        /**
         * 开启热key本地缓存策略
         */
        private Boolean isOpenHotKeyLocalCache;
        /**
         * 本地缓存的keys
         */
        private Set<String> localCacheKeys;
        /**
         * 热key同步频率，默认5s
         */
        private Long monitorHotKeyInterval;
        /**
         * 热key容量，默认收集TOP前50
         */
        private Integer monitorHotKeyStatisticCapacity;
        /**
         * 热key收集频率内的起始值，默认 10
         */
        private Integer monitorHotKeyLeastValue;
        /**
         * 热key定义最小值，开启热key本地缓存时，超过则分布式本地缓存
         */
        private Integer monitorHotKeyMinValue;

        public Builder cacheType(String cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        public Builder clientType(Integer clientType) {
            this.clientType = null == clientType ? CacheBasicConfig.clientType : clientType;
            return this;
        }

        public Builder connectType(ConnectTypeEnum connectType) {
            this.connectType = null == connectType ? CacheBasicConfig.connectTypeEnum : connectType;
            return this;
        }

        public Builder isLocalCache(Boolean isLocalCache) {
            this.isLocalCache = isLocalCache;
            return this;
        }

        public Builder configSourceType(CacheConfigSourceTypeEnum configSourceType) {
            this.configSourceType = configSourceType;
            return this;
        }

        public Builder isOpenMonitor(Boolean isOpenMonitor) {
            this.isOpenMonitor = isOpenMonitor;
            return this;
        }

        public Builder cacheConfig(BaseCacheConfig cacheConfig) {
            this.cacheConfig = cacheConfig;
            return this;
        }

        public Builder addLua(List<RedisLuaInterface> luas) {
            this.luas = luas;
            return this;
        }

        public Builder localCacheKeys(Set<String> keys) {
            this.localCacheKeys = keys;
            return this;
        }

        public Builder localCacheKeys(String... keys) {
            this.localCacheKeys = new HashSet<>(Arrays.asList(keys));
            return this;
        }

        public Builder isOpenHotKeyLocalCache(Boolean isOpenHotKeyLocalCache) {
            this.isOpenHotKeyLocalCache = isOpenHotKeyLocalCache;
            return this;
        }

        public Builder monitorHotKeyInterval(Long millisecond) {
            this.monitorHotKeyInterval = millisecond;
            return this;
        }

        public Builder monitorHotKeyStatisticCapacity(Integer num) {
            this.monitorHotKeyStatisticCapacity = num;
            return this;
        }

        public Builder monitorHotKeyLeastValue(Integer monitorHotKeyLeastValue) {
            this.monitorHotKeyLeastValue = monitorHotKeyLeastValue;
            return this;
        }

        public Builder monitorHotKeyMinValue(Integer monitorHotKeyMinValue) {
            this.monitorHotKeyMinValue = monitorHotKeyMinValue;
            return this;
        }

        public BaseCacheExecutor build() {
            CacheConfigModel cacheConfigModel = CacheConfigModel.newCache(cacheType)
                    .setClientType(clientType)
                    .setConnectTypeEnum(connectType)
                    .setLocalCache(null == isLocalCache ? CacheBasicConfig.isLocalCache : isLocalCache)
                    .setUseType(useType)
                    .setOpenMonitor(null == isOpenMonitor ? true : isOpenMonitor)
                    .setConfigSourceType(null == configSourceType ? CacheConfigSourceTypeEnum.DB : configSourceType);
            if (null == cacheConfig) {
                cacheConfig = RedisConfigBuilder.builder(cacheConfigModel).build();
            }
            BaseCacheExecutor cacheExecutor = CacheExecutorFactory.getCacheExecutor(cacheConfig, cacheConfigModel);
            cacheExecutor.addLua(luas);
            cacheExecutor.addLocalCacheKeys(localCacheKeys);
            cacheExecutor.setIsOpenMonitor(null == isOpenHotKeyLocalCache ? false : isOpenHotKeyLocalCache);
            cacheExecutor.setMonitorHotKeyInterval(null == monitorHotKeyInterval ? MonitorConfig.HOT_KEY_COUNT_lINTERVAL_MILLISECONDS : monitorHotKeyInterval);
            cacheExecutor.setMonitorHotKeyStatisticCapacity(null == monitorHotKeyStatisticCapacity ? MonitorConfig.HOTE_KEY_STATISTIC_CAPACITY : monitorHotKeyStatisticCapacity);
            cacheExecutor.setMonitorHotKeyLeastValue(null == monitorHotKeyLeastValue ? MonitorConfig.HOT_KEY_COUNT_LEAST_VALUE : monitorHotKeyLeastValue);
            cacheExecutor.setMonitorHotKeyMinValue(null == monitorHotKeyMinValue ? MonitorConfig.HOT_KEY_MIN_VALUE : monitorHotKeyMinValue);
            return cacheExecutor;
        }
    }
}
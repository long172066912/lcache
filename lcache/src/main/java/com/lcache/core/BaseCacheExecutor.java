package com.lcache.core;

import com.lcache.config.BaseCacheConfig;
import com.lcache.core.cache.localcache.RedisLocalCacheFactory;
import com.lcache.core.cache.redis.commands.*;
import com.lcache.core.cache.redis.lua.RedisLuaInterface;
import com.lcache.core.cache.redis.lua.RedisLuaScripts;
import com.lcache.extend.handle.redis.redisson.RedissonClientManager;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.core.monitor.MonitorConfig;
import com.lcache.core.monitor.MonitorFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: BaseCacheExecutor
 * @Description: 缓存命令抽象
 * @date 2021/1/22 2:40 PM
 */
public abstract class BaseCacheExecutor implements InterfaceCacheExecutor, RedisCommands, RedissonCommands, RedisIntegrationCommands, RedisLuaCommands, RedisLockCommands {
    /**
     * 组件常用信息
     */
    private CacheConfigModel cacheConfigModel = new CacheConfigModel();
    /**
     * 连接配置
     */
    private BaseCacheConfig redisSourceConfig;
    /**
     * 一次命令不开启本地缓存
     */
    private ThreadLocal<Boolean> noLocalCacheOnce = new ThreadLocal<>();

    public CacheConfigModel getCacheConfigModel() {
        return cacheConfigModel;
    }

    public void setCacheConfigModel(CacheConfigModel cacheConfigModel) {
        this.cacheConfigModel = cacheConfigModel;
    }

    public BaseCacheConfig getRedisSourceConfig() {
        return redisSourceConfig;
    }

    public void setRedisSourceConfig(BaseCacheConfig redisSourceConfig) {
        this.redisSourceConfig = redisSourceConfig;
    }

    /**
     * 获取Redisson客户端
     *
     * @return
     */
    public RedissonClient getRedissonClient() {
        return RedissonClientManager.getRedissonClient(this.getCacheConfigModel(), this.getRedisSourceConfig());
    }

    public void addLua(List<RedisLuaInterface> luas) {
        if (CollectionUtils.isNotEmpty(luas)) {
            RedisLuaScripts.addLua(luas);
            this.loadLuaScripts();
        }
    }

    /**
     * 一次命令不开启本地缓存
     *
     * @return
     */
    public BaseCacheExecutor noLocalCacheOnce() {
        noLocalCacheOnce.set(true);
        return this;
    }

    /**
     * 开启本地缓存
     *
     * @return
     */
    public BaseCacheExecutor openLocalCache() {
        cacheConfigModel.setLocalCache(true);
        return this;
    }

    public BaseCacheExecutor closeLocalCache() {
        cacheConfigModel.setLocalCache(false);
        return this;
    }

    /**
     * 添加需要本地缓存的key
     *
     * @param keys
     * @return
     */
    public BaseCacheExecutor addLocalCacheKeys(Set<String> keys) {
        if (CollectionUtils.isNotEmpty(keys)) {
            RedisLocalCacheFactory.addLocalCacheKey(this, keys);
        }
        return this;
    }

    /**
     * 添加需要本地缓存的key
     *
     * @param keys
     * @return
     */
    public BaseCacheExecutor addLocalCacheKeys(String... keys) {
        RedisLocalCacheFactory.addLocalCacheKey(this, new HashSet<>(Arrays.asList(keys)));
        return this;
    }

    /**
     * 是否开启监控
     *
     * @param isOpen
     * @return
     */
    public BaseCacheExecutor setIsOpenMonitor(boolean isOpen) {
        cacheConfigModel.setOpenMonitor(isOpen);
        return this;
    }

    /**
     * 开启热key本地缓存策略
     *
     * @return
     */
    public BaseCacheExecutor setIsOpenHotKeyLocalCache(boolean isOpen) {
        MonitorFactory.setIsOpenHotKeyLocalCache(this, isOpen);
        return this;
    }

    /**
     * 设置热key同步频率
     *
     * @param millisecond
     * @return
     */
    public BaseCacheExecutor setMonitorHotKeyInterval(long millisecond) {
        MonitorConfig.HOT_KEY_COUNT_lINTERVAL_MILLISECONDS = millisecond;
        return this;
    }

    /**
     * 设置热key容量
     *
     * @param num 上限值
     * @return
     */
    public BaseCacheExecutor setMonitorHotKeyStatisticCapacity(int num) {
        MonitorConfig.HOTE_KEY_STATISTIC_CAPACITY = num;
        return this;
    }

    /**
     * 设置热key统计最低值
     *
     * @param value 最小值
     * @return
     */
    public BaseCacheExecutor setMonitorHotKeyLeastValue(int value) {
        MonitorConfig.HOT_KEY_COUNT_LEAST_VALUE = value;
        return this;
    }

    /**
     * 设置热key定义最低值，即达到此值的时候，热key需要本地缓存处理
     *
     * @param minValue 起始值
     * @return
     */
    public BaseCacheExecutor setMonitorHotKeyMinValue(int minValue) {
        MonitorConfig.HOT_KEY_MIN_VALUE = minValue;
        return this;
    }

    public Boolean getNoLocalCacheOnce() {
        return noLocalCacheOnce.get();
    }

    public void removeNoLocalCacheOnce() {
        this.noLocalCacheOnce.remove();
    }
}

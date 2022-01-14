package com.lcache.core.model;

import com.lcache.config.CacheBasicConfig;
import com.lcache.core.constant.CacheConfigSourceTypeEnum;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.constant.UseTypeEnum;

import java.io.Serializable;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheConfigModel
 * @Description: 缓存常用信息对象
 * @date 2021/1/27 3:53 PM
 */
public class CacheConfigModel implements Serializable {
    private static final long serialVersionUID = 3445750473732982271L;

    public CacheConfigModel() {
    }

    public CacheConfigModel(String cacheType) {
        this.cacheType = cacheType;
    }

    public CacheConfigModel(CacheConfigModel model, UseTypeEnum useType){
        this.cacheType = model.getCacheType();
        this.clientType = model.getClientType();
        this.connectTypeEnum = model.getConnectTypeEnum();
        this.useType = useType;
        this.isLocalCache = model.isLocalCache();
        this.isOpenMonitor = model.isOpenMonitor();
        this.configSourceType = model.getConfigSourceType();
    }

    public CacheConfigModel(String cacheType, int clientType, ConnectTypeEnum connectTypeEnum, UseTypeEnum useType) {
        this.cacheType = cacheType;
        this.clientType = clientType;
        this.connectTypeEnum = connectTypeEnum;
        this.useType = useType;
        this.isLocalCache = false;
    }

    public CacheConfigModel(String cacheType, int clientType, ConnectTypeEnum connectTypeEnum, UseTypeEnum useType, boolean isLocalCache) {
        this.cacheType = cacheType;
        this.clientType = clientType;
        this.connectTypeEnum = connectTypeEnum;
        this.useType = useType;
        this.isLocalCache = isLocalCache;
    }

    public static CacheConfigModel jedis(String cacheType) {
        return new CacheConfigModel(cacheType).setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.SIMPLE);
    }

    public static CacheConfigModel jedisPool(String cacheType) {
        return new CacheConfigModel(cacheType).setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.POOL);
    }

    public static CacheConfigModel lettuce(String cacheType) {
        return new CacheConfigModel(cacheType).setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.SIMPLE);
    }

    public static CacheConfigModel lettucePool(String cacheType) {
        return new CacheConfigModel(cacheType).setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.POOL);
    }

    public static CacheConfigModel newCache(String cacheType) {
        return new CacheConfigModel(cacheType);
    }

    public CacheConfigModel jedis() {
        return this.setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.SIMPLE);
    }

    public CacheConfigModel jedisPool() {
        return this.setClientType(RedisClientConstants.JEDIS).setConnectTypeEnum(ConnectTypeEnum.POOL);
    }

    public CacheConfigModel lettuce() {
        return this.setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.SIMPLE);
    }

    public CacheConfigModel lettucePool() {
        return this.setClientType(RedisClientConstants.LETTUCE).setConnectTypeEnum(ConnectTypeEnum.POOL);
    }

    /**
     * cacheType
     */
    private String cacheType = "";
    /**
     * 客户端类型，默认Lettuce
     */
    private int clientType = CacheBasicConfig.clientType;
    /**
     * 默认连接池方式
     */
    private ConnectTypeEnum connectTypeEnum = CacheBasicConfig.connectTypeEnum;
    /**
     * 是否本地缓存，默认不开启
     */
    private boolean isLocalCache = CacheBasicConfig.isLocalCache;
    /**
     * 用途
     */
    private UseTypeEnum useType = CacheBasicConfig.useType;
    /**
     * 配置方式
     */
    private CacheConfigSourceTypeEnum configSourceType = CacheConfigSourceTypeEnum.DB;
    /**
     * 是否重试，目前针对jedisPool的subscribe
     */
    private boolean isRetry = false;
    /**
     * 是否开启monitor监控，默认开启
     */
    private boolean isOpenMonitor = true;


    public CacheConfigSourceTypeEnum getConfigSourceType() {
        return configSourceType;
    }

    public CacheConfigModel setConfigSourceType(CacheConfigSourceTypeEnum configSourceType) {
        this.configSourceType = configSourceType;
        return this;
    }

    public ConnectTypeEnum getConnectTypeEnum() {
        return connectTypeEnum;
    }

    public CacheConfigModel setConnectTypeEnum(ConnectTypeEnum connectTypeEnum) {
        this.connectTypeEnum = connectTypeEnum;
        return this;
    }

    public boolean isLocalCache() {
        return isLocalCache;
    }

    public CacheConfigModel setLocalCache(boolean localCache) {
        isLocalCache = localCache;
        return this;
    }

    public String getCacheType() {
        return cacheType;
    }

    public CacheConfigModel setCacheType(String cacheType) {
        this.cacheType = cacheType;
        return this;
    }

    public int getClientType() {
        return clientType;
    }

    public CacheConfigModel setClientType(int clientType) {
        this.clientType = clientType;
        return this;
    }

    public UseTypeEnum getUseType() {
        return useType;
    }

    public CacheConfigModel setUseType(UseTypeEnum useType) {
        this.useType = useType;
        return this;
    }

    public boolean isRetry() {
        return isRetry;
    }

    public CacheConfigModel setRetry(boolean retry) {
        isRetry = retry;
        return this;
    }

    public boolean isOpenMonitor() {
        return isOpenMonitor;
    }

    public CacheConfigModel setOpenMonitor(boolean openMonitor) {
        isOpenMonitor = openMonitor;
        return this;
    }

    @Override
    public String toString() {
        return "CacheConfigModel{" +
                "cacheType='" + cacheType + '\'' +
                ", clientType=" + clientType +
                ", connectTypeEnum=" + connectTypeEnum +
                ", isLocalCache=" + isLocalCache +
                ", useType=" + useType +
                ", configSourceType=" + configSourceType +
                ", isRetry=" + isRetry +
                ", isOpenMonitor=" + isOpenMonitor +
                '}';
    }
}

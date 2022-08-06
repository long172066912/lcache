package com.lcache.core.model;


import com.lcache.core.constant.LocalCacheHandleTypeEnum;
import com.lcache.util.CacheFunction;

import java.util.Arrays;
import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheHandleProcessorModel
 * @Description: 执行器模板
 * @date 2021/2/23 3:41 PM
 */
public class CacheHandleProcessorModel<T> {
    /**
     * 连接使用
     *
     * @param function
     */
    public CacheHandleProcessorModel(CacheFunction<T> function) {
        this.function = function;
        this.isLocalCache = false;
    }

    /**
     * 命令执行使用 单个key
     *
     * @param function
     * @param commands
     * @param cacheConfigModel
     * @param key
     */
    public CacheHandleProcessorModel(CacheFunction<T> function, String commands, CacheConfigModel cacheConfigModel, String key) {
        this.function = function;
        this.cacheConfigModel = cacheConfigModel;
        this.commands = commands;
        this.setKey(key);
        this.isLocalCache = false;
    }

    /**
     * 命令执行使用 多个key
     *
     * @param function
     * @param commands
     * @param cacheConfigModel
     * @param keys
     */
    public CacheHandleProcessorModel(CacheFunction<T> function, String commands, CacheConfigModel cacheConfigModel, String[] keys) {
        this.function = function;
        this.cacheConfigModel = cacheConfigModel;
        this.commands = commands;
        this.setKeys(Arrays.asList(keys));
        this.isLocalCache = false;
    }

    /**
     * 命令
     */
    private String commands;
    /**
     * key
     */
    private String key;
    /**
     * keys
     */
    private List<String> keys;
    /**
     * 方法
     */
    private CacheFunction function;
    /**
     * 执行结果
     */
    private T result;
    /**
     * 命令执行时间
     */
    private Long executeTime;
    /**
     * 配置
     */
    private CacheConfigModel cacheConfigModel;
    /**
     * 异常信息
     */
    private Exception e;
    /**
     * 是否本地缓存
     */
    private Boolean isLocalCache;
    /**
     * 本地缓存操作类型
     */
    private LocalCacheHandleTypeEnum localHandleType;

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public CacheFunction<T> getFunction() {
        return function;
    }

    public void setFunction(CacheFunction<T> function) {
        this.function = function;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public CacheConfigModel getCacheConfigModel() {
        return cacheConfigModel;
    }

    public void setCacheConfigModel(CacheConfigModel cacheConfigModel) {
        this.cacheConfigModel = cacheConfigModel;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public LocalCacheHandleTypeEnum getLocalHandleType() {
        return localHandleType;
    }

    public CacheHandleProcessorModel setLocalHandleType(LocalCacheHandleTypeEnum localHandleType) {
        this.localHandleType = localHandleType;
        return this;
    }

    public Boolean isLocalCache() {
        return isLocalCache;
    }

    public CacheHandleProcessorModel localCache(Boolean localCache) {
        isLocalCache = localCache;
        return this;
    }
}

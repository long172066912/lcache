package com.lcache.core.model;


import com.lcache.util.CacheSetFunction;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheDataBuilder
 * @Description: 封装double check方式的数据查询，先查cache，再查db
 * @date 2021/4/29 2:27 PM
 */
public class CacheDataBuilder {
    public CacheDataBuilder() {
    }

    public CacheDataBuilder(String lockKey, Function cacheGetFunction, Function dbGetFunction, CacheSetFunction cacheSetFunction) {
        this.lockKey = lockKey;
        this.cacheGetFunction = cacheGetFunction;
        this.dbGetFunction = dbGetFunction;
        this.cacheSetFunction = cacheSetFunction;
    }

    /**
     * 从缓存中获取数据方法
     */
    private Function cacheGetFunction;
    /**
     * 从数据库中获取数据方法
     */
    private Function dbGetFunction;
    /**
     * 将数据持久化到缓存中的方法
     */
    private CacheSetFunction cacheSetFunction;
    /**
     * 加锁的key
     */
    private String lockKey;
    /**
     * 获取锁的等待时间，默认1秒
     */
    private Long waitTime = 10L;
    /**
     * 锁执行超时时间
     */
    private Long leaseTime = 30L;
    /**
     * 时间类型
     */
    private TimeUnit unit = TimeUnit.SECONDS;
    /**
     * cache异常后，是否用DB查询兜底
     */
    private Boolean cacheExceptionByDb = true;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CacheDataBuilder cacheData;

        public Builder() {
            cacheData = new CacheDataBuilder();
        }

        public Builder cacheGetFunction(Function cacheGetFunction) {
            cacheData.setCacheGetFunction(cacheGetFunction);
            return this;
        }

        public Builder dbGetFunction(Function dbGetFunction) {
            cacheData.setDbGetFunction(dbGetFunction);
            return this;
        }

        public Builder cacheSetFunction(CacheSetFunction cacheSetFunction) {
            cacheData.setCacheSetFunction(cacheSetFunction);
            return this;
        }

        public Builder lockKey(String lockKey) {
            cacheData.setLockKey(lockKey);
            return this;
        }

        public Builder waitTime(Long waitTime) {
            cacheData.setWaitTime(waitTime);
            return this;
        }

        public Builder leaseTime(Long leaseTime) {
            cacheData.setLeaseTime(leaseTime);
            return this;
        }

        public Builder unit(TimeUnit unit) {
            cacheData.setUnit(unit);
            return this;
        }

        public Builder cacheExceptionByDb(Boolean cacheExceptionByDb) {
            cacheData.setCacheExceptionByDb(cacheExceptionByDb);
            return this;
        }

        public CacheDataBuilder build() {
            return cacheData;
        }
    }

    public Function getCacheGetFunction() {
        return cacheGetFunction;
    }

    public CacheDataBuilder setCacheGetFunction(Function cacheGetFunction) {
        this.cacheGetFunction = cacheGetFunction;
        return this;
    }

    public Function getDbGetFunction() {
        return dbGetFunction;
    }

    public CacheDataBuilder setDbGetFunction(Function dbGetFunction) {
        this.dbGetFunction = dbGetFunction;
        return this;
    }

    public CacheSetFunction getCacheSetFunction() {
        return cacheSetFunction;
    }

    public CacheDataBuilder setCacheSetFunction(CacheSetFunction cacheSetFunction) {
        this.cacheSetFunction = cacheSetFunction;
        return this;
    }

    public String getLockKey() {
        return lockKey;
    }

    public CacheDataBuilder setLockKey(String lockKey) {
        this.lockKey = lockKey;
        return this;
    }

    public Long getWaitTime() {
        return waitTime;
    }

    public CacheDataBuilder setWaitTime(Long waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public Long getLeaseTime() {
        return leaseTime;
    }

    public CacheDataBuilder setLeaseTime(Long leaseTime) {
        this.leaseTime = leaseTime;
        return this;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public CacheDataBuilder setUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    public Boolean getCacheExceptionByDb() {
        return cacheExceptionByDb;
    }

    public CacheDataBuilder setCacheExceptionByDb(Boolean cacheExceptionByDb) {
        this.cacheExceptionByDb = cacheExceptionByDb;
        return this;
    }
}

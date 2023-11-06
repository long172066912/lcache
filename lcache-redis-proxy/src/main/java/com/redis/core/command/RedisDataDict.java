package com.redis.core.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redis.core.command.model.RedisDataType;

public abstract class RedisDataDict {

    private long startTime;

    private int expireTime;

    private RedisDataType redisDataType;

    protected RedisDataDict(RedisDataType redisDataType, long startTime) {
        this.redisDataType = redisDataType;
        this.startTime = startTime;
        this.expireTime = -1;
    }

    public void expire(int expireTime) {
        if (expireTime > 0) {
            startTime = System.currentTimeMillis();
            this.expireTime = expireTime;
        }
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public RedisDataType getRedisDataType() {
        return redisDataType;
    }

    public void setRedisDataType(RedisDataType redisDataType) {
        this.redisDataType = redisDataType;
    }

    /**
     * 是否过期，true到期
     *
     * @return
     */
    @JsonIgnore
    public boolean isExpire() {
        if (-1 == expireTime) {
            return false;
        }
        return System.currentTimeMillis() - startTime > expireTime * 1000L;
    }
}

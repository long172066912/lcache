package com.redis.core;

public abstract class RedisDataDict {

    private long startTime;

    private int expireTime = 0;

    protected RedisDataDict(long startTime) {
        this.startTime = startTime;
    }

    public void setExpireTime(int expireTime) {
        startTime = System.currentTimeMillis();
        this.expireTime = expireTime;
    }

    /**
     * 是否过期，true到期
     *
     * @return
     */
    public boolean isExpire() {
        return System.currentTimeMillis() - startTime > expireTime * 1000L;
    }
}

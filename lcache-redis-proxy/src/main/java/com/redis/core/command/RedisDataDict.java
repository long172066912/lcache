package com.redis.core.command;

public abstract class RedisDataDict {

    private long startTime;

    private int expireTime;

    protected RedisDataDict(long startTime) {
        this.startTime = startTime;
        this.expireTime = -1;
    }

    public void setExpireTime(int expireTime) {
        if (expireTime > 0) {
            startTime = System.currentTimeMillis();
            this.expireTime = expireTime;
        }
    }

    /**
     * 是否过期，true到期
     *
     * @return
     */
    public boolean isExpire() {
        if (-1 == expireTime) {
            return false;
        }
        return System.currentTimeMillis() - startTime > expireTime * 1000L;
    }
}

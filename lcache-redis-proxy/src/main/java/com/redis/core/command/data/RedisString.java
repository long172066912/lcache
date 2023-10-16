package com.redis.core.command.data;

import com.redis.core.command.RedisDataDict;

public class RedisString extends RedisDataDict {
    private String v;

    public RedisString(String v) {
        super(System.currentTimeMillis());
        this.v = v;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}

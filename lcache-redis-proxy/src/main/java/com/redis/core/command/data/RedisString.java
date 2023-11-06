package com.redis.core.command.data;

import com.redis.core.command.RedisDataDict;
import com.redis.core.command.model.RedisDataType;

public class RedisString extends RedisDataDict {
    private String v;

    public RedisString() {
        super(RedisDataType.STRING, System.currentTimeMillis());
    }

    public RedisString(String v) {
        super(RedisDataType.STRING, System.currentTimeMillis());
        this.v = v;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}

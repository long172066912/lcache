package com.redis.core.command.model;

import com.redis.core.command.RedisDataDict;

import java.io.Serializable;

public class RedisRdbData implements Serializable {
    private String k;
    private RedisDataDict dataDict;

    public RedisRdbData(String k, RedisDataDict dataDict) {
        this.k = k;
        this.dataDict = dataDict;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public RedisDataDict getDataDict() {
        return dataDict;
    }

    public void setDataDict(RedisDataDict dataDict) {
        this.dataDict = dataDict;
    }
}
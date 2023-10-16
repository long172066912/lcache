package com.redis.core.command.data;

import com.redis.core.command.RedisDataDict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisHash extends RedisDataDict {
    private Map<String, String> hash = new ConcurrentHashMap<>();

    public RedisHash() {
        super(System.currentTimeMillis());
    }

    public String getValue(String filed) {
        return hash.get(filed);
    }

    public void setValue(String filed, String value) {
        hash.put(filed, value);
    }

    public void remove(String filed) {
        hash.remove(filed);
    }
}

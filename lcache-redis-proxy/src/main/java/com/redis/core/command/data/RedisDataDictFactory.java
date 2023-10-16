package com.redis.core.command.data;

import com.redis.core.command.RedisDataDict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisDataDictFactory {

    private static Map<String, RedisDataDict> map = new ConcurrentHashMap<>();

    public static <T extends RedisDataDict> T get(String k) {
        final RedisDataDict data = map.get(k);
        if (null == data || data.isExpire()) {
            return null;
        }
        return (T) data;
    }

    public static void put(String k, RedisDataDict data) {
        map.put(k, data);
    }

    public static void expire(String k, int expireTime) {
        RedisDataDict data = map.get(k);
        if (null != data) {
            data.setExpireTime(expireTime);
        }
    }
}

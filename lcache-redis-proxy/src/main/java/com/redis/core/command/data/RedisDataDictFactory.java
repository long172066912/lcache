package com.redis.core.command.data;

import com.redis.core.command.RedisDataDict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisDataDictFactory {

    private static Map<String, RedisDataDict> map = new ConcurrentHashMap<>();

    public static <T extends RedisDataDict> T get(String k) {
        final RedisDataDict data = map.get(k);
        if (null == data) {
            return null;
        }
        if (data.isExpire()) {
            RedisDataRdb.del(k);
            return null;
        }
        return (T) data;
    }

    public static void put(String k, RedisDataDict data) {
        if (null != data) {
            map.put(k, data);
            RedisDataRdb.put(k, data);
        }
    }

    public static void del(String k) {
        map.remove(k);
        RedisDataRdb.del(k);
    }

    public static void expire(String k, int expireTime) {
        RedisDataDict data = map.get(k);
        if (null != data) {
            data.expire(expireTime);
        }
    }
}

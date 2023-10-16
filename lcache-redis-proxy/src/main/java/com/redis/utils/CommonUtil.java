package com.redis.utils;

import com.redis.core.command.data.RedisHash;

import java.util.Map;

public class CommonUtil {

    public static boolean isNotBlank(String s) {
        return null != s && !"".equals(s);
    }

    public static boolean isBlank(String s) {
        return !isNotBlank(s);
    }

    public static String getValue(Map<String, String> map, String key) {
        if (null != map) {
            return map.get(key);
        }
        return null;
    }

    public static String getValue(Map<String, RedisHash> map, String key, String key1) {
        if (null != map) {
            RedisHash data = map.get(key);
            if (null != data) {
                return data.getValue(key1);
            }
        }
        return null;
    }
}

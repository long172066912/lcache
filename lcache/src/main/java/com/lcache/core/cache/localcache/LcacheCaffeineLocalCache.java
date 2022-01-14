package com.lcache.core.cache.localcache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 本地缓存分布式下会有不一致情况，使用时请考虑60秒延迟是否能接受
 *
 * @author JerryLong
 * @version V1.0
 * @Title: LcacheCaffeineLocalCache
 * @Description: Guava方式的本地缓存
 * @date 2021/2/4 3:14 PM
 */
public class LcacheCaffeineLocalCache {
    /**
     * 本地缓存
     */
    public static Cache<String, Object> keyValueCache = Caffeine.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //最大值
            .maximumSize(10000)
            //在写入后开始计时，在指定的时间后过期。
            .expireAfterWrite(60, TimeUnit.SECONDS)
            //构建cache实例
            .build();

    /**
     * 获取缓存
     *
     * @param key
     * @param function
     * @return
     */
    public static Object get(String key, Function function) {
        return keyValueCache.get(key, function);
    }

    /**
     * 释放缓存
     *
     * @param key
     */
    public static void invalidate(String key) {
        keyValueCache.invalidate(key);
    }
}

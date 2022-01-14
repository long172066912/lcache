package com.lcache.spring;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LcacheManager
 * @Description: 自定义缓存管理类
 * @date 2021/12/8 4:38 下午
 */
public class LcacheManager extends AbstractCacheManager {

    private static Map<String, LcacheRedissonCache> caches = new ConcurrentHashMap<>(4);

    public static synchronized void addCaches(LcacheRedissonCache cache) {
        caches.putIfAbsent(cache.getName(), cache);
    }

    @Override
    public Cache getCache(String name) {
        return caches.get(name);
    }

    @Override
    protected Collection<? extends LcacheRedissonCache> loadCaches() {
        return this.caches.values();
    }
}

package com.lcache.spring.manager;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
@Component
public class LcacheManager extends AbstractCacheManager {

    private static Map<String, Cache> caches = new ConcurrentHashMap<>(4);

    public static final String LCACHE_SPRING_LOCAL_CACHE = "LcacheSpringLocalCache";

    private static LcacheSpringLocalCache LOCAL_CACHE = new LcacheSpringLocalCache();

    @Override
    @PostConstruct
    public void initializeCaches() {
        super.initializeCaches();
        caches.putIfAbsent(LCACHE_SPRING_LOCAL_CACHE, LOCAL_CACHE);
    }

    @Override
    public Cache getCache(String name) {
        //返回默认值，即CacheName可以不填
        return caches.getOrDefault(name, LOCAL_CACHE);
    }

    public static synchronized void addCaches(LcacheRedissonCache cache) {
        caches.putIfAbsent(cache.getName(), cache);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return caches.values();
    }
}

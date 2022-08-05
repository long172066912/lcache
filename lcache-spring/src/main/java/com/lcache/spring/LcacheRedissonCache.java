package com.lcache.spring;

import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.model.CacheDataBuilder;
import com.lcache.exception.CacheExceptionFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LcacheRedissonCache
 * @Description: cache实现类，应该使用data-redis，重写cacheWriter
 * @date 2021/12/8 3:21 下午
 */
@Configuration
public class LcacheRedissonCache implements Cache {

    private String cacheType;

    private BaseCacheExecutor executor;

    private static final String NULL_VALUE = "n";

    public LcacheRedissonCache() {
    }

    public LcacheRedissonCache(String name, BaseCacheExecutor executor) {
        this.cacheType = name;
        this.executor = executor;
    }

    @Override
    public String getName() {
        return this.cacheType;
    }

    public void setName(String name) {
        this.cacheType = name;
        this.executor = CacheClientFactory.getCacheExecutor(cacheType);
    }


    @Override
    public Object getNativeCache() {
        return null;
    }


    @Override
    public ValueWrapper get(Object key) {
        Object o = null;
        try {
            o = executor.getByRedissonMap(key);
        } catch (Exception e) {
            CacheExceptionFactory.addWarnLog("LcacheRedissonCache get error !", e, key.toString());
        }
        if (null == o || NULL_VALUE.equals(o)) {
            return null;
        }
        return new SimpleValueWrapper(o);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) executor.getByRedissonMap(key);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) executor.getCacheData(new CacheDataBuilder(key.toString(), e -> {
            ValueWrapper valueWrapper = this.get(key);
            if (null != valueWrapper) {
                return valueWrapper.get();
            }
            return null;
        }, e -> {
            Object data = null;
            try {
                data = valueLoader.call();
            } catch (Exception ex) {
                CacheExceptionFactory.addWarnLog("LcacheRedissonCache get load error !", ex, key.toString());
            }
            return data;
        }, data -> {
            this.put(key, data);
            return true;
        }));
    }


    @Override
    public void put(Object key, Object value) {
        executor.putByRedissonMap(key, null == value ? NULL_VALUE : value);
    }


    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return new SimpleValueWrapper(executor.putIfAbsentByRedissonMap(key, null == value ? NULL_VALUE : value));
    }


    @Override
    public void evict(Object key) {
        executor.removeByRedissonMap(key);
    }


    @Override
    public void clear() {
        //不支持
        CacheExceptionFactory.throwException("Lcache Spring Cache 不支持清空 !");
    }
}

package com.lcache.spring;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lcache.exception.CacheExceptionFactory;
import org.redisson.spring.cache.NullValue;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
* @Title: LcacheSpringLocalCache
* @Description: 本地缓存支持
* @author JerryLong
* @date 2022/1/21 5:59 PM
* @version V1.0
*/
@Configuration
public class LcacheSpringLocalCache implements Cache {

    /**
     * 本地缓存
     */
    public static com.github.benmanes.caffeine.cache.Cache<String, Object> LOCAL_CACHE = Caffeine.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //最大值
            .maximumSize(10000)
            //在写入后开始计时，在5分钟后过期。
            .expireAfterWrite(300, TimeUnit.SECONDS)
            //构建cache实例
            .build();

    public LcacheSpringLocalCache() {
    }

    @Override
    public String getName() {
        return "LSpringLocalCache";
    }

    @Override
    public Object getNativeCache() {
        return null;
    }


    @Override
    public ValueWrapper get(Object key) {
        Object o = null;
        try {
            o = LOCAL_CACHE.getIfPresent(key);
        } catch (Exception e) {
            CacheExceptionFactory.addWarnLog("LcacheRedissonCache get error !",e, key.toString());
        }
        if (null == o || o.getClass().getName().equals(NullValue.class.getName())) {
            return null;
        }
        return new SimpleValueWrapper(o);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) LOCAL_CACHE.getIfPresent(key);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) LOCAL_CACHE.get(key.toString(), e-> {
            try {
                return valueLoader.call();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        });
    }


    @Override
    public void put(Object key, Object value) {
        LOCAL_CACHE.put(key.toString(),value);
    }


    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return new SimpleValueWrapper(LOCAL_CACHE.get(key.toString(), e -> value));
    }


    @Override
    public void evict(Object key) {
        LOCAL_CACHE.invalidate(key);
    }


    @Override
    public void clear() {
       LOCAL_CACHE.invalidateAll();
    }
}

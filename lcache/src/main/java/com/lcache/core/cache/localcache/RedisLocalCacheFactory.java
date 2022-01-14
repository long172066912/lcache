package com.lcache.core.cache.localcache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.annotations.CommandsDataTypeUtil;
import com.lcache.core.cache.localcache.impl.DefaultLocalCacheHandle;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.constant.LocalCacheHandleTypeEnum;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.localcache.*;
import com.lcache.util.CacheConfigUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisLocalCacheFactory
 * @Description: 本地缓存工厂
 * @date 2021/8/24 5:20 PM
 */
public class RedisLocalCacheFactory {
    /**
     * 支持本地缓存设置最大数量
     */
    private static final int KEY_MAX_SIZE = 200;
    /**
     * 短暂的本地缓存key，只支持5.1秒
     */
    private static final int LOCAL_TRANSIENCE_KEY_MILLISECONDS = 5100;
    /**
     * 需要长期本地缓存的key列表
     */
    private static Map<String, Set<String>> longTimesKeys = new ConcurrentHashMap<>();
    /**
     * 短暂本地缓存的key前缀
     */
    private static final String TRANSIENCE_KEY_PREFIX = "hot-key-";
    /**
     * 数值1，需要推送
     */
    public static final int KEY_STATUS_NEED_PUBLISH = 1;
    /**
     * 数值2，已经推送
     */
    public static final int KEY_STATUS_ALREADY_PUBLISH = 2;
    /**
     * 热key本地缓存
     */
    private static Cache<String, Object> transienceLocalKeys = Caffeine.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            //最大值
            .maximumSize(2000)
            //在写入后开始计时，在指定的时间后过期。
            .expireAfterWrite(LOCAL_TRANSIENCE_KEY_MILLISECONDS, TimeUnit.MILLISECONDS)
            //构建cache实例
            .build();

    private static Map<CommandsDataTypeEnum, AbstractLocalCacheHandle> localCacheHandleMap = new ConcurrentHashMap<>();

    private static AbstractLocalCacheHandle defaultHandle = new DefaultLocalCacheHandle();

    static {
        localCacheHandleMap.put(CommandsDataTypeEnum.STRING, new StringLocalCacheHandle());
        localCacheHandleMap.put(CommandsDataTypeEnum.SET, new SetLocalCacheHandle());
        localCacheHandleMap.put(CommandsDataTypeEnum.HASH, new HashLocalCacheHandle());
        localCacheHandleMap.put(CommandsDataTypeEnum.LIST, new ListLocalCacheHandle());
        localCacheHandleMap.put(CommandsDataTypeEnum.ZSET, new ZsetLocalCacheHandle());
    }

    public static void registe(CommandsDataTypeEnum dataType, AbstractLocalCacheHandle handle) {
        localCacheHandleMap.put(dataType, handle);
    }

    /**
     * 本地缓存key发布订阅channel
     */
    public static final String LOCAL_CACHE_KEY_PUBSUB_CHANNEL = "cache2:local:channel:";

    /**
     * 获取本地缓存执行器
     *
     * @param dataType
     * @param executor
     * @return
     */
    public static AbstractLocalCacheHandle getLocalCacheHandle(CommandsDataTypeEnum dataType, BaseCacheExecutor executor) {
        return localCacheHandleMap.getOrDefault(dataType, defaultHandle);
    }

    /**
     * 添加本地缓存的key
     *
     * @param key
     */
    public static void addLocalCacheKey(BaseCacheExecutor executor, String key) {
        Set<String> sets = longTimesKeys.computeIfAbsent(CacheConfigUtils.modelToHashKey(executor.getCacheConfigModel()), e -> new HashSet<>());
        CacheExceptionFactory.throwException(sets.size() + 1 < KEY_MAX_SIZE, "本地缓存的key数量超出" + KEY_MAX_SIZE);
        sets.add(key);
    }


    /**
     * 添加本地缓存的key
     *
     * @param keys
     */
    public static void addLocalCacheKey(BaseCacheExecutor executor, Set<String> keys) {
        Set<String> sets = longTimesKeys.computeIfAbsent(CacheConfigUtils.modelToHashKey(executor.getCacheConfigModel()), e -> new HashSet<>());
        CacheExceptionFactory.throwException(sets.size() + keys.size() < KEY_MAX_SIZE, "本地缓存的key数量超出" + KEY_MAX_SIZE);
        sets.addAll(keys);
    }

    /**
     * 是否是本地缓存的key
     *
     * @param key
     * @return 1 需要推送，2 不需要推送
     */
    public static int isLocalCacheKey(BaseCacheExecutor executor, String key) {
        if (StringUtils.isBlank(key)) {
            return -1;
        }
        Set<String> sets = longTimesKeys.computeIfAbsent(CacheConfigUtils.modelToHashKey(executor.getCacheConfigModel()), e -> new HashSet<>());
        if (sets.contains(key)) {
            return KEY_STATUS_ALREADY_PUBLISH;
        }
        return (int) transienceLocalKeys.get(TRANSIENCE_KEY_PREFIX + key, e -> -1);
    }

    /**
     * 提交一个短暂本地缓存的key
     *
     * @param key
     * @param isNeedPublish 是否需要推送
     */
    public static void addTransienceLocalCacheKey(String key, boolean isNeedPublish) {
        transienceLocalKeys.put(TRANSIENCE_KEY_PREFIX + key, isNeedPublish ? KEY_STATUS_NEED_PUBLISH : KEY_STATUS_ALREADY_PUBLISH);
    }

    /**
     * 获取命令与key本地缓存执行策略
     *
     * @param commands
     * @param key
     * @return
     */
    public static RedisLocalCachePubHandle getRedisLocalCachePubHandle(BaseCacheExecutor executor, String commands, String key) {
        int isNeedPublish = isLocalCacheKey(executor, key);
        //不需要本地缓存的key，直接返回none
        if (isNeedPublish < 0) {
            return null;
        }
        return new RedisLocalCachePubHandle(isNeedPublish, CommandsDataTypeUtil.getHotKeyHandleType(commands));
    }

    /**
     * 本地缓存通知处理方式
     */
    public static class RedisLocalCachePubHandle {

        public RedisLocalCachePubHandle() {
        }

        public RedisLocalCachePubHandle(int isNeedPublish, LocalCacheHandleTypeEnum handleType) {
            this.isNeedPublish = isNeedPublish;
            this.handleType = handleType;
        }

        private int isNeedPublish;
        private LocalCacheHandleTypeEnum handleType;

        public int getIsNeedPublish() {
            return isNeedPublish;
        }

        public LocalCacheHandleTypeEnum getHandleType() {
            return handleType;
        }
    }
}

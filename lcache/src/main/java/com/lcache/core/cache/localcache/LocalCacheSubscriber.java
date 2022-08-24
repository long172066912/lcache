package com.lcache.core.cache.localcache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.model.HotKeySubscriptData;
import com.lcache.core.monitor.MonitorFactory;
import com.lcache.util.CacheConfigUtils;
import com.lcache.util.JsonUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LocalCacheSubscriber
 * @Description: 热key消息监听方
 * @date 2021/8/24 7:05 PM
 */
public class LocalCacheSubscriber {

    private static Map<String, Integer> subscribeInfo = new ConcurrentHashMap<>();

    /**
     * 增加监听
     *
     * @param executor
     */
    public static void addSubscriber(BaseCacheExecutor executor) {
        subscribeInfo.computeIfAbsent(CacheConfigUtils.modelToHashKeyNoUseType(executor.getCacheConfigModel()), e -> {
            executor.subscribe((message) -> manageMessage(JsonUtil.parseObject(message, HotKeySubscriptData.class)), RedisLocalCacheFactory.LOCAL_CACHE_KEY_PUBSUB_CHANNEL + executor.getCacheConfigModel().getCacheType());
            return 1;
        });
    }

    /**
     * 管理消息
     *
     * @param hotKeySubscriptData
     */
    public static void manageMessage(HotKeySubscriptData hotKeySubscriptData) {
        if (!MonitorFactory.isOpenHotKeyLocalCache()) {
            return;
        }
        //如果是当前pod发的消息，不处理
        if (hotKeySubscriptData.isLocalHost()) {
            return;
        }
        if (hotKeySubscriptData.isNewKey()) {
            //新的key，本地缓存中注册这个key
            RedisLocalCacheFactory.addTransienceLocalCacheKey(hotKeySubscriptData.getKey(), false);
        } else {
            //修改，需要删除本地缓存
            AbstractLocalCacheHandle.del(hotKeySubscriptData.getKey());
        }
    }
}

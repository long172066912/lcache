package com.lcache.core.cache.localcache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.model.HotKeySubscriptData;
import com.lcache.core.monitor.MonitorFactory;
import com.lcache.util.JsonUtil;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LocalCachePublisher
 * @Description: 热key消息发送方
 * @date 2021/8/24 7:06 PM
 */
public class LocalCachePublisher {

    /**
     * 通过pubsub通知其他节点
     *
     * @param executor
     * @param key
     * @param isNewKey true是新的key，false是修改的key
     */
    public static void publish(BaseCacheExecutor executor, String key, boolean isNewKey) {
        if (!MonitorFactory.isOpenHotKeyLocalCache()) {
            return;
        }
        if (null == executor) {
            return;
        }
        //通知其他pod
        executor.publishAsync(RedisLocalCacheFactory.LOCAL_CACHE_KEY_PUBSUB_CHANNEL + executor.getCacheConfigModel().getCacheType(), JsonUtil.toJSONString(new HotKeySubscriptData(key, isNewKey ? -1 : 1)));
    }
}

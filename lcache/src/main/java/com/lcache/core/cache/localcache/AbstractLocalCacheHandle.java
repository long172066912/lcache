package com.lcache.core.cache.localcache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.util.CacheConfigUtils;
import com.lcache.util.CacheFunction;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractLocalCacheHandle
 * @Description: 本地缓存执行器抽象类
 * @date 2021/11/9 2:34 PM
 */
public abstract class AbstractLocalCacheHandle {

    private static Cache<String, LocalCacheLifeCycle> LOCAL_CACHE = Caffeine.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(100)
            //最大值
            .maximumSize(10000)
            //在写入后开始计时，在指定的时间后过期。保留500毫秒
            .expireAfterWrite(500, TimeUnit.MILLISECONDS)
            //构建cache实例
            .build();

    /**
     * 获取，如果存在
     *
     * @param key
     * @return
     */
    public LocalCacheLifeCycle getIfPresent(BaseCacheExecutor executor, String key) {
        return LOCAL_CACHE.getIfPresent(getLocalKey(executor, key));
    }

    /**
     * 删除
     *
     * @param key
     */
    public static void del(String key) {
        LOCAL_CACHE.invalidate(key);
    }

    public static void del(BaseCacheExecutor executor, String key) {
        LOCAL_CACHE.invalidate(getLocalKey(executor.getCacheConfigModel(), key));
    }

    /**
     * 实现类需要指定自身数据类型
     *
     * @return
     */
    protected abstract CommandsDataTypeEnum getDataType();

    @PostConstruct
    public void regist() {
        RedisLocalCacheFactory.registe(getDataType(), this);
    }

    /**
     * 获取
     *
     * @param function
     * @param key
     * @param fields
     * @return
     */
    protected abstract Object get(BaseCacheExecutor executor, CacheFunction function, String key, Object[] fields);

    protected Object set(BaseCacheExecutor executor, CacheFunction function, String key) {
        try {
            return function.apply();
        } finally {
            //删除本地缓存
            LOCAL_CACHE.invalidate(key);
        }
    }

    /**
     * 缓存方法执行
     *
     * @param function
     * @param key
     * @param fields
     * @return
     */
    public Object doCacheFunc(BaseCacheExecutor executor, CacheFunction function, String key, Object[] fields) {
        if (executor.getCacheConfigModel().isLocalCache()) {
            //这一次命令是否不走本地缓存
            if (Boolean.TRUE.equals(executor.getNoLocalCacheOnce())) {
                try {
                    return function.apply();
                } finally {
                    executor.removeNoLocalCacheOnce();
                }
            }
            RedisLocalCacheFactory.RedisLocalCachePubHandle localCacheHandle = RedisLocalCacheFactory.getRedisLocalCachePubHandle(executor, function.fnToFnName(), key);
            if (null != localCacheHandle) {
                Object res = null;
                switch (localCacheHandle.getHandleType()) {
                    case NONE:
                        res = function.apply();
                        break;
                    case GET:
                        res = this.get(executor, function, key, fields);
                        //判断是否需要通知其他pod
                        if (localCacheHandle.getIsNeedPublish() == RedisLocalCacheFactory.KEY_STATUS_NEED_PUBLISH) {
                            //修改状态
                            RedisLocalCacheFactory.addTransienceLocalCacheKey(key, false);
                            LocalCachePublisher.publish(executor, key, true);
                        }
                        break;
                    case SET:
                        res = this.set(executor, function, getLocalKey(executor, key));
                        //通知其他pod，key是拼接后的
                        LocalCachePublisher.publish(executor, getLocalKey(executor, key), false);
                        break;
                    default:
                        res = function.apply();
                        break;
                }
                return res;
            }
        }
        return function.apply();
    }

    /**
     * 获取本地缓存的key
     *
     * @param key
     * @return
     */
    private String getLocalKey(BaseCacheExecutor executor, String key) {
        return getLocalKey(executor.getCacheConfigModel(), key);
    }

    protected static String getLocalKey(CacheConfigModel cacheConfigModel, String key) {
        return "l" + CacheConfigUtils.modelToHashKey(cacheConfigModel) + key;
    }

    /**
     * 获取cache
     *
     * @param key
     * @param check
     * @param loadData
     * @param <T>
     * @return
     */
    protected <T extends Object> Optional<LocalCacheLifeCycle<T>> getLocalCache(BaseCacheExecutor executor, String key, Function check, Function loadData) {
        key = getLocalKey(executor, key);
        @Nullable LocalCacheLifeCycle data = LOCAL_CACHE.getIfPresent(key);
        if (null == data) {
            loadAsync(key, check, loadData);
        }
        return Optional.ofNullable(data);
    }

    /**
     * 异步加载，如果在加载期间有修改，则放弃
     *
     * @param key
     */
    protected static void loadAsync(String key, Function checkLength, Function loadData) {
        CompletableFuture.runAsync(() -> {
            //判断是否能被加载
            LocalCacheLifeCycle cycle = LOCAL_CACHE.getIfPresent(key);
            if (!isCanLoad(cycle)) {
                return;
            }
            //判断长度
            if (null != checkLength) {
                if (!(boolean) checkLength.apply(null)) {
                    LOCAL_CACHE.put(key, new LocalCacheLifeCycle(LocalCacheStatus.CAN_NOT, null));
                    return;
                }
            }
            Object data = loadData.apply(null);
            //最后再判断一遍是否能被加载，并且判断版本号是否是最新的
            LocalCacheLifeCycle cycle1 = LOCAL_CACHE.getIfPresent(key);
            if (isCanLoad(cycle1)) {
                //判断版本号
                if (null != cycle && null != cycle1 && cycle1.getTimestamp() > cycle.getTimestamp()) {
                    return;
                }
                //缓存
                LOCAL_CACHE.put(key, new LocalCacheLifeCycle(LocalCacheStatus.CACHE, data));
            }
        });
    }

    /**
     * 是否可以被重新加载，
     * true : 初始化Null、已删除、已缓存
     *
     * @param cycle
     * @return
     */
    private static boolean isCanLoad(LocalCacheLifeCycle cycle) {
        return null == cycle || cycle.getLocalCacheStatus().equals(LocalCacheStatus.CACHE) || cycle.getLocalCacheStatus().equals(LocalCacheStatus.DEL);
    }

    /**
     * 本地缓存生命周期
     */
    public static class LocalCacheLifeCycle<T> {

        public LocalCacheLifeCycle() {
        }

        public LocalCacheLifeCycle(LocalCacheStatus localCacheStatus, T data) {
            this.localCacheStatus = localCacheStatus;
            this.timestamp = System.currentTimeMillis();
            this.data = data;
        }

        /**
         * 缓存状态
         */
        private LocalCacheStatus localCacheStatus;
        /**
         * 版本号，毫秒时间戳
         */
        private Long timestamp;
        /**
         * 缓存数据
         */
        private T data;

        public LocalCacheStatus getLocalCacheStatus() {
            return localCacheStatus;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public T getData() {
            return data;
        }
    }

    /**
     * key缓存状态
     */
    protected enum LocalCacheStatus {
        /**
         * 不能本地缓存
         */
        CAN_NOT,
        /**
         * 删除
         */
        DEL,
        /**
         * 加载中
         */
        LOADING,
        /**
         * 缓存中
         */
        CACHE;
    }
}

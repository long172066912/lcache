package com.lcache.extend.handle.localcache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.AbstractLocalCacheHandle;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.util.CacheFunction;

import java.util.Optional;
import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: SetLocalCacheHandle
 * @Description: set没有本地缓存的价值
 * @date 2021/11/9 2:36 PM
 */
public class SetLocalCacheHandle extends AbstractLocalCacheHandle {

    @Override
    protected CommandsDataTypeEnum getDataType() {
        return CommandsDataTypeEnum.SET;
    }

    private static final Integer SET_MAX_LEN = 5000;

    /**
     * 如果key不存在，需要先load
     *
     * @param function
     * @param fields
     * @return
     */
    @Override
    public Object get(BaseCacheExecutor executor, CacheFunction function, String key, Object[] fields) {
        return this.getCacheMap(executor, key).map(e -> {
            //判断缓存状态
            if (!e.getLocalCacheStatus().equals(LocalCacheStatus.CACHE)) {
                return function.apply();
            }
            switch (function.fnToFnName()) {
                case "sismember":
                    return e.getData().contains(fields[0]);
                case "smembers":
                    return e.getData();
                case "scard":
                    return Long.valueOf(e.getData().size());
                default:
                    return function.apply();
            }
        }).orElse(function.apply());
    }

    /**
     * 获取cache
     *
     * @param key
     * @return
     */
    private Optional<LocalCacheLifeCycle<Set<Object>>> getCacheMap(BaseCacheExecutor executor, String key) {
        return getLocalCache(executor, key, e -> executor.noLocalCacheOnce().scard(key) <= SET_MAX_LEN, e -> executor.noLocalCacheOnce().smembers(key));
    }
}

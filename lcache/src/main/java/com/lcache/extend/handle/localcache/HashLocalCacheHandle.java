package com.lcache.extend.handle.localcache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.AbstractLocalCacheHandle;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.util.CacheFunction;

import java.util.*;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HashLocalCacheHandle
 * @Description: hash类型本地缓存，超过500个value则不本地缓存；uid类型只能通过热key进入
 * @date 2021/11/9 2:35 PM
 */
public class HashLocalCacheHandle extends AbstractLocalCacheHandle {

    @Override
    protected CommandsDataTypeEnum getDataType() {
        return CommandsDataTypeEnum.HASH;
    }

    private static final Integer HASH_MAX_LEN = 5000;

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
                case "hget":
                    return e.getData().get(fields[0]);
                case "hmget":
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < fields.length; i++) {
                        list.add(e.getData().get(fields[i]));
                    }
                    return list;
                case "hmgetToMap":
                    Map<String, Object> map = new HashMap<>(8);
                    Object o = null;
                    for (int i = 0; i < fields.length; i++) {
                        o = e.getData().get(fields[i]);
                        if (null != o) {
                            map.put(fields[i].toString(), o);
                        }
                    }
                    return map;
                case "hmgetToMapCanNull":
                    Map<String, Object> map1 = new HashMap<>(8);
                    for (int i = 0; i < fields.length; i++) {
                        map1.put(fields[i].toString(), e.getData().get(fields[i]));
                    }
                    return map1;
                case "hgetAll":
                    return e.getData();
                case "hkeys":
                    return e.getData().keySet();
                case "hlen":
                    return Long.valueOf(e.getData().size());
                case "hvals":
                    return new ArrayList<>(e.getData().values());
                case "hexists":
                    return e.getData().containsKey(fields[0]);
                case "hstrlen":
                    return e.getData().get(fields[0]).toString().length();
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
    private Optional<LocalCacheLifeCycle<Map<String, Object>>> getCacheMap(BaseCacheExecutor executor, String key) {
        return getLocalCache(executor, key, e -> executor.noLocalCacheOnce().hlen(key) <= HASH_MAX_LEN, e -> executor.noLocalCacheOnce().hgetAll(key));
    }
}

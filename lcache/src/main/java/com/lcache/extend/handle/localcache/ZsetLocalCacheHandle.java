package com.lcache.extend.handle.localcache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.AbstractLocalCacheHandle;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.util.CacheFunction;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ZsetLocalCacheHandle
 * @Description: zset，不支持
 * @date 2021/11/9 2:36 PM
 */
public class ZsetLocalCacheHandle extends AbstractLocalCacheHandle {

    @Override
    protected CommandsDataTypeEnum getDataType() {
        return CommandsDataTypeEnum.ZSET;
    }

    @Override
    public Object get(BaseCacheExecutor executor, CacheFunction function, String key, Object[] fields) {
        return function.apply();
    }

    @Override
    public Object set(BaseCacheExecutor executor, CacheFunction function, String key) {
        return function.apply();
    }
}

package com.lcache.extend.handle.localcache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.AbstractLocalCacheHandle;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.util.CacheFunction;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ListLocalCacheHandle
 * @Description: list没有本地缓存的价值
 * @date 2021/11/9 2:36 PM
 */
public class ListLocalCacheHandle extends AbstractLocalCacheHandle {

    @Override
    protected CommandsDataTypeEnum getDataType() {
        return CommandsDataTypeEnum.LIST;
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

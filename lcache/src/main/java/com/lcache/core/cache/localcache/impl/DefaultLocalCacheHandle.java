package com.lcache.core.cache.localcache.impl;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.AbstractLocalCacheHandle;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.util.CacheFunction;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: DefaultLocalCacheHandle
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2021/11/10 3:02 PM
 */
public class DefaultLocalCacheHandle extends AbstractLocalCacheHandle {

    @Override
    protected CommandsDataTypeEnum getDataType() {
        return CommandsDataTypeEnum.OTHER;
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

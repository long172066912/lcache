package com.lcache.extend.handle.localcache;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.localcache.AbstractLocalCacheHandle;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.util.CacheFunction;
import org.apache.commons.lang3.StringUtils;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: StringLocalCacheHandle
 * @Description: 字符类型，get、set
 * @date 2021/11/9 2:30 PM
 */
public class StringLocalCacheHandle extends AbstractLocalCacheHandle {

    @Override
    protected CommandsDataTypeEnum getDataType() {
        return CommandsDataTypeEnum.STRING;
    }

    @Override
    public Object get(BaseCacheExecutor executor, CacheFunction function, String key, Object[] fields) {
        return this.getLocalCache(executor, key, null, e -> executor.noLocalCacheOnce().get(key)).map(
                e -> {
                    //判断缓存状态
                    if (!e.getLocalCacheStatus().equals(LocalCacheStatus.CACHE)) {
                        return function.apply();
                    }
                    switch (function.fnToFnName()) {
                        case "get":
                            return e.getData();
                        case "substr":
                            if (null == e.getData()) {
                                return "";
                            }
                            return substr(e.getData().toString(), Integer.parseInt(fields[0].toString()), Integer.parseInt(fields[1].toString()));
                        case "strlen":
                            if (null == e.getData()) {
                                return 0L;
                            }
                            return Long.parseLong(String.valueOf(e.getData().toString().length()));
                        default:
                            return function.apply();
                    }
                }
        ).orElse(function.apply());
    }

    public static String substr(String v, int s, int e) {
        if (StringUtils.isBlank(v)) {
            return "";
        }
        return v.substring(s, javaEnd(v.length(), e));
    }

    public static int javaEnd(int length, int end) {
        if (0 < end) {
            int l = end + 1;
            return l > length ? length : l;
        } else if (0 == end) {
            return 1;
        } else {
            int l = length + 1 + end;
            return l < 1 ? 1 : l;
        }
    }
}

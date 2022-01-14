package com.lcache.util;

import java.io.Serializable;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheFunction
 * @Description: 自定义方法
 * @date 2021/2/23 3:06 PM
 */
@FunctionalInterface
public interface CacheSetFunction extends Serializable {
    /**
     * 执行
     *
     * @param dbData
     * @return
     */
    Object apply(Object dbData);
}

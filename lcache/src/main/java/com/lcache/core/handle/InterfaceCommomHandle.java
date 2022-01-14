package com.lcache.core.handle;


import com.lcache.util.CacheFunction;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: InterfaceCommomHandle
 * @Description: 公告操作类接口
 * @date 2021/1/18 3:17 PM
 */
public interface InterfaceCommomHandle {
    /**
     * 执行命令
     *
     * @param function
     * @return
     */
    Object execute(CacheFunction function);

    /**
     * 执行命令
     *
     * @param function
     * @param key
     * @return
     */
    Object execute(CacheFunction function, String key);

    /**
     * 执行命令，支持本地缓存
     *
     * @param function
     * @param key
     * @param fields
     * @return
     */
    Object execute(CacheFunction function, String key, Object[] fields);

    /**
     * 执行命令
     *
     * @param function
     * @param keys
     * @return
     */
    Object execute(CacheFunction function, String[] keys);

    /**
     * 执行命令，支持本地缓存，字符串单key类型
     *
     * @param function
     * @param expireSeconds
     * @param key
     * @return
     */
    Object execute(CacheFunction function, int expireSeconds, String key);

    /**
     * 执行命令，支持本地缓存
     *
     * @param function
     * @param expireSeconds
     * @param key
     * @param fields
     * @return
     */
    Object execute(CacheFunction function, int expireSeconds, String key, Object[] fields);

    /**
     * 执行命令，不支持本地缓存
     *
     * @param function
     * @param expireSeconds
     * @param keys
     * @return
     */
    Object execute(CacheFunction function, int expireSeconds, String[] keys);
}

package com.lcache.core.cache.redis.commands;

import com.lcache.core.cache.LcacheCommands;
import com.lcache.core.cache.annotations.CommandsDataType;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedissonCommands
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2021/12/10 6:06 下午
 */
public interface RedissonCommands extends LcacheCommands {
    /**
     * 查询
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "getByRedissonMap")
    Object getByRedissonMap(Object key);

    /**
     * 写入
     *
     * @param key
     * @param value
     */
    @CommandsDataType(commands = "putByRedissonMap")
    void putByRedissonMap(Object key, Object value);

    /**
     * putIfAbsent
     *
     * @param key
     * @param value
     * @return
     */
    @CommandsDataType(commands = "putIfAbsentByRedissonMap")
    Object putIfAbsentByRedissonMap(Object key, Object value);

    /**
     * 删除
     *
     * @param key
     */
    @CommandsDataType(commands = "removeByRedissonMap")
    void removeByRedissonMap(Object key);
}

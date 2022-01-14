package com.lcache.core.cache.redis.commands;

import com.lcache.core.cache.LcacheCommands;
import com.lcache.core.cache.annotations.CommandsDataType;
import com.lcache.core.constant.CommandsDataTypeEnum;
import io.lettuce.core.RedisFuture;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisAsyncCommands
 * @Description: redis异步命令接口，只支持Lettuce
 * @date 2021/3/29 7:33 PM
 */
public interface RedisAsyncCommands extends LcacheCommands {

    /**
     * 异步publish
     *
     * @param channel
     * @param message
     * @return
     */
    @CommandsDataType(commands = "asyncPublish", dataType = CommandsDataTypeEnum.PUBSUB)
    RedisFuture<Long> publish(String channel, String message);

    /**
     * 异步自增
     *
     * @param key
     * @param expireSeconds
     * @return
     */
    @CommandsDataType(commands = "asyncIncr")
    RedisFuture<Long> incr(String key, int expireSeconds);

    /**
     * 异步设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "asyncExpire")
    RedisFuture<Boolean> expire(String key, long seconds);

    /**
     * 异步lua方式批量设置过期时间
     *
     * @param seconds
     * @param keys
     * @return Map<String, Boolean> json字符串
     */
    @CommandsDataType(commands = "asyncExpireBatch")
    RedisFuture<String> expireBatch(long seconds, String... keys);
}

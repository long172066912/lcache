package com.lcache.core.cache.redis.commands;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractRedisAsyncCommands
 * @Description: 异步命令抽象类
 * @date 2021/3/30 10:40 AM
 */
public abstract class AbstractRedisAsyncCommands implements RedisAsyncCommands {

    /**
     * 放入异步执行器
     *
     * @param asyncSource
     */
    public abstract void setAsyncExeutor(Object asyncSource);
}

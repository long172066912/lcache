package com.lcache.core.cache.redis.commands;

import com.lcache.core.cache.LcacheCommands;
import com.lcache.core.cache.annotations.CommandsDataType;
import com.lcache.core.cache.redis.model.LLock;
import com.lcache.core.constant.CommandsDataTypeEnum;

import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisLockCommands
 * @Description: 分布式锁接口
 * @date 2021/8/20 11:25 AM
 */
public interface RedisLockCommands extends LcacheCommands {
    /**
     * 加锁，内部通过Redisson实现
     *
     * @param name
     * @param leaseTime 在定义了leaseTime后，锁会自动释放。如果leaseTime为-1，保持锁定直到显式解锁
     * @param unit
     * @return
     */
    @CommandsDataType(commands = "lock", dataType = CommandsDataTypeEnum.LOCK)
    LLock lock(String name, long leaseTime, TimeUnit unit);

    /**
     * 有最大等待时间方式的加锁
     *
     * @param name
     * @param waitTime
     * @param leaseTime
     * @param unit
     * @return
     */
    @CommandsDataType(commands = "tryLock", dataType = CommandsDataTypeEnum.LOCK)
    LLock tryLock(String name, long waitTime, long leaseTime, TimeUnit unit);
}

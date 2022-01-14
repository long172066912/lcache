package com.lcache.extend.handle.redis.lettuce.commands;

import com.lcache.core.cache.redis.commands.AbstractRedisAsyncCommands;
import com.lcache.core.cache.redis.lua.RedisLuaScripts;
import com.lcache.extend.handle.redis.lettuce.AbstractLettuceHandleExecutor;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScriptOutputType;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LettuceRedisAsyncCommandsImpl
 * @Description: 异步命令实现
 * @date 2021/3/29 7:48 PM
 */
public class LettuceRedisAsyncCommandsImpl extends AbstractRedisAsyncCommands {

    private AbstractLettuceHandleExecutor asyncSource;

    @Override
    public void setAsyncExeutor(Object asyncSource) {
        this.asyncSource = (AbstractLettuceHandleExecutor) asyncSource;
    }

    @Override
    public RedisFuture<Long> publish(String channel, String message) {
        return (RedisFuture<Long>) asyncSource.execute(() -> asyncSource.async(asyncSource.getConnectResource()).publish(channel, message));
    }

    @Override
    public RedisFuture<Long> incr(String key, int seconds) {
        return (RedisFuture<Long>) asyncSource.execute(() -> (asyncSource.async(asyncSource.getConnectResource())).incr(key), seconds, key);
    }


    @Override
    public RedisFuture<Boolean> expire(String key, long seconds) {
        return (RedisFuture<Boolean>) asyncSource.execute(() -> asyncSource.async(asyncSource.getConnectResource()).expire(key, seconds));
    }

    @Override
    public RedisFuture<String> expireBatch(long seconds, String... keys) {
        return (RedisFuture<String>) asyncSource.execute(() ->
                asyncSource.async(asyncSource.getConnectResource()).evalsha(asyncSource.getLuaSha1(RedisLuaScripts.EXPIRE_BATCH), ScriptOutputType.VALUE, keys, new String[]{seconds + ""})
        );
    }
}

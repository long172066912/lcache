package com.redis.handler;

import com.redis.core.command.RedisData;
import com.redis.core.Resp;

public interface CommandHandler {
    /**
     * 处理命令
     *
     * @param redisData
     */
    Resp handle(RedisData redisData);
}
package com.redis.core.command;

import com.redis.core.Command;
import com.redis.core.Resp;

public interface CommonRedisCommands extends Command {
    Resp command();
    Resp hello();
    Resp expire(String k, Integer expireTime);
}

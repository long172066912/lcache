package com.redis.core.command;

import com.redis.core.Command;
import com.redis.resp.Resp;

public interface CommonRedisCommands extends Command {
    Resp command();
    Resp hello();
    Resp expire(String k, Integer expireTime);
}

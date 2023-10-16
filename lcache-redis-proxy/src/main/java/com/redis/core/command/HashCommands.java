package com.redis.core.command;

import com.redis.core.Command;
import com.redis.core.Resp;

public interface HashCommands extends Command {
    Resp hget(String k, String field);

    Resp hset(String k, String field, String v);
}

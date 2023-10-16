package com.redis.core.command;

import com.redis.core.Command;

public interface HashCommands extends Command {
    String hget(String k, String field);

    int hset(String k, String field, String v);
}

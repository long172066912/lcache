package com.redis.core.command;

import com.redis.core.Command;
import com.redis.core.Resp;

public interface StringCommands extends Command {
    Resp get(String k);

    Resp set(String k, String v);
}

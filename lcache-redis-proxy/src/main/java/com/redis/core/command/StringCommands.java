package com.redis.core.command;

import com.redis.core.Command;

public interface StringCommands extends Command {
    String get(String k);

    String set(String k, String v);
}

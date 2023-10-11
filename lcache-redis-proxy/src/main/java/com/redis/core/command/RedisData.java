package com.redis.core.command;

import com.redis.handler.codec.Resp;

import java.util.Arrays;

public class RedisData {
    private final String commandName;
    private final Resp[] content;

    public RedisData(String commandType, Resp[] content) {
        this.commandName = commandType;
        this.content = content;
    }

    public String getCommandName() {
        return commandName;
    }

    public Resp[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "RedisData{" +
                "commandName='" + commandName + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}

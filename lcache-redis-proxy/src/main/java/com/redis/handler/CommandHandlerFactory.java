package com.redis.handler;

import com.redis.core.CommandType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandHandlerFactory {
    static Map<String, Supplier<CommandHandler>> map = new HashMap<>();

    static {
        for (CommandHandlerType handlerType : CommandHandlerType.values()) {
            for (CommandType commandType : handlerType.getCommandTypes()) {
                map.put(commandType.getCommand(), handlerType.getSupplier());
            }
        }
    }

    public static CommandHandler getCommandHandler(String commandName) {
        final Supplier<CommandHandler> commandHandlerSupplier = map.get(commandName);
        if (null == commandHandlerSupplier) {
            return null;
        }
        return commandHandlerSupplier.get();
    }
}
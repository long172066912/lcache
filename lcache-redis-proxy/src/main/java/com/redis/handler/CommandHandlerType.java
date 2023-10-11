package com.redis.handler;

import com.redis.core.CommandType;
import com.redis.core.impl.CommandHandlerImpl;
import com.redis.core.impl.HashHandlerImpl;
import com.redis.core.impl.HelloHandlerImpl;
import com.redis.core.impl.StringHandlerImpl;

import java.util.function.Supplier;

import static com.redis.core.CommandType.*;

public enum CommandHandlerType {
    /**
     *
     */
    HANDLER_TYPE_COMMAND(new CommandType[]{COMMAND}, CommandHandlerImpl::new),
    HANDLER_TYPE_HELLO(new CommandType[]{HELLO}, HelloHandlerImpl::new),
    STRING(new CommandType[]{GET, SET}, StringHandlerImpl::new),
    HASH(new CommandType[]{HGET, HSET}, HashHandlerImpl::new),
    ;

    private final CommandType[] commandTypes;
    private final Supplier<CommandHandler> supplier;

    CommandHandlerType(CommandType[] commandTypes, Supplier supplier) {
        this.commandTypes = commandTypes;
        this.supplier = supplier;
    }

    public Supplier<CommandHandler> getSupplier() {
        return supplier;
    }

    public CommandType[] getCommandTypes() {
        return commandTypes;
    }
}
package com.redis.core;

import com.redis.core.command.impl.CommonRedisCommandsImpl;
import com.redis.core.command.impl.HashCommandsImpl;
import com.redis.core.command.impl.StringCommandsImpl;
import com.redis.core.resp.BulkString;
import com.redis.utils.RespUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisCommandHandlerFactory {

    private static Map<String, CommandModel> redisHandlerMap = new ConcurrentHashMap<>();
    private static Map<String, CommandArgResolver[]> argResolverMap = new ConcurrentHashMap<>();

    static {
        add(new CommonRedisCommandsImpl());
        add(new HashCommandsImpl());
        add(new StringCommandsImpl());
    }

    public static <T extends Command> void add(T commandHandler) {
        //循环类所有方法，写入table
        for (Method method : commandHandler.getClass().getMethods()) {
            redisHandlerMap.put(method.getName(), new CommandModel(commandHandler, method));
            if (method.getParameters().length > 0) {
                CommandArgResolver[] argResolvers = new CommandArgResolver[method.getParameters().length];
                for (int i = 0; i < method.getParameters().length; i++) {
                    final Parameter parameter = method.getParameters()[i];
                    CommandArgResolver resolver;
                    if (Integer.class.isAssignableFrom(parameter.getType()) || int.class.isAssignableFrom(parameter.getType())) {
                        resolver = RespUtil::toInt;
                    } else {
                        //其他一律按字符串处理
                        resolver = RespUtil::toString;
                    }
                    argResolvers[i] = resolver;
                }
                argResolverMap.put(method.getName(), argResolvers);
            }
        }
    }

    /**
     * 反射执行
     *
     * @param method
     * @param args
     * @return
     */
    public static Resp execute(String method, Resp[] args) {
        try {
            final CommandModel executor = redisHandlerMap.get(method);
            if (null == executor || null == executor.getMethod() || null == executor.getCommandHandler()) {
                return BulkString.NullBulkString;
            }
            //解析参数
            Object[] params = new Object[executor.getMethod().getParameters().length];
            final CommandArgResolver[] argResolvers = argResolverMap.get(method);
            if (null != argResolvers && argResolvers.length > 0) {
                for (int i = 1; i < args.length; i++) {
                    params[i - 1] = argResolvers[i - 1].resolver(args[i]);
                }
            }
            System.out.println("执行命令 : " + method + " ， 参数 ：" + params);
            return (Resp) executor.getMethod().invoke(executor.getCommandHandler(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return BulkString.NullBulkString;
        }
    }

    public interface CommandArgResolver<T> {
        T resolver(Resp resp);
    }


    private static class CommandModel {
        private Command commandHandler;
        private Method method;

        public CommandModel(Command commandHandler, Method method) {
            this.commandHandler = commandHandler;
            this.method = method;
        }

        public Command getCommandHandler() {
            return commandHandler;
        }

        public Method getMethod() {
            return method;
        }
    }
}

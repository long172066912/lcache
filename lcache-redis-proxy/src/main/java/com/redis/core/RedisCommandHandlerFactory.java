package com.redis.core;

import com.redis.core.command.impl.CommonRedisCommandsImpl;
import com.redis.core.command.impl.HashCommandsImpl;
import com.redis.core.command.impl.StringCommandsImpl;
import com.redis.resp.Resp;
import com.redis.resp.impl.BulkString;
import com.redis.resp.impl.RespInt;
import com.redis.resp.impl.SimpleString;
import com.redis.utils.RespUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisCommandHandlerFactory {

    private static Map<String, CommandModel> redisHandlerMap = new ConcurrentHashMap<>();
    private static Map<String, CommandArgResolver[]> argResolverMap = new ConcurrentHashMap<>();
    private static CommandResResolver defaultResResolver = response -> (Resp) response;
    private static CommandResResolver intResResolver = response -> new RespInt((int) response);
    private static CommandResResolver voidResResolver = response -> SimpleString.OK;
    private static CommandResResolver stringResResolver = response -> new SimpleString(null == response ? "" : response.toString());

    static {
        add(new CommonRedisCommandsImpl());
        add(new HashCommandsImpl());
        add(new StringCommandsImpl());
    }

    public static <T extends Command> void add(T commandHandler) {
        //循环类所有方法，写入table
        for (Method method : commandHandler.getClass().getMethods()) {
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
            CommandResResolver resResolver;
            if (Resp.class.isAssignableFrom(method.getReturnType())) {
                resResolver = defaultResResolver;
            } else if (Void.class.isAssignableFrom(method.getReturnType())) {
                resResolver = voidResResolver;
            } else if (Integer.class.isAssignableFrom(method.getReturnType()) || int.class.isAssignableFrom(method.getReturnType())) {
                resResolver = intResResolver;
            } else {
                //其他一律按字符串处理
                resResolver = stringResResolver;
            }
            redisHandlerMap.put(method.getName(), new CommandModel(commandHandler, method, resResolver));
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
            final Object response = executor.getMethod().invoke(executor.getCommandHandler(), params);
            System.out.println("执行命令 : " + method + " ， 参数 ：" + params.length + " , 结果 ：" + response);
            return executor.getResResolver().resolver(response);
        } catch (Exception e) {
            e.printStackTrace();
            return BulkString.NullBulkString;
        }
    }

    public interface CommandArgResolver<T> {
        T resolver(Resp resp);
    }

    public interface CommandResResolver {
        Resp resolver(Object response);
    }

    private static class CommandModel {
        private Command commandHandler;
        private Method method;
        private CommandResResolver resResolver;

        public CommandModel(Command commandHandler, Method method, CommandResResolver resResolver) {
            this.commandHandler = commandHandler;
            this.method = method;
            this.resResolver = resResolver;
        }

        public Command getCommandHandler() {
            return commandHandler;
        }

        public Method getMethod() {
            return method;
        }

        public CommandResResolver getResResolver() {
            return resResolver;
        }
    }
}

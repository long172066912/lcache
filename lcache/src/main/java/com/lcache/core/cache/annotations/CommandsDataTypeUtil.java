package com.lcache.core.cache.annotations;

import com.lcache.core.cache.redis.commands.RedisCommands;
import com.lcache.core.cache.redis.commands.RedisIntegrationCommands;
import com.lcache.core.cache.redis.commands.RedisLuaCommands;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.constant.LocalCacheHandleTypeEnum;
import com.lcache.exception.CacheExceptionFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CommandsDataTypeUtil
 * @Description: 接口数据类型
 * @date 2021/8/20 11:30 AM
 */
public class CommandsDataTypeUtil {

    /**
     * 命令数据类型集合
     */
    private static Map<String, CommandsDataTypeEnum> commandsDataTypes = new ConcurrentHashMap<>();

    /**
     * 热key处理方式集合
     */
    private static Map<String, LocalCacheHandleTypeEnum> hotKeyHashTypes = new ConcurrentHashMap<>();

    static {
        buildCommandsDataType(RedisCommands.class.getMethods());
        buildCommandsDataType(RedisIntegrationCommands.class.getMethods());
        buildCommandsDataType(RedisLuaCommands.class.getMethods());
    }

    /**
     * 构建接口数据类型
     *
     * @param methods
     */
    private static void buildCommandsDataType(Method[] methods) {
        //反射放入map中
        for (Method method : methods) {
            //判断属性是否标注了@CommandsDataType注解
            boolean methodHasAnno = method.isAnnotationPresent(CommandsDataType.class);
            if (methodHasAnno) {
                //获取CommandsDataType注解
                CommandsDataType commandsDataType = method.getAnnotation(CommandsDataType.class);
                commandsDataTypes.put(commandsDataType.commands(), commandsDataType.dataType());
                hotKeyHashTypes.put(commandsDataType.commands(), commandsDataType.localCacheHandleType());
            } else {
                CacheExceptionFactory.addWarnLog("RedisCommands commands:{} dataType is null !", method.getName());
            }
        }
    }

    /**
     * 获取数据结构类型
     *
     * @param commands
     * @return
     */
    public static CommandsDataTypeEnum getCommandsDataType(String commands) {
        return commandsDataTypes.getOrDefault(commands, CommandsDataTypeEnum.UNKNOWN);
    }

    /**
     * 获取热key本地缓存操作方式
     *
     * @param commands
     * @return
     */
    public static LocalCacheHandleTypeEnum getHotKeyHandleType(String commands) {
        return hotKeyHashTypes.getOrDefault(commands, LocalCacheHandleTypeEnum.NONE);
    }
}

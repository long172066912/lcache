package com.lcache.core.cache.redis.commands;

import com.lcache.core.cache.LcacheCommands;
import com.lcache.core.cache.annotations.CommandsDataType;
import com.lcache.core.cache.redis.lua.RedisLuaInterface;
import com.lcache.core.constant.CommandsDataTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisLuaCommands
 * @Description: lua封装的命令
 * @date 2021/8/20 11:24 AM
 */
public interface RedisLuaCommands extends LcacheCommands {
    /**
     * 缓存lua脚本
     */
    @CommandsDataType(commands = "loadLuaScripts", dataType = CommandsDataTypeEnum.OTHER)
    public void loadLuaScripts();

    /**
     * 执行lua脚本
     *
     * @param lua
     * @param keys
     * @param args
     * @return
     */
    Object executeByLua(RedisLuaInterface lua, List<String> keys, List<String> args);

    /**
     * 获取lua脚本缓存的值
     *
     * @param redisLuaScripts
     * @return
     */
    @CommandsDataType(commands = "getLuaSha1", dataType = CommandsDataTypeEnum.OTHER)
    String getLuaSha1(RedisLuaInterface redisLuaScripts);

    /**
     * zadd 如果key存在则添加
     *
     * @param key
     * @param score
     * @param member
     * @param seconds
     * @return
     */
    @CommandsDataType(commands = "zaddIfKeyExists", dataType = CommandsDataTypeEnum.ZSET)
    Long zaddIfKeyExists(String key, double score, String member, int seconds);

    /**
     * 批量zscore
     *
     * @param key
     * @param members
     * @return Map<member, score>
     */
    @CommandsDataType(commands = "zscoreBatch", dataType = CommandsDataTypeEnum.ZSET)
    Map<String, Double> zscoreBatch(String key, List<String> members);
}

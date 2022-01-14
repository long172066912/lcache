package com.lcache.core.cache.redis.lua;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ConnectTypeEnum
 * @Description: 连接方式枚举
 * @date 2021/1/20 5:43 PM
 */
public enum RedisLuaScripts implements RedisLuaInterface {
    /**
     * zadd，如果key存在则添加
     */
    ZADD_IF_EXISTS("if redis.call('exists', KEYS[1]) == 1 then return tostring(redis.call('zadd', KEYS[1], ARGV[1], ARGV[2])) else return '0' end"),
    /**
     * zscoreBatch ，批量zscore，只返回拿到的
     */
    ZSCORE_BATCH("local r = {} " +
            "for i, v in ipairs(ARGV) do " +
            "local zr = redis.call('ZSCORE', KEYS[1], v); if zr ~= false and zr ~= nil then r[v] = zr end;" +
            "end;" +
            "return cjson.encode(r);"),
    /**
     * expireBatch，批量设置过期时间
     */
    EXPIRE_BATCH("local r = {} " +
            "for i, v in ipairs(KEYS) do " +
            "r[v] = redis.call('EXPIRE', v, ARGV[1]);" +
            "end;" +
            "return cjson.encode(r);"),
    ;

    RedisLuaScripts(String scripts) {
        this.scripts = scripts;
    }

    /**
     * 操作类型
     */
    private String scripts;

    @Override
    public String getScripts() {
        return scripts;
    }

    /**
     * lua脚本列表
     */
    private static Set<RedisLuaInterface> luas = new HashSet<>();

    public static void addLua(RedisLuaInterface lua) {
        RedisLuaScripts.luas.add(lua);
    }

    public static void addLua(List<RedisLuaInterface> luas) {
        RedisLuaScripts.luas.addAll(luas);
    }

    public static Set<RedisLuaInterface> getRedisLuaScripts() {
        List<RedisLuaScripts> collect = Arrays.stream(RedisLuaScripts.values()).collect(Collectors.toList());
        RedisLuaScripts.luas.addAll(collect);
        return RedisLuaScripts.luas;
    }

}

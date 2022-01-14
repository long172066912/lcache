package com.lcache.core.cache.redis.lua;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisLuaInterface
 * @Description: Lua脚本接口
 * @date 2022/1/10 3:56 PM
 */
public interface RedisLuaInterface {
    /**
     * 获取lua脚本
     *
     * @return
     */
    String getScripts();
}

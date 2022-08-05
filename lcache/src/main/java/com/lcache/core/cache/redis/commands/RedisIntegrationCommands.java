package com.lcache.core.cache.redis.commands;

import com.lcache.core.cache.LcacheCommands;
import com.lcache.core.cache.annotations.CommandsDataType;
import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.constant.LocalCacheHandleTypeEnum;
import com.lcache.core.model.CacheDataBuilder;
import org.redisson.api.RLock;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisIntegrationCommands
 * @Description: 自定义集成封装命令
 * @date 2021/8/20 11:25 AM
 */
public interface RedisIntegrationCommands extends LcacheCommands {
    /**
     * 本地缓存，不存在则通过cache.get(key)获取，并存入本地缓存，缓存有效时间60s
     * 注：在使用本方法时，请注意以下几点：
     * 1、如果function中获取的值二次修改，分布式下本地缓存查询结果可能不一致
     * 2、key的数量太多，会导致本地缓存频繁执行淘汰策略，反例：localGetAndSet("uid:123:score");
     *
     * @param key
     * @return
     */
    @CommandsDataType(commands = "localGetAndSet")
    Object localGetAndSet(String key);

    /**
     * 本地缓存，不存在则通过function获取，并存入本地缓存，缓存有效时间60s
     * 注：在使用本方法时，请注意以下几点：
     * 1、如果function中获取的值二次修改，分布式下本地缓存查询结果可能不一致
     * 2、key的数量太多，会导致本地缓存频繁执行淘汰策略，反例：localGetAndSet("uid:123:score",cache.get("uid:123:score"));
     *
     * @param key
     * @param function
     * @return
     */
    @CommandsDataType(commands = "localGetAndSet")
    Object localGetAndSet(String key, Function function);

    /**
     * hmgetToMap
     *
     * @param key
     * @param fields
     * @return
     */
    @CommandsDataType(commands = "hmgetToMap", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Map<String, Object> hmgetToMap(String key, String[] fields);

    /**
     * hmgetToMapCanNull，值不存在则返回null
     *
     * @param key
     * @param fields
     * @return
     */
    @CommandsDataType(commands = "hmgetToMapCanNull", dataType = CommandsDataTypeEnum.HASH, localCacheHandleType = LocalCacheHandleTypeEnum.GET)
    Map<String, Object> hmgetToMapCanNull(String key, String[] fields);

    /**
     * 1、从缓存方法中拿数据
     * 2、没拿到从DB方法中获取
     * 3、放入缓存
     *
     * @param cacheDataBuilder
     * @return
     */
    @CommandsDataType(commands = "getCacheData", dataType = CommandsDataTypeEnum.OTHER)
    Object getCacheData(CacheDataBuilder cacheDataBuilder);
}

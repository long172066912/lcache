package com.lcache.config.model;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheShardConfig
 * @Description: shard配置
 * @date 2021/1/22 4:12 PM
 */
public class CacheShardConfig {
    /**
     * 是否散库
     */
    public static boolean isShard = false;
    /**
     * 散库值
     */
    public static String shardValue = "0";
}

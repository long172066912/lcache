package com.lcache.util;

import com.lcache.config.model.CommonCacheConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceClusterConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheConfigBuildUtils
 * @Description: 缓存配置构建帮助类
 * @date 2021/1/28 5:51 PM
 */
public class CacheConfigBuildUtils {

    public static ClientOptions getClientOptions(LettuceConnectSourceConfig redisSourceConfig) {
        return ClientOptions.builder()
                .autoReconnect(redisSourceConfig.isAutoReconnect())
                .pingBeforeActivateConnection(redisSourceConfig.isPingBeforeActivateConnection())
                .requestQueueSize(redisSourceConfig.getRequestQueueSize())
                .build();
    }

    public static ClusterClientOptions getClusterClientOptions(LettuceClusterConnectSourceConfig redisSourceConfig) {
        return ClusterClientOptions.builder()
                .autoReconnect(redisSourceConfig.isAutoReconnect())
                .maxRedirects(redisSourceConfig.getMaxRedirects())
                .validateClusterNodeMembership(redisSourceConfig.isValidateClusterNodeMembership())
                .build();
    }

    public static JedisPoolConfig getJedisPoolConfig(CommonCacheConfig redisSourceConfig) {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大空闲连接数,
        config.setMaxIdle(redisSourceConfig.getMaxIdle());
        //最大连接数,
        config.setMaxTotal(redisSourceConfig.getMaxTotal());
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(redisSourceConfig.getMaxWaitMillis());
        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        config.setMinEvictableIdleTimeMillis(redisSourceConfig.getMinEvictableIdleTimeMillis());
        //最小空闲连接数,
        config.setMinIdle(redisSourceConfig.getMinIdle());
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        config.setNumTestsPerEvictionRun(redisSourceConfig.getNumTestsPerEvictionRun());
        //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
        config.setSoftMinEvictableIdleTimeMillis(redisSourceConfig.getSoftMinEvictableIdleTimeMillis());
        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(redisSourceConfig.isTestOnBorrow());
        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(redisSourceConfig.isTestWhileIdle());
        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        config.setTimeBetweenEvictionRunsMillis(redisSourceConfig.getTimeBetweenEvictionRunsMillis());
        return config;
    }
}

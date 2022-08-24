package com.lcache.core.cache.redis.redisson;

import com.lcache.config.BaseCacheConfig;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.redis.jedis.config.JedisClusterConnectSourceConfig;
import com.lcache.extend.handle.redis.jedis.config.JedisConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceClusterConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import com.lcache.util.CacheConfigUtils;
import com.lcache.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.clients.jedis.HostAndPort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lcache.config.CacheBasicConfig.REDISSON_THREAD_NUM;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedissonClientManager
 * @Description: Redisson管理器
 * @date 2021/3/2 4:42 PM
 */
public class RedissonClientManager {

    /**
     * Redisson客户端
     */
    private static Map<String, RedissonClient> redissonMap = new ConcurrentHashMap<>();

    /**
     * 关闭Redisson连接
     *
     * @param cacheConfigModel
     */
    public static void close(CacheConfigModel cacheConfigModel) {
        String key = CacheConfigUtils.modelToHashKey(cacheConfigModel);
        if (null != redissonMap.get(key)) {
            RedissonClient redissonClient = redissonMap.get(key);
            redissonClient.shutdown();
            redissonMap.remove(redissonClient);
        }
    }

    /**
     * 获取Redisson客户端
     *
     * @param cacheConfigModel
     * @param cacheConfig
     * @return
     */
    public synchronized static RedissonClient getRedissonClient(CacheConfigModel cacheConfigModel, BaseCacheConfig cacheConfig) {
        RedissonClient redissonClient = null;
        String key = CacheConfigUtils.modelToHashKey(cacheConfigModel);
        if (null != redissonMap.get(key)) {
            redissonClient = redissonMap.get(key);
            if (!redissonClient.isShutdown()) {
                return redissonClient;
            }
        }
        Config config = getConfig(cacheConfigModel, cacheConfig);
        if (null == config) {
            CacheExceptionFactory.throwException("RedissonClientManager->getConfig error ! config is null !,cacheConfigModel:[{}],cacheConfig:[{}]", cacheConfigModel.toString() + JsonUtil.toJSONString(cacheConfig));
        }
        //设置线程数
        config.setThreads(REDISSON_THREAD_NUM);
        //设置Netty线程数
        config.setNettyThreads(REDISSON_THREAD_NUM);
        redissonClient = Redisson.create(config);
        redissonMap.put(key, redissonClient);
        return redissonClient;
    }

    /**
     * 配置转换
     *
     * @param cacheConfigModel
     * @param cacheConfig
     * @return
     */
    private static Config getConfig(CacheConfigModel cacheConfigModel, BaseCacheConfig cacheConfig) {
        switch (cacheConfigModel.getClientType()) {
            case RedisClientConstants.JEDIS:
                return getJedisRedissonConfig(cacheConfigModel, cacheConfig);
            case RedisClientConstants.LETTUCE:
                return getLettuceRedissonConfig(cacheConfigModel, cacheConfig);
            default:
                return null;
        }
    }

    /**
     * Jedis配置转Redisson配置
     *
     * @param cacheConfigModel
     * @param cacheConfig
     * @return
     */
    private static Config getJedisRedissonConfig(CacheConfigModel cacheConfigModel, BaseCacheConfig cacheConfig) {
        Config config = new Config();
        switch (cacheConfigModel.getConnectTypeEnum()) {
            case SIMPLE:
                JedisConnectSourceConfig simpleConfig = (JedisConnectSourceConfig) cacheConfig;
                config.useSingleServer()
                        .setAddress("redis://" + simpleConfig.getHost() + ":" + simpleConfig.getPort())
                        .setPassword(StringUtils.isNotBlank(simpleConfig.getPwd()) ? simpleConfig.getPwd() : null);
                break;
            case POOL:
                JedisConnectSourceConfig poolConfig = (JedisConnectSourceConfig) cacheConfig;
                config.useSingleServer()
                        .setAddress("redis://" + poolConfig.getHost() + ":" + poolConfig.getPort())
                        .setPassword(StringUtils.isNotBlank(poolConfig.getPwd()) ? poolConfig.getPwd() : null);
                break;
            case SHARDED:
                return null;
            case CLUSTER:
                JedisClusterConnectSourceConfig clusterConfig = (JedisClusterConnectSourceConfig) cacheConfig;
                for (HostAndPort hostAndPort : clusterConfig.getNodes()) {
                    config.useClusterServers().addNodeAddress("redis://" + hostAndPort.getHost() + ":" + hostAndPort.getPort());
                }
                config.useClusterServers()
                        // 集群状态扫描间隔时间，单位是毫秒
                        .setScanInterval(2000)
                        .setPassword(StringUtils.isNotBlank(clusterConfig.getPwd()) ? clusterConfig.getPwd() : null);
                break;
            case CLUSTER_POOL:
                JedisClusterConnectSourceConfig clusterPoolConfig = (JedisClusterConnectSourceConfig) cacheConfig;
                for (HostAndPort hostAndPort : clusterPoolConfig.getNodes()) {
                    config.useClusterServers().addNodeAddress("redis://" + hostAndPort.getHost() + ":" + hostAndPort.getPort());
                }
                config.useClusterServers()
                        // 集群状态扫描间隔时间，单位是毫秒
                        .setScanInterval(2000)
                        .setPassword(StringUtils.isNotBlank(clusterPoolConfig.getPwd()) ? clusterPoolConfig.getPwd() : null);
                break;
            default:
                return null;
        }
        return config;
    }

    /**
     * Lettuce配置转Redisson配置
     *
     * @param cacheConfigModel
     * @param cacheConfig
     * @return
     */
    private static Config getLettuceRedissonConfig(CacheConfigModel cacheConfigModel, BaseCacheConfig cacheConfig) {
        Config config = new Config();
        switch (cacheConfigModel.getConnectTypeEnum()) {
            case SIMPLE:
                LettuceConnectSourceConfig simpleConfig = (LettuceConnectSourceConfig) cacheConfig;
                config.useSingleServer()
                        .setAddress("redis://" + simpleConfig.getHost() + ":" + simpleConfig.getPort())
                        .setPassword(StringUtils.isNotBlank(simpleConfig.getPwd()) ? simpleConfig.getPwd() : null);
                break;
            case POOL:
                LettuceConnectSourceConfig poolConfig = (LettuceConnectSourceConfig) cacheConfig;
                config.useSingleServer()
                        .setAddress("redis://" + poolConfig.getHost() + ":" + poolConfig.getPort())
                        .setPassword(StringUtils.isNotBlank(poolConfig.getPwd()) ? poolConfig.getPwd() : null);
                break;
            case SHARDED:
                return null;
            case CLUSTER:
                LettuceClusterConnectSourceConfig clusterConfig = (LettuceClusterConnectSourceConfig) cacheConfig;
                String pwd = "";
                for (LettuceConnectSourceConfig hostAndPort : clusterConfig.getNodes()) {
                    config.useClusterServers().addNodeAddress("redis://" + hostAndPort.getHost() + ":" + hostAndPort.getPort());
                    pwd = hostAndPort.getPwd();
                }
                config.useClusterServers()
                        // 集群状态扫描间隔时间，单位是毫秒
                        .setScanInterval(2000)
                        .setPassword(StringUtils.isNotBlank(pwd) ? pwd : null);
                break;
            case CLUSTER_POOL:
                LettuceClusterConnectSourceConfig clusterPoolConfig = (LettuceClusterConnectSourceConfig) cacheConfig;
                String pwd1 = "";
                for (LettuceConnectSourceConfig hostAndPort : clusterPoolConfig.getNodes()) {
                    config.useClusterServers().addNodeAddress("redis://" + hostAndPort.getHost() + ":" + hostAndPort.getPort());
                    pwd1 = hostAndPort.getPwd();
                }
                config.useClusterServers()
                        // 集群状态扫描间隔时间，单位是毫秒
                        .setScanInterval(2000)
                        .setPassword(StringUtils.isNotBlank(pwd1) ? pwd1 : null);
                break;
            default:
                return null;
        }
        return config;
    }
}

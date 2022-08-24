package com.lcache.extend.handle.redis.lettuce.connect;

import com.lcache.config.InterfaceCacheConfig;
import com.lcache.connect.InterfaceConnectFactory;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.handle.AbstractConnectHandle;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.exception.CacheExceptionConstants;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.redis.lettuce.config.LettuceClusterConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import com.lcache.util.CacheConfigBuildUtils;
import com.lcache.util.JsonUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.metrics.CommandLatencyCollector;
import io.lettuce.core.metrics.CommandLatencyCollectorOptions;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LettuceConnectionFactory
 * @Description: Lettuce连接工厂
 * @date 2021/1/27 5:15 PM
 */
public class LettuceConnectionFactory extends AbstractConnectHandle implements InterfaceConnectFactory {


    @Override
    public int getClientType() {
        return RedisClientConstants.LETTUCE;
    }

    /**
     * 获取Lettuce连接
     *
     * @param redisSourceConfig
     * @param redisSourceConfig
     * @return
     */
    @Override
    public LettuceConnectResource getConnectionResource(InterfaceCacheConfig redisSourceConfig, CacheConfigModel cacheConfigModel) {
        try {
            switch (cacheConfigModel.getConnectTypeEnum()) {
                case SIMPLE:
                    return new LettuceConnectResource().setStatefulRedisConnection(this.getLettuceConnection((LettuceConnectSourceConfig) redisSourceConfig));
                case POOL:
                    return new LettuceConnectResource().setGenericObjectPool(this.getLettuceConnectionByPool((LettuceConnectSourceConfig) redisSourceConfig));
                case SHARDED:
                    return null;
                case CLUSTER:
                    return new LettuceConnectResource().setStatefulRedisClusterConnection(this.getLettuceClusterConnection((LettuceClusterConnectSourceConfig) redisSourceConfig));
                case CLUSTER_POOL:
                    return new LettuceConnectResource().setGenericObjectPool(this.getLettuceClusterPoolConnection((LettuceClusterConnectSourceConfig) redisSourceConfig));
                default:
                    return null;
            }
        } catch (Exception e) {
            CacheExceptionFactory.throwException(CacheExceptionConstants.CACHE_ERROR_CODE, "RedisConnectionManager->getLettuceConnectionResource", JsonUtil.toJSONString(cacheConfigModel), e);
            return null;
        }
    }

    /**
     * 获取Lettuce连接
     *
     * @param redisSourceConfig
     * @return
     */
    public StatefulRedisConnection<String, String> getLettuceConnection(LettuceConnectSourceConfig redisSourceConfig) {
        //设置线程
        DefaultClientResources res = this.getDefaultClientResources(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getCommonCacheConfig().getIoThreadPoolSize(), redisSourceConfig.getCommonCacheConfig().getComputationThreadPoolSize());
        //构建连接
        RedisURI uri = this.getRedisUri(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getPwd(), redisSourceConfig.getDatabase(), redisSourceConfig.getTimeout());
        //创建资源对象
        RedisClient client = RedisClient.create(res, uri);
        client.setOptions(CacheConfigBuildUtils.getClientOptions(redisSourceConfig));
        client.setDefaultTimeout(Duration.ofMillis(redisSourceConfig.getSoTimeout()));
        return (StatefulRedisConnection<String, String>) this.execute(() -> {
            return client.connect();
        });
    }

    /**
     * 获取Simple方式发布订阅连接
     *
     * @param redisSourceConfig
     * @return
     */
    public StatefulRedisPubSubConnection<String, String> getLettucePubSubConnection(LettuceConnectSourceConfig redisSourceConfig) {
        //设置线程
        DefaultClientResources res = this.getDefaultClientResources(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getCommonCacheConfig().getIoThreadPoolSize(), redisSourceConfig.getCommonCacheConfig().getComputationThreadPoolSize());
        RedisClient client = RedisClient.create(res, this.getRedisUri(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getPwd(), redisSourceConfig.getDatabase(), redisSourceConfig.getTimeout()));
        client.setOptions(CacheConfigBuildUtils.getClientOptions(redisSourceConfig));
        client.setDefaultTimeout(Duration.ofMillis(redisSourceConfig.getSoTimeout()));
        return (StatefulRedisPubSubConnection<String, String>) this.execute(() -> {
            return client.connectPubSub();
        });
    }

    /**
     * 获取Lettuce连接池
     *
     * @param redisSourceConfig
     * @return
     */
    public GenericObjectPool getLettuceConnectionByPool(LettuceConnectSourceConfig redisSourceConfig) {
        //设置线程
        DefaultClientResources res = this.getDefaultClientResources(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getCommonCacheConfig().getIoThreadPoolSize(), redisSourceConfig.getCommonCacheConfig().getComputationThreadPoolSize());
        RedisClient client = RedisClient.create(res, this.getRedisUri(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getPwd(), redisSourceConfig.getDatabase(), redisSourceConfig.getTimeout()));
        client.setOptions(CacheConfigBuildUtils.getClientOptions(redisSourceConfig));
        client.setDefaultTimeout(Duration.ofMillis(redisSourceConfig.getSoTimeout()));
        return (GenericObjectPool) this.execute(() -> {
            return ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), CacheConfigBuildUtils.getJedisPoolConfig(redisSourceConfig.getCommonCacheConfig()));
        });
    }

    /**
     * 获取Lettuce集群
     *
     * @param lettuceClusterConnectSourceConfig
     * @return
     */
    public StatefulRedisClusterConnection<String, String> getLettuceClusterConnection(LettuceClusterConnectSourceConfig lettuceClusterConnectSourceConfig) {
        //设置线程
        DefaultClientResources res = this.getDefaultClientResources(lettuceClusterConnectSourceConfig.getHosts().toString(), 0, lettuceClusterConnectSourceConfig.getCommonCacheConfig().getIoThreadPoolSize(), lettuceClusterConnectSourceConfig.getCommonCacheConfig().getComputationThreadPoolSize());
        List<RedisURI> nodeConfigs = new ArrayList<>(lettuceClusterConnectSourceConfig.getNodes().size());
        for (LettuceConnectSourceConfig redisSourceConfig : lettuceClusterConnectSourceConfig.getNodes()) {
            nodeConfigs.add(this.getRedisUri(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getPwd(), redisSourceConfig.getDatabase(), redisSourceConfig.getTimeout()));
        }
        RedisClusterClient client = RedisClusterClient.create(res, nodeConfigs);
        client.setDefaultTimeout(Duration.ofMillis(lettuceClusterConnectSourceConfig.getSoTimeout()));
        client.setOptions(CacheConfigBuildUtils.getClusterClientOptions(lettuceClusterConnectSourceConfig));
        return (StatefulRedisClusterConnection<String, String>) this.execute(() -> {
            return client.connect();
        });
    }

    /**
     * 获取集群方式发布订阅连接
     *
     * @param lettuceClusterConnectSourceConfig
     * @return
     */
    public StatefulRedisClusterPubSubConnection<String, String> getLettuceClusterPubSubConnection(LettuceClusterConnectSourceConfig lettuceClusterConnectSourceConfig) {
        //设置线程
        DefaultClientResources res = this.getDefaultClientResources(lettuceClusterConnectSourceConfig.getHosts().toString(), 0, lettuceClusterConnectSourceConfig.getCommonCacheConfig().getIoThreadPoolSize(), lettuceClusterConnectSourceConfig.getCommonCacheConfig().getComputationThreadPoolSize());
        List<RedisURI> nodeConfigs = new ArrayList<>(lettuceClusterConnectSourceConfig.getNodes().size());
        for (LettuceConnectSourceConfig redisSourceConfig : lettuceClusterConnectSourceConfig.getNodes()) {
            nodeConfigs.add(this.getRedisUri(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getPwd(), redisSourceConfig.getDatabase(), redisSourceConfig.getTimeout()));
        }
        RedisClusterClient client = RedisClusterClient.create(res, nodeConfigs);
        client.setDefaultTimeout(Duration.ofMillis(lettuceClusterConnectSourceConfig.getSoTimeout()));
        client.setOptions(CacheConfigBuildUtils.getClusterClientOptions(lettuceClusterConnectSourceConfig));
        return (StatefulRedisClusterPubSubConnection<String, String>) this.execute(() -> {
            return client.connectPubSub();
        });
    }

    /**
     * 获取Lettuce集群连接池
     *
     * @param lettuceClusterConnectSourceConfig
     * @return
     */
    public GenericObjectPool getLettuceClusterPoolConnection(LettuceClusterConnectSourceConfig lettuceClusterConnectSourceConfig) {
        //设置线程
        DefaultClientResources res = this.getDefaultClientResources(lettuceClusterConnectSourceConfig.getHosts().toString(), 0, lettuceClusterConnectSourceConfig.getCommonCacheConfig().getIoThreadPoolSize(), lettuceClusterConnectSourceConfig.getCommonCacheConfig().getComputationThreadPoolSize());
        List<RedisURI> nodeConfigs = new ArrayList<>(lettuceClusterConnectSourceConfig.getNodes().size());
        for (LettuceConnectSourceConfig redisSourceConfig : lettuceClusterConnectSourceConfig.getNodes()) {
            nodeConfigs.add(this.getRedisUri(redisSourceConfig.getHost(), redisSourceConfig.getPort(), redisSourceConfig.getPwd(), redisSourceConfig.getDatabase(), redisSourceConfig.getTimeout()));
        }
        RedisClusterClient client = RedisClusterClient.create(res, nodeConfigs);
        client.setDefaultTimeout(Duration.ofMillis(lettuceClusterConnectSourceConfig.getSoTimeout()));
        client.setOptions(CacheConfigBuildUtils.getClusterClientOptions(lettuceClusterConnectSourceConfig));
        return (GenericObjectPool) this.execute(() -> {
            return ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(), CacheConfigBuildUtils.getJedisPoolConfig(lettuceClusterConnectSourceConfig.getCommonCacheConfig()));
        });
    }

    private static Map<String, DefaultClientResources> defaultClientResources = new ConcurrentHashMap<>();

    /**
     * 设置线程
     *
     * @param ioThreadPoolSize
     * @param computationThreadPoolSize
     * @return
     */
    private synchronized DefaultClientResources getDefaultClientResources(String host, int port, int ioThreadPoolSize, int computationThreadPoolSize) {
        return defaultClientResources.computeIfAbsent(host + port
                , e -> DefaultClientResources.builder()
                        .ioThreadPoolSize(ioThreadPoolSize)
                        .computationThreadPoolSize(computationThreadPoolSize)
                        .commandLatencyRecorder(CommandLatencyCollector.create(CommandLatencyCollectorOptions.disabled()))
                        .build());
    }

    /**
     * 初始化连接对象
     *
     * @param host
     * @param port
     * @param pwd
     * @param database
     * @param timeout
     * @return
     */
    private RedisURI getRedisUri(String host, int port, String pwd, int database, int timeout) {
        RedisURI build = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withDatabase(database)
                .withTimeout(Duration.ofMillis(timeout)).build();
        if (StringUtils.isNotBlank(pwd)) {
            build.setPassword(pwd.toCharArray());
        }
        return build;
    }
}

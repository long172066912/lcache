package com.lcache.extend.handle.redis.jedis;

import com.lcache.connect.ConnectResource;
import com.lcache.core.InterfaceCacheExecutor;
import com.lcache.core.cache.redis.commands.RedisAsyncCommands;
import com.lcache.core.cache.redis.lua.RedisLuaScripts;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.handle.AbstractCacheHandle;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.pipeline.PipelineCmd;
import com.lcache.extend.handle.redis.jedis.connect.JedisConnectResource;
import com.lcache.extend.handle.redis.jedis.pipeline.ClusterJedisPipelineExecutor;
import com.lcache.extend.handle.redis.jedis.pipeline.JedisPipelineExecutor;
import com.lcache.extend.handle.redis.jedis.pipeline.ShardJedisPipelineExecutor;
import com.lcache.util.CacheCommonUtils;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ShardedJedis;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractLettuceHandleExecutor
 * @Description: Jedis执行器
 * @date 2021/1/19 2:33 PM
 */
public abstract class AbstractJedisHandleExecutor extends AbstractCacheHandle implements InterfaceCacheExecutor {

    /**
     * 连接资源
     */
    private ConnectResource connectResource;

    private ThreadLocal<JedisCommands> resource = new ThreadLocal<>();

    @Override
    public void setConnectionResource(ConnectResource connectResource) {
        if (null == connectResource) {
            CacheExceptionFactory.throwException("AbstractJedisHandleExecutor->setConnectionResource connectResource is null !");
        }
        this.connectResource = connectResource;
    }

    @Override
    public JedisCommands getConnectResource() {
        if (this.getCacheConfigModel().isRetry()) {
            this.returnConnectResource();
            this.getCacheConfigModel().setRetry(false);
        }
        JedisCommands jedisCommands = null;
        //获取乐观读锁
        long stamp = connectResource.getStampedLock().tryOptimisticRead();
        jedisCommands = getConnectResourceNoLock();
        //判断是否需要加悲观读锁
        if (!connectResource.getStampedLock().validate(stamp)) {
            stamp = connectResource.getStampedLock().readLock();
            try {
                jedisCommands = getConnectResourceNoLock();
            } finally {
                connectResource.getStampedLock().unlockRead(stamp);
            }
        }
        return jedisCommands;
    }

    private boolean luaStatus = false;

    @Override
    public void loadLuaScripts() {
        if (!luaStatus) {
            luaStatus = true;
            try {
                RedisLuaScripts.getRedisLuaScripts().stream().forEach(e -> this.getLuaLoadsInfo().put(e, this.scriptLoad(e.getScripts())));
            } catch (Exception e) {
                CacheExceptionFactory.addErrorLog("Jedis loadLuaScripts fail", e);
            }
            luaStatus = false;
        }
    }

    private JedisCommands getConnectResourceNoLock() {
        JedisCommands localResource = resource.get();
        if (null != localResource) {
            return localResource;
        }
        JedisConnectResource connectResource = (JedisConnectResource) this.connectResource.getResource();
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                localResource = connectResource.getShardedJedis();
                break;
            case CLUSTER:
                localResource = connectResource.getJedisCluster();
                break;
            case POOL:
                try {
                    localResource = connectResource.getJedisPool().getResource();
                } catch (Exception e) {
                    CacheExceptionFactory.addErrorLog("AbstractJedisHandleExecutor->getJedisPool error once !", e);
                    try {
                        localResource = connectResource.getJedisPool().getResource();
                    } catch (Exception e1) {
                        CacheExceptionFactory.addErrorLog("AbstractJedisHandleExecutor->getJedisPool error twice !", e1);
                        throw e1;
                    }
                }
                break;
            default:
                localResource = connectResource.getJedis();
                break;
        }
        resource.set(localResource);
        return localResource;
    }

    @Override
    public Object getPool() {
        return this.getConnectResource();
    }

    @Override
    public void returnConnectResource() {
        //释放资源
        try {
            JedisCommands localResource = resource.get();
            if (null != localResource) {
                switch (this.getCacheConfigModel().getConnectTypeEnum()) {
                    case SIMPLE:
                        ((Jedis) localResource).close();
                        break;
                    case POOL:
                        ((Jedis) localResource).close();
                        break;
                    case SHARDED:
                        ((ShardedJedis) localResource).close();
                        break;
                    case CLUSTER:
                        ((JedisCluster) localResource).close();
                        break;
                    default:
                        break;
                }
                localResource = null;
                resource.remove();
            }
        } catch (Exception e) {
            CacheExceptionFactory.addErrorLog("AbstractJedisHandleExecutor", "returnConnectResource", "resourceReturn error", e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public int getClientType() {
        return RedisClientConstants.JEDIS;
    }

    @Override
    public RedisAsyncCommands async() {
        CacheExceptionFactory.throwException("Jedis->async 不支持!");
        return null;
    }

    @Override
    public RedisClusterAsyncCommands asyncL() {
        CacheExceptionFactory.throwException("Jedis->asyncL 不支持!");
        return null;
    }


    @Override
    public void expireAsync(int expireSeconds, String... keys) {
        if (expireSeconds > 0 && keys.length > 0) {
            if (keys.length == 1) {
                CompletableFuture.runAsync(() -> this.expire(keys[0], expireSeconds));
            } else {
                this.pAsync(CacheCommonUtils.getPipelineExpires(Arrays.stream(keys).collect(Collectors.toSet()), expireSeconds));
            }
        }
    }

    @Override
    public List<Object> pSync(PipelineCmd[] commands) {
        return this.pSync(Arrays.asList(commands));
    }

    @Override
    public CompletableFuture<List<Object>> pAsync(PipelineCmd[] commands) {
        return this.pAsync(Arrays.asList(commands));
    }

    @Override
    public CompletableFuture<List<Object>> pAsync(List<PipelineCmd> commands) {
        return CompletableFuture.supplyAsync(() -> {
            return this.pSync(commands);
        });
    }

    @Override
    public List<Object> pSync(List<PipelineCmd> commands) {
        JedisConnectResource connectResource = (JedisConnectResource) this.connectResource.getResource();
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                ShardedJedis shardedJedis = connectResource.getShardedJedis();
                try {
                    return new ShardJedisPipelineExecutor(shardedJedis.pipelined()).pSync(commands);
                } finally {
                    if (null != shardedJedis) {
                        try {
                            shardedJedis.close();
                        } catch (Exception e) {
                            CacheExceptionFactory.addErrorLog("AbstractJedisHandleExecutor->pSync ShardedJedis close fail !", e);
                        }
                    }
                }
            case CLUSTER:
                JedisCluster jedisCluster = connectResource.getJedisCluster();
                try {
                    return new ClusterJedisPipelineExecutor(jedisCluster).pSync(commands);
                } finally {
                    if (null != jedisCluster) {
                        try {
                            jedisCluster.close();
                        } catch (Exception e) {
                            CacheExceptionFactory.addErrorLog("AbstractJedisHandleExecutor->pSync JedisCluster close fail !", e);
                        }
                    }
                }
            case POOL:
                Jedis pool = connectResource.getJedisPool().getResource();
                try {
                    return new JedisPipelineExecutor(pool.pipelined()).pSync(commands);
                } finally {
                    if (null != pool) {
                        try {
                            pool.close();
                        } catch (Exception e) {
                            CacheExceptionFactory.addErrorLog("AbstractJedisHandleExecutor->pSync JedisPool close fail !", e);
                        }
                    }
                }
            default:
                Jedis jedis = connectResource.getJedis();
                try {
                    return new JedisPipelineExecutor(jedis.pipelined()).pSync(commands);
                } finally {
                    if (null != jedis) {
                        try {
                            jedis.close();
                        } catch (Exception e) {
                            CacheExceptionFactory.addErrorLog("AbstractJedisHandleExecutor->pSync Jedis close fail !", e);
                        }
                    }
                }
        }
    }
}

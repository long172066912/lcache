package com.lcache.extend.handle.redis.lettuce;

import com.lcache.connect.ConnectResource;
import com.lcache.core.InterfaceCacheExecutor;
import com.lcache.core.cache.redis.commands.AbstractRedisAsyncCommands;
import com.lcache.core.cache.redis.lua.RedisLuaScripts;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.constant.UseTypeEnum;
import com.lcache.core.handle.AbstractCacheHandle;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.exception.CacheExceptionConstants;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.pipeline.PipelineCmd;
import com.lcache.extend.handle.redis.lettuce.commands.LettuceRedisAsyncCommandsImpl;
import com.lcache.extend.handle.redis.lettuce.config.LettuceClusterConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import com.lcache.extend.handle.redis.lettuce.connect.LettuceConnectResource;
import com.lcache.extend.handle.redis.lettuce.connect.LettuceConnectionFactory;
import com.lcache.extend.handle.redis.lettuce.pipeline.LettucePipelineExecutor;
import com.lcache.extend.handle.redis.lettuce.proxy.LettuceAsyncProxy;
import com.lcache.util.BeanFactory;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractLettuceHandleExecutor
 * @Description: Lettuce命令执行器
 * @date 2021/1/19 2:33 PM
 */
public abstract class AbstractLettuceHandleExecutor extends AbstractCacheHandle implements InterfaceCacheExecutor {

    protected static LettuceConnectionFactory lettuceConnectionFactory = BeanFactory.get(LettuceConnectionFactory.class);

    private static LettuceRedisAsyncCommandsImpl lettuceRedisAsyncCommands = BeanFactory.get(LettuceRedisAsyncCommandsImpl.class);

    @Override
    public int getClientType() {
        return RedisClientConstants.LETTUCE;
    }

    /**
     * 链接资源
     */
    private ConnectResource connectResource;

    /**
     * 管道链接资源
     */
    private LettuceConnectResource pipelineConnectResource;
    /**
     * 资源缓存
     */
    private ThreadLocal<StatefulConnection> statefulConnection = new ThreadLocal<>();

    /**
     * 放入资源
     *
     * @param connectResource
     */
    @Override
    public void setConnectionResource(ConnectResource connectResource) {
        if (null == connectResource) {
            CacheExceptionFactory.throwException("AbstractLettuceHandleExecutor->setConnectionResource connectResource is null !");
        }
        this.connectResource = connectResource;
    }

    /**
     * 获取资源
     *
     * @return
     */
    @Override
    public StatefulConnection getConnectResource() {
        StatefulConnection statefulConnection = null;
        long stamp = connectResource.getStampedLock().tryOptimisticRead();
        statefulConnection = this.getStatefulConnection();
        //判断是否需要加悲观读锁
        if (!connectResource.getStampedLock().validate(stamp)) {
            stamp = connectResource.getStampedLock().readLock();
            try {
                statefulConnection = this.getStatefulConnection();
            } finally {
                //释放读锁
                connectResource.getStampedLock().unlockRead(stamp);
            }
        }
        return statefulConnection;
    }

    @Override
    public void loadLuaScripts() {
        /**
         * 加写锁
         */
        long writeLock = connectResource.getStampedLock().writeLock();
        try {
            RedisLuaScripts.getRedisLuaScripts().stream().forEach(e -> this.getLuaLoadsInfo().put(e, this.sync(this.getStatefulConnection()).scriptLoad(e.getScripts())));
        } catch (Exception e) {
            CacheExceptionFactory.addErrorLog("Lettuce loadLuaScripts fail", e);
        } finally {
            this.returnConnectResource();
            connectResource.getStampedLock().unlockWrite(writeLock);
        }
    }

    private StatefulConnection getStatefulConnection() {
        StatefulConnection localResource = statefulConnection.get();
        if (null != localResource) {
            return localResource;
        }
        localResource = this.getStatefulConnection(this.connectResource);
        statefulConnection.set(localResource);
        return localResource;
    }

    private StatefulConnection getStatefulConnection(ConnectResource connectResource) {
        try {
            LettuceConnectResource resource = (LettuceConnectResource) this.connectResource.getResource();
            switch (this.getCacheConfigModel().getConnectTypeEnum()) {
                case SHARDED:
                    CacheExceptionFactory.throwException("Lettuce 暂不支持Shard方式");
                    return null;
                case POOL:
                    try {
                        return ((StatefulRedisConnection) resource.getGenericObjectPool().borrowObject(2000L));
                    } catch (Exception e) {
                        CacheExceptionFactory.addErrorLog("AbstractLettuceHandleExecutor->getLettucePool error once !", e);
                        try {
                            return ((StatefulRedisConnection) resource.getGenericObjectPool().borrowObject(2000L));
                        } catch (Exception e1) {
                            CacheExceptionFactory.addErrorLog("AbstractLettuceHandleExecutor->getLettucePool error twice !", e1);
                            throw e1;
                        }
                    }
                case CLUSTER:
                    return resource.getStatefulRedisClusterConnection();
                case CLUSTER_POOL:
                    return ((StatefulRedisClusterConnection) resource.getGenericObjectPool().borrowObject(2000L));
                default:
                    return resource.getStatefulRedisConnection();
            }
        } catch (Exception e) {
            CacheExceptionFactory.throwException(CacheExceptionConstants.CACHE_ERROR_CODE, "Lettuce getStatefulConnection error !", e);
            return null;
        }
    }

    @Override
    public void returnConnectResource() {
        //释放资源
        try {
            if (this.getCacheConfigModel().getConnectTypeEnum() == ConnectTypeEnum.POOL || this.getCacheConfigModel().getConnectTypeEnum() == ConnectTypeEnum.CLUSTER_POOL) {
                StatefulConnection localResource = statefulConnection.get();
                if (null != localResource) {
                    this.getPool().returnObject(localResource);
                }
            }
        } catch (Exception e) {
            CacheExceptionFactory.addErrorLog("AbstractLettuceHandleExecutor", "returnConnectResource", "resourceReturn error", e);
        }
        statefulConnection.remove();
    }

    @Override
    public void close() {
        //关闭连接
        this.getConnectResource().close();
    }

    @Override
    public GenericObjectPool getPool() {
        LettuceConnectResource resource = (LettuceConnectResource) this.connectResource.getResource();
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case POOL:
                return resource.getGenericObjectPool();
            case CLUSTER_POOL:
                return resource.getGenericObjectPool();
            default:
                return null;
        }
    }

    /**
     * 获取同步执行器
     *
     * @return
     */
    public RedisClusterCommands sync() {
        return this.sync(this.getStatefulConnection());
    }

    private RedisClusterCommands sync(StatefulConnection statefulConnection) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("Lettuce 暂不支持Shard方式");
                return null;
            case CLUSTER:
                return ((StatefulRedisClusterConnection) statefulConnection).sync();
            case CLUSTER_POOL:
                return ((StatefulRedisClusterConnection) statefulConnection).sync();
            default:
                return ((StatefulRedisConnection) statefulConnection).sync();
        }
    }

    /**
     * 异步
     *
     * @return
     */
    @Override
    public AbstractRedisAsyncCommands async() {
        lettuceRedisAsyncCommands.setAsyncExeutor(this);
        return lettuceRedisAsyncCommands;
    }

    /**
     * 异步
     *
     * @return
     */
    public RedisClusterAsyncCommands async(StatefulConnection statefulConnection) {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("Lettuce 暂不支持Shard方式");
                return null;
            case CLUSTER:
                return ((StatefulRedisClusterConnection) statefulConnection).async();
            case CLUSTER_POOL:
                return ((StatefulRedisClusterConnection) statefulConnection).async();
            default:
                return ((StatefulRedisConnection) statefulConnection).async();
        }
    }

    @Override
    public RedisClusterAsyncCommands asyncL() {
        return new LettuceAsyncProxy(this, this.async(this.getConnectResource())).getProxy();
    }

    /**
     * 获取发布订阅连接
     *
     * @return
     */
    public StatefulRedisPubSubConnection getPubSubConnection() {
        switch (this.getCacheConfigModel().getConnectTypeEnum()) {
            case SHARDED:
                CacheExceptionFactory.throwException("Lettuce 暂不支持Shard方式");
                return null;
            case CLUSTER:
                return lettuceConnectionFactory.getLettuceClusterPubSubConnection((LettuceClusterConnectSourceConfig) this.getRedisSourceConfig());
            case CLUSTER_POOL:
                return lettuceConnectionFactory.getLettuceClusterPubSubConnection((LettuceClusterConnectSourceConfig) this.getRedisSourceConfig());
            default:
                return lettuceConnectionFactory.getLettucePubSubConnection((LettuceConnectSourceConfig) this.getRedisSourceConfig());
        }
    }

    /**
     * Lettuce管道需要使用新的连接
     *
     * @param commands
     * @return
     */
    @Override
    public List<Object> pSync(List<PipelineCmd> commands) {
        //设置使用方式为管道
        CacheConfigModel pipelineCacheConfigModel = new CacheConfigModel(this.getCacheConfigModel(), UseTypeEnum.PIPELINE);
        //获取管道专用链接
        StatefulConnection statefulConnection = this.getStatefulConnection(new ConnectResource().setConnectResource(this.getPipelineConnectResource(pipelineCacheConfigModel)));
        try {
            List<Object> res = new ArrayList<>();
            List<RedisFuture<?>> redisFutures = new LettucePipelineExecutor(this.async(statefulConnection)).pSync(commands);
            for (int i = 0; i < redisFutures.size(); i++) {
                try {
                    res.add(redisFutures.get(i).get());
                } catch (Exception e) {
                    CacheExceptionFactory.addErrorLog("AbstractLettuceHandleExecutor->pSync get error ！ size:" + i, e);
                }
            }
            return res;
        } finally {
            //关闭连接
            //connectionResource.close();
        }
    }

    /**
     * 获取管道链接资源
     *
     * @return
     */
    private synchronized LettuceConnectResource getPipelineConnectResource(CacheConfigModel pipelineCacheConfigModel) {
        if (null == pipelineConnectResource) {
            pipelineConnectResource = lettuceConnectionFactory.getConnectionResource(this.getRedisSourceConfig(), pipelineCacheConfigModel);
        }
        return pipelineConnectResource;
    }

    @Override
    public CompletableFuture<List<Object>> pAsync(List<PipelineCmd> commands) {
        return CompletableFuture.supplyAsync(() -> {
            return this.pSync(commands);
        });
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
    public void expireAsync(int expireSeconds, String... keys) {
        if (expireSeconds > 0 && keys.length > 0) {
            if (keys.length == 1) {
                this.async().expire(keys[0], expireSeconds);
            } else {
                //lua批量设置过期时间
                this.async().expireBatch(expireSeconds, keys);
            }
        }
    }
}

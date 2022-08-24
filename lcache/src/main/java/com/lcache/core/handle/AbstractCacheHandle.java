package com.lcache.core.handle;

import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.cache.annotations.CommandsDataTypeUtil;
import com.lcache.core.cache.localcache.AbstractLocalCacheHandle;
import com.lcache.core.cache.localcache.LcacheCaffeineLocalCache;
import com.lcache.core.cache.localcache.RedisLocalCacheFactory;
import com.lcache.core.cache.redis.lua.RedisLuaInterface;
import com.lcache.core.cache.redis.model.LLock;
import com.lcache.core.constant.HandlePostProcessorTypeEnum;
import com.lcache.core.converters.PostProcessorConvertersAndExecutor;
import com.lcache.core.model.CacheDataBuilder;
import com.lcache.core.model.CacheHandleProcessorModel;
import com.lcache.core.processor.AbstractHandlePostProcessor;
import com.lcache.exception.CacheException;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.util.BeanFactory;
import com.lcache.util.CacheFunction;
import com.lcache.util.JsonUtil;
import io.lettuce.core.ScriptOutputType;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractCommomHandle
 * @Description: 对公共执行接口的抽象
 * @date 2021/1/18 3:20 PM
 */
public abstract class AbstractCacheHandle extends BaseCacheExecutor implements InterfaceCommomHandle {

    protected PostProcessorConvertersAndExecutor postProcessorConverters = BeanFactory.get(PostProcessorConvertersAndExecutor.class);

    /**
     * lua缓存信息
     */
    private Map<RedisLuaInterface, String> luaLoadsInfo = new ConcurrentHashMap<>();

    /**
     * 锁前缀
     */
    private static final String LOCK_PRE = "lcache:lock:";

    /**
     * 获取客户端类型
     * RedisClientConstants
     *
     * @return
     */
    public abstract int getClientType();

    /**
     * 根据操作类型获取执行链路
     *
     * @return
     */
    public List<AbstractHandlePostProcessor> getHandleLinkList() {
        return postProcessorConverters.getHandlePostProcessors(HandlePostProcessorTypeEnum.HANDLE, this.getClientType());
    }

    @Override
    public <T> T execute(CacheFunction<T> function) {
        return this.execute(function, -1, "");
    }


    @Override
    public <T> T execute(CacheFunction<T> function, String key) {
        return this.execute(function, -1, key);
    }

    @Override
    public <T> T execute(CacheFunction<T> function, String key, Object[] fields) {
        return this.execute(function, -1, key, fields);
    }

    @Override
    public <T> T execute(CacheFunction<T> function, String[] keys) {
        return this.execute(function, -1, keys);
    }

    /**
     * 执行命令
     *
     * @return
     */
    @Override
    public <T> T execute(CacheFunction<T> function, int expireSeconds, String key) {
        return this.execute(function, expireSeconds, key, null);
    }

    /**
     * 执行命令
     *
     * @return
     */
    @Override
    public <T> T execute(CacheFunction<T> function, int expireSeconds, String key, Object[] fields) {
        String commands = function.fnToFnName();
        return this.doExecute(
                new CacheHandleProcessorModel<T>(
                        //重写function，增加本地缓存
                        () -> RedisLocalCacheFactory.getLocalCacheHandle(CommandsDataTypeUtil.getCommandsDataType(commands), this).doCacheFunc(this, function, key, fields),
                        commands,
                        getCacheConfigModel(),
                        key),
                expireSeconds,
                key
        );
    }

    /**
     * 执行命令
     *
     * @return
     */
    @Override
    public <T> T execute(CacheFunction<T> function, int expireSeconds, String[] keys) {
        return this.doExecute(new CacheHandleProcessorModel<T>(function, function.fnToFnName(), getCacheConfigModel(), keys), expireSeconds, keys);
    }

    /**
     * 执行命令
     *
     * @param cacheHandleProcessorModel
     * @param expireSeconds
     * @param keys
     * @return
     */
    private <T> T doExecute(CacheHandleProcessorModel<T> cacheHandleProcessorModel, int expireSeconds, String... keys) {
        try {
            return postProcessorConverters.executeHandles(this.getHandleLinkList(), cacheHandleProcessorModel);
        } finally {
            //释放资源
            this.returnConnectResource();
            //设置过期时间
            this.expireAsync(expireSeconds, keys);
        }
    }

    /**
     * 执行命令
     *
     * @return
     */
    protected <T> T executeAndDelLocal(CacheFunction<T> function, int expireSeconds, String[] keys) {
        try {
            return this.doExecute(new CacheHandleProcessorModel<T>(function, function.fnToFnName(), getCacheConfigModel(), keys), expireSeconds, keys);
        } finally {
            for (int i = 0; i < keys.length; i++) {
                AbstractLocalCacheHandle.del(this, keys[i]);
            }
        }
    }

    /**
     * 异步设置过期时间
     *
     * @param expireSeconds
     * @param keys
     */
    public abstract void expireAsync(int expireSeconds, String... keys);


    @Override
    public LLock lock(String name, long leaseTime, TimeUnit unit) {
        return (LLock) this.execute(() -> {
            try {
                RLock lock = this.getRedissonClient().getLock(LOCK_PRE + name);
                lock.lock(leaseTime, unit);
                return new LLock(lock, this);
            } catch (Exception e) {
                CacheExceptionFactory.throwException("JedisRedisCommandsImpl->lock error !", e);
                return null;
            }
        });

    }

    @Override
    public LLock tryLock(String name, long waitTime, long leaseTime, TimeUnit unit) {
        return (LLock) this.execute(() -> {
            try {
                RLock lock = this.getRedissonClient().getLock(LOCK_PRE + name);
                boolean b = lock.tryLock(waitTime, leaseTime, unit);
                if (b) {
                    return new LLock(lock, this);
                }
                return null;
            } catch (InterruptedException e) {
                CacheExceptionFactory.throwException("JedisRedisCommandsImpl->tryLock error !", e);
                return null;
            }
        });
    }

    @Override
    public Object localGetAndSet(String key) {
        return LcacheCaffeineLocalCache.get(key, e -> this.get(key));
    }

    @Override
    public Object localGetAndSet(String key, Function function) {
        return LcacheCaffeineLocalCache.get(key, e -> function.apply(this));
    }

    @Override
    public Object getCacheData(CacheDataBuilder cacheDataBuilder) {
        if (null == cacheDataBuilder || StringUtils.isBlank(cacheDataBuilder.getLockKey()) || null == cacheDataBuilder.getCacheGetFunction() || null == cacheDataBuilder.getDbGetFunction() || null == cacheDataBuilder.getCacheSetFunction()) {
            CacheExceptionFactory.throwException("Lcache getCacheData params error !");
            return null;
        }
        Object data = null;
        try {
            //初次从缓存获取
            data = cacheDataBuilder.getCacheGetFunction().apply(this);
            if (null != data) {
                return data;
            }
            LLock lock = this.tryLock(cacheDataBuilder.getLockKey(), cacheDataBuilder.getWaitTime(), cacheDataBuilder.getLeaseTime(), cacheDataBuilder.getUnit());
            if (null != lock) {
                try {
                    //双检测
                    data = cacheDataBuilder.getCacheGetFunction().apply(this);
                    if (null != data) {
                        return data;
                    }
                    //从数据库获取，并保存数据
                    data = cacheDataBuilder.getDbGetFunction().apply(this);
                    if (null != data) {
                        //同步放入缓存
                        cacheDataBuilder.getCacheSetFunction().apply(data);
                    }
                    return data;
                } finally {
                    lock.unlock();
                }
            } else {
                //锁等待失败，最后尝试再从缓存拿一次
                return cacheDataBuilder.getCacheGetFunction().apply(this);
            }
        } catch (CacheException e) {
            CacheExceptionFactory.addErrorLog("AbstractCacheHandle", "getCacheData", "test:[{}]", e, JsonUtil.toJSONString(cacheDataBuilder));
            //redis异常从数据库拿
            if (null != data) {
                return data;
            }
            if (cacheDataBuilder.getCacheExceptionByDb()) {
                return cacheDataBuilder.getDbGetFunction().apply(this);
            } else {
                throw e;
            }
        }
    }

    @Override
    public String getLuaSha1(RedisLuaInterface redisLuaScripts) {
        return this.luaLoadsInfo.getOrDefault(redisLuaScripts, redisLuaScripts.getScripts());
    }

    @Override
    public Object executeByLua(RedisLuaInterface lua, ScriptOutputType outputType, List<String> keys, List<String> args) {
        return this.evalsha(getLuaSha1(lua), outputType, Optional.ofNullable(keys).orElse(new ArrayList<>()), Optional.ofNullable(args).orElse(new ArrayList<>()));
    }

    protected Map<RedisLuaInterface, String> getLuaLoadsInfo() {
        return luaLoadsInfo;
    }

    private static final String RMAP_PREFIX = "rmap:";

    @Override
    public Object getByRedissonMap(Object key) {
        return this.execute(() -> getRmap().get(key));
    }

    @Override
    public void putByRedissonMap(Object key, Object value) {
        this.execute(() -> getRmap().fastPut(key, value, 1, TimeUnit.DAYS));
    }

    @Override
    public Object putIfAbsentByRedissonMap(Object key, Object value) {
        return this.execute(() -> getRmap().putIfAbsent(key, value, 1, TimeUnit.DAYS));
    }

    @Override
    public void removeByRedissonMap(Object key) {
        this.execute(() -> getRmap().remove(key));
    }

    private RMapCache getRmap() {
        return this.getRedissonClient().getMapCache(RMAP_PREFIX + this.getCacheConfigModel().getCacheType());
    }
}

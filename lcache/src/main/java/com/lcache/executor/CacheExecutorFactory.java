package com.lcache.executor;

import com.lcache.config.BaseCacheConfig;
import com.lcache.connect.RedisConnectionManager;
import com.lcache.connect.scheduled.HeartCheckScheduled;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.constant.ConnectTypeEnum;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.core.monitor.MonitorConsumer;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.redis.jedis.JedisHandleExecutor;
import com.lcache.extend.handle.redis.lettuce.LettuceHandleExecutor;
import com.lcache.util.BeanFactory;
import com.lcache.util.CacheConfigUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheExecutorFactory
 * @Description: 缓存执行器
 * @date 2021/1/19 3:44 PM
 */
public class CacheExecutorFactory {
    /**
     * 心跳检测
     */
    private static HeartCheckScheduled heartCheckSchedululed = BeanFactory.get(HeartCheckScheduled.class);
    /**
     * 保存执行器副本
     */
    protected static Map<String, BaseCacheExecutor> executorMap = new ConcurrentHashMap<>();
    /**
     * cacheType与redis地址对应关系
     */
    private static Map<String, List<String>> cacheTypeHosts = new ConcurrentHashMap<>();
    /**
     * 配置的对应关系
     */
    private static Map<String, BaseCacheConfig> cacheConfigMap = new ConcurrentHashMap<>();

    public static List<String> getHostsByCacheType(String cacheType) {
        return cacheTypeHosts.get(cacheType);
    }

    private static Map<Integer, Class<? extends BaseCacheExecutor>> clientTypeSupports = new ConcurrentHashMap<>();

    static {
        //默认注入2个实现，支持非spring方式
        registClient(RedisClientConstants.JEDIS, JedisHandleExecutor.class);
        registClient(RedisClientConstants.LETTUCE, LettuceHandleExecutor.class);
    }

    /**
     * redis实现注册接口，如果有新的实现，需要主动调用注册
     *
     * @param clientType
     * @param cls
     */
    public static void registClient(int clientType, Class<? extends BaseCacheExecutor> cls) {
        clientTypeSupports.put(clientType, cls);
    }

    /**
     * 获取默认单节点连接信息
     *
     * @param cacheType
     * @return
     */
    public static String getDefaultHost(String cacheType) {
        List<String> hosts = CacheExecutorFactory.getHostsByCacheType(cacheType);
        String host = hosts.get(0);
        int index = host.indexOf(".");
        if (index > 0) {
            return host.substring(0, index);
        }
        return host;
    }

    public static BaseCacheConfig getRedisSourceConfig(CacheConfigModel cacheConfigModel) {
        return cacheConfigMap.get(getKey(cacheConfigModel));
    }

    /**
     * 获取执行器，通过默认配置
     *
     * @param redisSourceConfig
     * @param cacheConfigModel
     * @return
     */
    public static BaseCacheExecutor getCacheExecutor(BaseCacheConfig redisSourceConfig, CacheConfigModel cacheConfigModel) {
        if (null == cacheConfigModel || StringUtils.isBlank(cacheConfigModel.getCacheType()) || cacheConfigModel.getClientType() <= 0) {
            CacheExceptionFactory.throwException("CacheExecutorFactory->getCacheExecutor fail ! cacheConfigModel error ! cacheConfigModel:", null != cacheConfigModel ? cacheConfigModel.toString() : "null");
        }

        //禁止JedisSimple方式连接
        if (cacheConfigModel.getConnectTypeEnum() == ConnectTypeEnum.SIMPLE && cacheConfigModel.getClientType() == RedisClientConstants.JEDIS) {
            CacheExceptionFactory.throwException("禁止通过Jedis普通方式连接，非线程安全！");
        }
        if (null == redisSourceConfig) {
            CacheExceptionFactory.addWarnLog("CacheExecutorFactory->getCacheExecutor fail ! redisSourceConfig is null ! cacheConfigModel:{}", cacheConfigModel.toString());
            return null;
        }
        return connect(redisSourceConfig, cacheConfigModel);
    }

    /**
     * 连接
     *
     * @param redisSourceConfig
     * @param cacheConfigModel
     */
    public static BaseCacheExecutor connect(BaseCacheConfig redisSourceConfig, CacheConfigModel cacheConfigModel) {
        return executorMap.computeIfAbsent(getKey(cacheConfigModel), e -> {
            //去连接
            BaseCacheExecutor baseCacheExecutor = getCacheExecutorByClientType(cacheConfigModel);
            baseCacheExecutor.setCacheConfigModel(cacheConfigModel);
            baseCacheExecutor.setRedisSourceConfig(redisSourceConfig);
            return connect(baseCacheExecutor);
        });
    }

    /**
     * 去连接
     *
     * @param executor
     */
    public static BaseCacheExecutor connect(BaseCacheExecutor executor) {
        if (null == executor) {
            CacheExceptionFactory.throwException("connect fail BaseCacheExecutor is null ！");
        }
        executor.setConnectionResource(RedisConnectionManager.getConnectionResource(executor.getCacheConfigModel(), executor.getRedisSourceConfig()));
        //实例初始化后必须的操作
        after(executor.getRedisSourceConfig(), executor.getCacheConfigModel(), executor);
        return executor;
    }

    private static String getKey(CacheConfigModel cacheConfigModel) {
        return CacheConfigUtils.modelToHashKeyNoUseType(cacheConfigModel);
    }

    private static BaseCacheExecutor getCacheExecutorByClientType(CacheConfigModel cacheConfigModel) {
        try {
            return clientTypeSupports.get(cacheConfigModel.getClientType()).newInstance();
        } catch (Exception ex) {
            CacheExceptionFactory.throwException("获取连接资源失败！", ex);
            return null;
        }
    }


    /**
     * 执行器初始化后的操作
     *
     * @param redisSourceConfig
     * @param cacheConfigModel
     * @param baseCacheExecutor
     */
    private static void after(BaseCacheConfig redisSourceConfig, CacheConfigModel cacheConfigModel, BaseCacheExecutor baseCacheExecutor) {
        cacheTypeHosts.put(cacheConfigModel.getCacheType(), redisSourceConfig.getHosts());
        cacheConfigMap.put(getKey(cacheConfigModel), redisSourceConfig);
        //开启monitor消费
        if (cacheConfigModel.isOpenMonitor()) {
            MonitorConsumer.doConsume();
        }
        /**
         * 缓存lua脚本
         */
        baseCacheExecutor.loadLuaScripts();
    }

}

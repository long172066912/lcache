package com.lcache.connect;

import com.lcache.config.InterfaceCacheConfig;
import com.lcache.core.constant.RedisClientConstants;
import com.lcache.core.handle.AbstractConnectHandle;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.redis.jedis.connect.JedisConnectionFactory;
import com.lcache.extend.handle.redis.lettuce.connect.LettuceConnectionFactory;
import com.lcache.util.BeanFactory;
import com.lcache.util.CacheConfigUtils;
import com.lcache.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisConnectionManager
 * @Description: 连接池获取与创建
 * @date 2021/1/19 5:34 PM
 */
public class RedisConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConnectionManager.class);
    /**
     * 连接缓存，避免重复创建连接
     */
    protected static Map<String, ConnectResource> connectionMap = new ConcurrentHashMap<>();
    /**
     * 不同连接方式的配置实现管理
     */
    private static Map<Integer, AbstractConnectHandle> clientTypeSupports = new ConcurrentHashMap<>();

    static {
        ServiceLoader.load(AbstractConnectHandle.class).forEach(connectHandle-> regist(connectHandle.getClientType(), BeanFactory.get(connectHandle.getClass())));
    }

    /**
     * redis实现注册接口，如果有新的实现，需要主动调用注册
     *
     * @param clientType
     * @param cls
     */
    public static void regist(int clientType, AbstractConnectHandle cls) {
        clientTypeSupports.put(clientType, cls);
    }

    /**
     * 重置连接资源
     *
     * @param cacheConfigModel
     * @param redisSourceConfig
     */
    public static void resetConnectionResource(CacheConfigModel cacheConfigModel, Object redisSourceConfig) {
        if (null != redisSourceConfig) {
            String hashKey = CacheConfigUtils.modelToHashKey(cacheConfigModel);
            ConnectResource connection = connectionMap.get(hashKey);
            if (null != connection) {
                //加写锁
                final long writeLock = connection.getStampedLock().writeLock();
                try {
                    //释放旧连接资源
                    connection.getResource().close();
                    //重新获取连接
                    getConnectionResourceByCacheConfigModel(cacheConfigModel, (InterfaceCacheConfig) redisSourceConfig, connection);
                    LOGGER.info("RedisConnectionManager->resetConnectionResource end ! cacheConfigModel:[{}]", JsonUtil.toJSONString(cacheConfigModel));
                } catch (Exception e) {
                    CacheExceptionFactory.addErrorLog("RedisConnectionManager->resetConnectionResource reset error ！", e);
                } finally {
                    connection.getStampedLock().unlockWrite(writeLock);
                }
            }
        }
    }

    /**
     * 获取连接资源
     *
     * @param cacheConfigModel
     * @param redisSourceConfig
     * @return
     */
    public synchronized static ConnectResource getConnectionResource(CacheConfigModel cacheConfigModel, InterfaceCacheConfig redisSourceConfig) {
        if (null == redisSourceConfig) {
            CacheExceptionFactory.throwException("RedisConnectionManager->getConnectionResource redisSourceConfig is empty !");
            return null;
        }
        //获取已有的连接
        String hashKey = CacheConfigUtils.modelToHashKey(cacheConfigModel);
        ConnectResource connectionResource = connectionMap.get(hashKey);
        //没拿到创建新连接
        if (null == connectionResource) {
            connectionResource = getConnectionResourceByCacheConfigModel(cacheConfigModel, redisSourceConfig, null);
            if (null != connectionResource) {
                connectionMap.put(hashKey, connectionResource);
            }
        }
        return connectionResource;
    }

    /**
     * 获取连接资源
     *
     * @param cacheConfigModel
     * @param redisSourceConfig
     * @return
     */
    private synchronized static ConnectResource getConnectionResourceByCacheConfigModel(CacheConfigModel cacheConfigModel, InterfaceCacheConfig redisSourceConfig, ConnectResource connectResource) {
        if (null == connectResource) {
            connectResource = new ConnectResource();
        }
        try {
            return connectResource.setConnectResource(clientTypeSupports.get(cacheConfigModel.getClientType()).getConnectionResource(redisSourceConfig, cacheConfigModel));
        } catch (Exception ex) {
            CacheExceptionFactory.throwException("创建连接资源失败！", ex);
            return null;
        }
    }


}

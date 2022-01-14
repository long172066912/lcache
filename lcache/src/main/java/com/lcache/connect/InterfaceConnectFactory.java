package com.lcache.connect;

import com.lcache.config.InterfaceCacheConfig;
import com.lcache.core.model.CacheConfigModel;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: InterfaceConnectFactory
 * @Description: 连接创建接口
 * @date 2022/1/12 3:50 PM
 */
public interface InterfaceConnectFactory {
    /**
     * 获取连接资源
     *
     * @param cacheConfigModel
     * @param redisSourceConfig
     * @return
     */
    InterfaceConnectResource getConnectionResource(InterfaceCacheConfig redisSourceConfig, CacheConfigModel cacheConfigModel);
}

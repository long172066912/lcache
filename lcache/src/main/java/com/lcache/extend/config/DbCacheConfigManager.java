package com.lcache.extend.config;

import com.lcache.config.manager.AbstractConfigManager;
import com.lcache.config.manager.CacheConfigFactory;
import com.lcache.core.constant.CacheConfigSourceTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: DbCacheConfigManager
 * @Description: 获取缓存DB配置，应该有统一抽象类，此为DB方式的实现，TODO 使用者实现
 * @date 2021/1/28 9:01 PM
 */
public abstract class DbCacheConfigManager extends AbstractConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbCacheConfigManager.class);

    @PostConstruct
    public void init() {
        CacheConfigFactory.register(getConfigType(), this);
    }

    @Override
    public CacheConfigSourceTypeEnum getConfigType() {
        return CacheConfigSourceTypeEnum.DB;
    }
}

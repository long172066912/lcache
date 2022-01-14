package com.lcache.config.manager;

import com.lcache.config.BaseCacheConfig;
import com.lcache.core.constant.CacheConfigSourceTypeEnum;
import com.lcache.core.model.CacheConfigModel;

import javax.annotation.PostConstruct;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractConfigManager
 * @Description: 配置管理抽象
 * @date 2021/7/25 3:41 PM
 */
public abstract class AbstractConfigManager {

    /**
     * 获取配置类型
     *
     * @return
     */
    public abstract CacheConfigSourceTypeEnum getConfigType();

    @PostConstruct
    public void register() {
        CacheConfigFactory.register(this.getConfigType(), this);
    }

    /**
     * 获取配置
     *
     * @param cacheConfigModel
     * @return
     */
    public BaseCacheConfig getConfigByCacheModel(CacheConfigModel cacheConfigModel) {
        return this.getConfig(cacheConfigModel);
    }

    /**
     * 通过数据源获取配置
     *
     * @param cacheConfigModel
     * @return
     */
    protected abstract BaseCacheConfig getConfig(CacheConfigModel cacheConfigModel);
}

package com.lcache.config;

import com.lcache.config.model.CommonCacheConfig;

import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: BaseCacheConfig
 * @Description: 缓存配置抽象
 * @date 2021/2/24 3:26 PM
 */
public abstract class BaseCacheConfig implements InterfaceCacheConfig {

    /**
     * 公共配置
     */
    private CommonCacheConfig commonCacheConfig = new CommonCacheConfig();

    public CommonCacheConfig getCommonCacheConfig() {
        return commonCacheConfig;
    }

    public void setCommonCacheConfig(CommonCacheConfig commonCacheConfig) {
        this.commonCacheConfig = commonCacheConfig;
    }

    /**
     * 获取host
     *
     * @return
     */
    public abstract List<String> getHosts();
}

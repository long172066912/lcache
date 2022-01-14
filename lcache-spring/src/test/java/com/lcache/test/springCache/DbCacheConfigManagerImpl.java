package com.lcache.test.springCache;

import com.lcache.config.BaseCacheConfig;
import com.lcache.config.manager.CacheConfigFactory;
import com.lcache.core.constant.CacheConfigSourceTypeEnum;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.extend.config.DbCacheConfigManager;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
* @Title: DbCacheConfigManagerImpl
* @Description: 重写默认DB连接资源，连接本地
* @author JerryLong
* @date 2022/1/13 3:02 PM
* @version V1.0
*/
@Component
public class DbCacheConfigManagerImpl extends DbCacheConfigManager {

    @Override
    protected BaseCacheConfig getConfig(CacheConfigModel cacheConfigModel) {
        return new LettuceConnectSourceConfig();
    }
}

package com.lcache.spring;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LcacheConfig
 * @Description: 开启SpringCache
 * @date 2021/12/9 10:41 上午
 */
@Configuration
@EnableCaching
public class LcacheConfig extends CachingConfigurerSupport {

    @Bean
    public LcacheManager lcacheManager() {
        return new LcacheManager();
    }

}

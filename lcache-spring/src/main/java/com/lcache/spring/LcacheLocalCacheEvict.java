package com.lcache.spring;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
* @Title: WbLocalCacheEvict
* @Description: 与WbLocalCacheAble配套，默认使用本地缓存操作
* @author JerryLong
* @date 2022/1/24 2:07 PM
* @version V1.0
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CacheEvict
public @interface LcacheLocalCacheEvict {

    @AliasFor(annotation = CacheEvict.class)
    String[] cacheNames() default LcacheManager.LCACHE_SPRING_LOCAL_CACHE;

    @AliasFor(annotation = CacheEvict.class)
    String key();

    @AliasFor(annotation = CacheEvict.class)
    String condition() default "";

    @AliasFor(annotation = CacheEvict.class)
    boolean allEntries() default false;

    @AliasFor(annotation = CacheEvict.class)
    boolean beforeInvocation() default false;
}

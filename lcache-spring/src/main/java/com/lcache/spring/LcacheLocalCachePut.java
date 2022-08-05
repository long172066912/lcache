package com.lcache.spring;

import org.springframework.cache.annotation.CachePut;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
* @Title: LLocalCachePut
* @Description: 与LLocalCacheAble配套，默认使用本地缓存操作
* @author JerryLong
* @date 2022/1/24 2:07 PM
* @version V1.0
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CachePut
public @interface LcacheLocalCachePut {

    @AliasFor(annotation = CachePut.class)
    String[] cacheNames() default LcacheManager.LCACHE_SPRING_LOCAL_CACHE;

    @AliasFor(annotation = CachePut.class)
    String key();

    @AliasFor(annotation = CachePut.class)
    String condition() default "";

    @AliasFor(annotation = CachePut.class)
    String unless() default "";

}

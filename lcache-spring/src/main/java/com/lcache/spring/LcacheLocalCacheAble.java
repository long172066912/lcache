package com.lcache.spring;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
* @Title: LcacheLocalCacheAble
* @Description: 默认使用本地缓存进行SpringCacheAble操作
* @author JerryLong
* @date 2022/1/24 2:07 PM
* @version V1.0
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Cacheable
public @interface LcacheLocalCacheAble {
    /**
     * cacheType
     * 默认不传，通过 WbCacheManager.WB_SPRING_LOCAL_CACHE 调用 WbSpringLocalCache 中的 Caffeine
     * 有效期5分钟
     * 修改 LcacheLocalCachePut / 删除 LcacheLocalCacheEvict
     * @return
     */
    @AliasFor(annotation = Cacheable.class)
    String[] cacheNames() default LcacheManager.LCACHE_SPRING_LOCAL_CACHE;

    /**
     * 缓存的key，支持SpEL表达式，不传（不建议）则通过请求参数进行加密生成
     * @return
     */
    @AliasFor(annotation = Cacheable.class)
    String key();

    /**
     * 条件判断属性，用来指定符合指定的条件下才可以缓存。也可以通过 SpEL 表达式进行设置。
     * @return
     */
    @AliasFor(annotation = Cacheable.class)
    String condition() default "";

    /**
     * 即只有 unless 指定的条件为 true 时，方法的返回值才不会被缓存。可以在获取到结果后进行判断。
     * @return
     */
    @AliasFor(annotation = Cacheable.class)
    String unless() default "";

    /**
     * 默认同步
     * @return
     */
    @AliasFor(annotation = Cacheable.class)
    boolean sync() default false;
}

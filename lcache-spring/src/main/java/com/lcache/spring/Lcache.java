package com.lcache.spring;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: Lcache
 * @Description: Lcache spring注入注解
 * @date 2022/1/13 2:37 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lcache {
    /**
     * cacheType
     * @return
     */
    @AliasFor("cacheType")
    String value() default "";

    /**
     * 通过cacheType区分连接实例
     *
     * @return
     */
    String cacheType() default "";
}

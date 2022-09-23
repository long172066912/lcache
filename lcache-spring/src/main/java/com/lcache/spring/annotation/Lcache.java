package com.lcache.spring.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: Lcache
 * @Description: Lcache spring注入注解
 * @date 2022/1/13 2:37 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lcache {

    /**
     * 通过cacheType区分连接实例
     *
     * @return
     */
    String cacheType() default "";
}

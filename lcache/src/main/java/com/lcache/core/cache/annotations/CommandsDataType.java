package com.lcache.core.cache.annotations;


import com.lcache.core.constant.CommandsDataTypeEnum;
import com.lcache.core.constant.LocalCacheHandleTypeEnum;

import java.lang.annotation.*;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CommandsDataType
 * @Description: 自定义注解，收集redis命令的数据类型
 * @date 2021/7/8 10:41 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandsDataType {

    /**
     * 命令名称
     *
     * @return
     */
    public String commands() default "";

    /**
     * 命令名称
     *
     * @return
     */
    public CommandsDataTypeEnum dataType() default CommandsDataTypeEnum.STRING;

    /**
     * 热key 操作类型
     *
     * @return
     */
    public LocalCacheHandleTypeEnum localCacheHandleType() default LocalCacheHandleTypeEnum.NONE;
}

package com.lcache.test.util;

import com.lcache.exception.CacheExceptionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
* @Title: CommonUtils
* @Description: 公共帮助类
* @author JerryLong
* @date 2022/1/13 2:32 PM
* @version V1.0
*/
public class CommonUtils {

    public static Map<String, Object> bean2Map(Object bean) {
        if (bean == null) {
            return null;
        }
        // 注意: getDeclaredFields()获得某个类的所有申明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
        // 如果要获取所有的使用jacksonBean2Map
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                    continue;
                }
                field.setAccessible(true);
                map.put(field.getName(), field.get(bean));
            }
        } catch (IllegalAccessException e) {
            CacheExceptionFactory.addErrorLog("CommonUtils bean2Map !", e);
        }
        return map;
    }
}

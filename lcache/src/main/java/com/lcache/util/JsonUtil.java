package com.lcache.util;

import com.alibaba.fastjson2.JSON;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: JsonUtil
 * @Description: JSON组件封装
 * @date 2022/8/24 15:44
 */
public class JsonUtil {

    /**
     * 转json
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * json转对象
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }
}

package com.lcache.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: BeanFactory
 * @Description: BeanFactory
 * @author JerryLong
 * @date 2021/7/30 4:05 PM
 * @version V1.0
 */
public class BeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanFactory.class);
    private static ConcurrentHashMap<String, Object> beanMap;

    static {
        beanMap = new ConcurrentHashMap<>();
    }

    public static <T> T get(Class<T> clazz) {
        try {
            String className = clazz.getName();
            synchronized (className) {
                Object obj = beanMap.get(className);
                if (obj == null) {
                    Class<?> dao = Class.forName(className);
                    obj = dao.newInstance();
                    beanMap.put(className, obj);
                }
                return (T) obj;
            }

        } catch (Exception e) {
            LOGGER.error("BeanFactory get error ! className:{}", clazz.getSimpleName(), e);
        }
        return null;
    }
}


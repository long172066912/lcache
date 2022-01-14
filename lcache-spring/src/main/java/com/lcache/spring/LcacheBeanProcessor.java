package com.lcache.spring;

import com.lcache.client.CacheClientFactory;
import com.lcache.core.BaseCacheExecutor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @Title: LcacheBeanProcessor
* @Description: 后置类处理器注入Lcache
* @author JerryLong
* @date 2022/1/13 2:38 PM
* @version V1.0
*/
@Component
public class LcacheBeanProcessor implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        Class<?> cls = bean.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (!cls.equals(Object.class)){
            fieldList.addAll(new ArrayList<>(Arrays.asList(cls.getDeclaredFields())));
            cls = cls.getSuperclass();
        }
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(Lcache.class)) {
                field.setAccessible(true);
                try {
                    Object fieldObj = field.get(bean);
                    if (fieldObj != null) {
                        continue;
                    }
                    Lcache annotation = field.getAnnotation(Lcache.class);
                    String cacheType = annotation.cacheType();
                    BaseCacheExecutor baseCacheExecutor = CacheClientFactory.getCacheExecutor(cacheType);
                    //注入LcacheManager（使用Redisson实现的RedissonCache）
                    LcacheManager.addCaches(new LcacheRedissonCache(cacheType, baseCacheExecutor));
                    field.set(bean, baseCacheExecutor);
                } catch (Exception e) {
                    throw new BeanCreationException(beanName, e.getMessage(), e);
                }
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
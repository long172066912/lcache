package com.lcache.spring.processor;

import com.lcache.client.CacheClientFactory;
import com.lcache.config.BaseCacheConfig;
import com.lcache.config.RedisConfigBuilder;
import com.lcache.core.BaseCacheExecutor;
import com.lcache.core.constant.CacheConfigSourceTypeEnum;
import com.lcache.core.model.CacheConfigModel;
import com.lcache.extend.handle.redis.lettuce.config.LettuceConnectSourceConfig;
import com.lcache.spring.LcacheProperties;
import com.lcache.spring.annotation.Lcache;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbRedisBeanProcessor
 * @Description: 覆盖cache组件中的后置处理器
 * @date 2022/8/12 13:46
 */
@Component
public class CacheBeanProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {

    private BeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> cls = bean.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (!cls.equals(Object.class)) {
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
                    String cacheTypeName = annotation.cacheType();
                    BaseCacheExecutor baseCacheExecutor = null;
                    final LcacheProperties wbCacheProperties = beanFactory.getBean(LcacheProperties.class);
                    if (null != wbCacheProperties && null != wbCacheProperties.getCacheTypes() && wbCacheProperties.getCacheTypes().containsKey(cacheTypeName)) {
                        baseCacheExecutor = this.createCacheExecutor(cacheTypeName, wbCacheProperties.getCacheTypes().get(cacheTypeName));
                    } else {
                        baseCacheExecutor = this.createCacheExecutor(cacheTypeName, null);
                    }
                    if (null != baseCacheExecutor) {
                        field.set(bean, baseCacheExecutor);
                    }
                } catch (Exception e) {
                    throw new BeanCreationException(beanName, e.getMessage(), e);
                }
            }
        }
        return bean;
    }

    /**
     * 比cache组件中的后置处理器先执行
     *
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 创建cache执行器客户端
     *
     * @param cacheType
     * @param cacheConfig
     * @return
     */
    @SneakyThrows
    public BaseCacheExecutor createCacheExecutor(String cacheType, LcacheProperties.CacheTypeConfig cacheConfig) {
        if (null == cacheConfig) {
            return CacheClientFactory.getCacheExecutor(cacheType);
        }
        //循环所有cacheType
        BaseCacheExecutor cacheExecutor = null;
        BaseCacheConfig config = null;
        CacheConfigModel cacheConfigModel = new CacheConfigModel(cacheType);
        //设置连接客户端
        cacheConfigModel.setClientType(cacheConfig.getClientType());
        //设置连接资源类型
        cacheConfigModel.setConfigSourceType(cacheConfig.getSourceType());
        //设置连接方式
        cacheConfigModel.setConnectTypeEnum(cacheConfig.getConnectType());

        //构建配置
        switch (cacheConfig.getSourceType()) {
            case CUSTOM:
                if (CollectionUtils.isEmpty(cacheConfig.getSourceConfig())) {
                    throw new Throwable("Cache 组件 配置异常，请配置自定义连接资源配置!");
                }
                //TODO 暂时只支持Lettuce+Simple方式
                LcacheProperties.SourceConfig sourceConfig = cacheConfig.getSourceConfig().get(0);
                LettuceConnectSourceConfig lettuceConnectSourceConfig = new LettuceConnectSourceConfig(sourceConfig.getHost(), sourceConfig.getPort(), sourceConfig.getPassword(), sourceConfig.getConnectTimeout());
                lettuceConnectSourceConfig.setSoTimeout(sourceConfig.getSoTimeout());
                lettuceConnectSourceConfig.setDatabase(sourceConfig.getDatabase());
                config = lettuceConnectSourceConfig;
                break;
            case APOLLO:
            case DB:
            default:
                config = RedisConfigBuilder.builder(cacheConfigModel).build();
                break;
        }
        //如果是本地方式或者需要懒加载，则进行加载
        if (cacheConfig.isLazyLoading() || cacheConfig.getSourceType().equals(CacheConfigSourceTypeEnum.CUSTOM)) {
            cacheExecutor = CacheClientFactory.getCacheExecutor(cacheType, config);
        }
        //是否需要连接Redisson
        if (cacheConfig.isUseLock()) {
            cacheExecutor.getRedissonClient();
        }
        return cacheExecutor;
    }
}
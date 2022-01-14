package com.lcache.extend.handle.redis.lettuce.proxy;

import com.alibaba.fastjson.JSON;
import com.lcache.core.handle.AbstractCacheHandle;
import com.lcache.exception.CacheExceptionFactory;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: LettuceAsyncProxy
 * @Description: 生菜异步代理
 * @date 2021/10/21 2:24 PM
 */
public class LettuceAsyncProxy implements InvocationHandler {
    /**
     * 被代理的对象
     */
    private RedisClusterAsyncCommands redisAsyncCommands;

    private AbstractCacheHandle executor;

    public LettuceAsyncProxy(AbstractCacheHandle executor, RedisClusterAsyncCommands redisAsyncCommands) {
        this.executor = executor;
        this.redisAsyncCommands = redisAsyncCommands;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return executor.execute(() -> {
            try {
                return method.invoke(redisAsyncCommands, args);
            } catch (Exception e) {
                CacheExceptionFactory.addErrorLog("LettuceAsyncProxy asyncL invoke error ! args:{}", JSON.toJSONString(args), e);
            }
            return null;
        });
    }

    public RedisClusterAsyncCommands getProxy() {
        return (RedisClusterAsyncCommands) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                redisAsyncCommands.getClass().getInterfaces(),
                this);
    }
}

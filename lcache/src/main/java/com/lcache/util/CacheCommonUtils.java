package com.lcache.util;

import com.lcache.core.constant.RedisMagicConstants;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.pipeline.PipelineCmd;
import com.lcache.extend.handle.pipeline.PipelineExpire;

import java.lang.invoke.SerializedLambda;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheCommonUtils
 * @Description: 统一帮助类
 * @date 2021/4/19 3:25 PM
 */
public class CacheCommonUtils {

    /**
     * 反射对象缓存
     */
    public static Map<Object, SerializedLambda> cacheFunctionClassMap = new ConcurrentHashMap<>();

    /**
     * key1,value1,key2,value2 = > {key1=>value1,key2=>value2}
     *
     * @param keysvalues
     * @return
     */
    public static Map<String, Object> stringsToMap(String... keysvalues) {
        if (keysvalues.length % RedisMagicConstants.TWO > 0) {
            StringBuilder fields = new StringBuilder();
            for (String keysvalue : keysvalues) {
                fields.append(keysvalue);
            }
            CacheExceptionFactory.throwException(" CacheCommonUtils->stringsToMap 参数错误" + fields.toString());
            return null;
        }
        Map<String, Object> map = new HashMap<>(keysvalues.length);
        for (int i = 0; i < keysvalues.length; i++) {
            map.put(keysvalues[i], keysvalues[++i]);
        }
        return map;
    }

    /**
     * 获取设置过期时间管道命令
     *
     * @param keys
     * @param seconds
     * @return
     */
    public static List<PipelineCmd> getPipelineExpires(Set<String> keys, int seconds) {
        List<PipelineCmd> commands = new ArrayList<>();
        for (String key : keys) {
            commands.add(new PipelineExpire(key, seconds));
        }
        return commands;
    }
}

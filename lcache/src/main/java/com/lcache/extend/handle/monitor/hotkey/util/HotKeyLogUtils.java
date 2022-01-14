package com.lcache.extend.handle.monitor.hotkey.util;

import com.alibaba.fastjson.JSON;
import com.lcache.core.cache.annotations.CommandsDataTypeUtil;
import com.lcache.executor.CacheExecutorFactory;
import com.lcache.extend.handle.monitor.hotkey.model.HotKeyItem;
import com.lcache.extend.handle.monitor.hotkey.model.HotKeyWriterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HotKeyLogUtils
 * @Description: 热key文件操作帮助类
 * @date 2021/7/7 2:31 PM
 */
public class HotKeyLogUtils {

    private static final String SERVER_NAME = System.getProperty("spring.application.name", "unkown");;

    private static final Logger LOGGER = LoggerFactory.getLogger(HotKeyLogUtils.class);

    /**
     * 记录hotkeys到文件
     *
     * @param cacheType
     * @param hotkey
     */
    public static void apendHotkeys(String cacheType, HotKeyItem hotkey) {
        apendFile(JSON.toJSONString(
                new HotKeyWriterModel(
                        CacheExecutorFactory.getDefaultHost(cacheType),
                        SERVER_NAME,
                        cacheType,
                        CommandsDataTypeUtil.getCommandsDataType(hotkey.getCommands()).name(),
                        hotkey.getKey(),
                        hotkey.getCount().intValue(),
                        getNowTime()
                )
        ));
    }

    private static void apendFile(String content) {
        LOGGER.debug(content);
    }

    private static String getNowTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

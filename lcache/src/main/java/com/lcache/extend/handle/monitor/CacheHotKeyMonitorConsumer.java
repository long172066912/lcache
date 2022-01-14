package com.lcache.extend.handle.monitor;

import com.lcache.core.constant.MonitorTypeEnum;
import com.lcache.core.monitor.AbstractCacheMonitorConsumer;
import com.lcache.core.monitor.MonitorConfig;
import com.lcache.core.monitor.MonitorFactory;
import com.lcache.exception.CacheExceptionFactory;
import com.lcache.extend.handle.monitor.hotkey.HotKeyMonitor;
import com.lcache.extend.handle.monitor.hotkey.model.HotKeyItem;
import com.lcache.extend.handle.monitor.hotkey.util.HotKeyLogUtils;
import com.lcache.util.BeanFactory;
import com.lcache.util.async.AsyncExecutorUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: TimerMonitor
 * @Description: 时间监控实现
 * @date 2021/7/1 2:57 PM
 */
public class CacheHotKeyMonitorConsumer extends AbstractCacheMonitorConsumer {

    private static HotKeyMonitor hotKeyMonitor = BeanFactory.get(HotKeyMonitor.class);

    static {
        /**
         * 定时1秒调用一次
         */
        AsyncExecutorUtils.submitScheduledTask(() -> {
            hotKeyMonitor.getHotkeyStatisticsMap().entrySet().stream().forEach(statisticsEntry -> {
                try {
                    List<HotKeyItem> hotkeys = statisticsEntry.getValue().getHotKeysAndClean();
                    if (CollectionUtils.isNotEmpty(hotkeys)) {
                        //写文件
                        hotkeys.stream().forEach(e ->
                        {
                            HotKeyLogUtils.apendHotkeys(statisticsEntry.getKey(), e);
                            //提交热key
                            if (e.getCount().intValue() >= MonitorConfig.HOT_KEY_MIN_VALUE) {
                                MonitorFactory.submitHotKey(e.getKey());
                            }
                        });
                    }
                } catch (Exception e) {
                    CacheExceptionFactory.addErrorLog("HotKeyMonitor synchronization error ! cacheType:{}", statisticsEntry.getKey(), e);
                }
            });
        }, MonitorConfig.HOT_KEY_COUNT_lINTERVAL_MILLISECONDS, MonitorConfig.HOT_KEY_COUNT_lINTERVAL_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    @Override
    public MonitorTypeEnum getType() {
        return MonitorTypeEnum.HOTKEY;
    }

    @Override
    public Object doMonitor(MonitorFactory.MonitorData monitorData) {
        if (monitorData.getResult()) {
            if (StringUtils.isNotBlank(monitorData.getKey())) {
                hotKeyMonitor.doMonitor(monitorData.getCacheType(), monitorData.getCommands(), monitorData.getKey());
            } else if (CollectionUtils.isNotEmpty(monitorData.getKeys())) {
                monitorData.getKeys().stream().forEach(e -> hotKeyMonitor.doMonitor(monitorData.getCacheType(), monitorData.getCommands(), e));
            }
        }
        return null;
    }
}

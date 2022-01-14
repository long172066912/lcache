package com.lcache.extend.handle.monitor;

import com.lcache.core.constant.MonitorTypeEnum;
import com.lcache.core.monitor.AbstractCacheMonitorConsumer;
import com.lcache.core.monitor.MonitorFactory;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: TimerMonitor
 * @Description: 时间监控实现
 * @date 2021/7/1 2:57 PM
 */
public class CacheTimerMonitorConsumer extends AbstractCacheMonitorConsumer {

    @Override
    public MonitorTypeEnum getType() {
        return MonitorTypeEnum.TIMER;
    }

    @Override
    public Object doMonitor(MonitorFactory.MonitorData monitorData) {
        //TODO 原公司实现方式，需使用者更改消费实现
//        Metrics.timer("LcacheCommandsTime")
//                .tag("cacheType", monitorData.getCacheType())
//                .tag("dataStructure", CommandsDataTypeUtil.getCommandsDataType(monitorData.getCommands()).name())
//                .tag("commands", monitorData.getCommands())
//                .tag("host", CacheExecutorFactory.getDefaultHost(monitorData.getCacheType()))
//                .get().update(monitorData.getExecuteTime(), TimeUnit.MILLISECONDS);
        return null;
    }
}

package com.lcache.extend.handle.monitor;

import com.lcache.core.constant.MonitorTypeEnum;
import com.lcache.core.monitor.AbstractCacheMonitorConsumer;
import com.lcache.core.monitor.MonitorFactory;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: TimerMonitor
 * @Description: 统计类监控实现
 * @date 2021/7/1 2:57 PM
 */
public class CacheCounterMonitorConsumer extends AbstractCacheMonitorConsumer {

    @Override
    public MonitorTypeEnum getType() {
        return MonitorTypeEnum.COUNT;
    }

    @Override
    public Object doMonitor(MonitorFactory.MonitorData monitorData) {
            //count统计，TODO 原公司实现方式，需使用者更改消费实现
//            Metrics.counter("LcacheCommandsCount")
//                    .tag("host", CacheExecutorFactory.getDefaultHost(monitorData.getCacheType()))
//                    .tag("cacheType", monitorData.getCacheType())
//                    .tag("dataStructure", CommandsDataTypeUtil.getCommandsDataType(monitorData.getCommands()).name())
//                    .tag("commands", monitorData.getCommands())
//                    .get().inc();
        return null;
    }
}

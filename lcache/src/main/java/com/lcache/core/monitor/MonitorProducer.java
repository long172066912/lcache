package com.lcache.core.monitor;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MonitorProducer
 * @Description: 监控数据的生产者
 * @date 2021/7/5 3:07 PM
 */
public class MonitorProducer {

    /**
     * 生产命令执行结果
     *
     * @param monitorData
     */
    public static void addCommands(MonitorFactory.MonitorData monitorData) {
        MonitorFactory.addMonitorMsg(monitorData);
    }
}

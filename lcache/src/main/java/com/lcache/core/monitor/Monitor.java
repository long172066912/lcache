package com.lcache.core.monitor;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: Monitor
 * @Description: 监控接口
 * @date 2021/7/1 1:59 PM
 */
public interface Monitor {

    /**
     * 执行监控
     *
     * @param monitorData 监控实体
     * @return
     */
    Object doMonitor(MonitorFactory.MonitorData monitorData);
}

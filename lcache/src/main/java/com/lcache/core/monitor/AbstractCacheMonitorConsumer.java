package com.lcache.core.monitor;


import com.lcache.core.constant.MonitorTypeEnum;

import javax.annotation.PostConstruct;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractCacheMonitorConsumer
 * @Description: Monitor顶级抽象，制定协议
 * @date 2021/7/1 3:38 PM
 */
public abstract class AbstractCacheMonitorConsumer implements Monitor {

    /**
     * 获取消费类型
     *
     * @return
     */
    public abstract MonitorTypeEnum getType();

    @PostConstruct
    public void regester() {
        MonitorFactory.monitorRegist(this.getType(), this);
    }
}

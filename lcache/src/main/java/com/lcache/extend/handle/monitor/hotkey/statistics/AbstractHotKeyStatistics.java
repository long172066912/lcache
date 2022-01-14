package com.lcache.extend.handle.monitor.hotkey.statistics;

import com.lcache.core.monitor.MonitorConfig;
import com.lcache.extend.handle.monitor.hotkey.model.HotKeyItem;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.StampedLock;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractHotKeyStatistics
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2021/7/7 12:00 PM
 */
public abstract class AbstractHotKeyStatistics implements InterfaceHotKeyStatistics {
    /**
     * 热key统计容量大小
     */
    protected static final int STATISTIC_CAPACITY = MonitorConfig.HOTE_KEY_STATISTIC_CAPACITY;

    /**
     * 热key统计起始值
     */
    protected static final int HOT_KEY_COUNT_LEAST_VALUE = MonitorConfig.HOT_KEY_COUNT_LEAST_VALUE;

    /**
     * 读写锁
     */
    protected StampedLock stampedLock = new StampedLock();

    /**
     * key自增
     *
     * @param commands
     * @param key
     */
    protected abstract void incr(String commands, String key);

    /**
     * 获取热key列表
     *
     * @return
     */
    protected abstract List<HotKeyItem> getHotkeys();

    /**
     * 清空
     */
    protected abstract void clean();

    /**
     * 获取热key
     *
     * @return
     */
    public List<HotKeyItem> getHotKeysAndClean() {
        long writeLock = stampedLock.writeLock();
        try {
            List<HotKeyItem> hotkeys = this.getHotkeys();
            if (CollectionUtils.isNotEmpty(hotkeys)) {
                this.clean();
            }
            return hotkeys;
        } finally {
            stampedLock.unlock(writeLock);
        }
    }

    /**
     * key自增
     *
     * @param commands
     * @param key
     */
    public void hotKeyIncr(String commands, String key) {
        long readLock = stampedLock.readLock();
        try {
            this.incr(commands, key);
        } finally {
            stampedLock.unlock(readLock);
        }
    }
}

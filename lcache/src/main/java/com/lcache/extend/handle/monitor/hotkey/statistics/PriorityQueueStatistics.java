package com.lcache.extend.handle.monitor.hotkey.statistics;

import com.lcache.extend.handle.monitor.hotkey.model.HotKeyItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: PriorityQueueStatistics
 * @Description: 优先队列方式实现，未加锁，通过单线程消费无并发
 * @date 2021/7/5 9:43 AM
 */
public class PriorityQueueStatistics extends AbstractHotKeyStatistics {
    /**
     * 数量限制，只保留指定数量的元素
     * 最小堆，即频率最低的在队头，每次将新元素与堆头的比较，留下其中较大者
     */
    private PriorityQueue<HotKeyItem> hotKeyQueue = new PriorityQueue<>(STATISTIC_CAPACITY, (e1, e2) -> e1.getCount().intValue() > e2.getCount().intValue() ? 1 : 0);

    private Set<String> existedSet = new HashSet<>();

    private ConcurrentHashMap<String, HotKeyItem> hotkeyMap = new ConcurrentHashMap<>();

    private ReentrantLock incrLock = new ReentrantLock();

    @Override
    public List<HotKeyItem> getHotkeys() {
        return Arrays.asList(hotKeyQueue.toArray(new HotKeyItem[]{}));
    }

    @Override
    public void clean() {
        hotkeyMap.clear();
        existedSet.clear();
        hotKeyQueue.clear();
    }

    @Override
    public void incr(String commands, String key) {
        HotKeyItem item = hotkeyMap.computeIfAbsent(key, e -> new HotKeyItem(commands, key, new LongAdder()));
        incrLock.lock();
        try {
            //自增
            item.calibrationCommands(commands);
            item.getCount().increment();
            //如果未达到起始值，不计入热key
            if (item.getCount().intValue() < HOT_KEY_COUNT_LEAST_VALUE) {
                return;
            }
            //如果队列中存在对象
            if (existedSet.contains(key)) {
                hotKeyQueue.remove(item);
                hotKeyQueue.offer(item);
            } else {
                //如果队列长度不足
                if (hotKeyQueue.size() < STATISTIC_CAPACITY) {
                    hotKeyQueue.remove(item);
                    hotKeyQueue.offer(item);
                    existedSet.add(key);
                    return;
                }
                //弹出首位最小的进行对比，如果大于则插入
                HotKeyItem head = hotKeyQueue.peek();
                if (item.getCount().intValue() > head.getCount().intValue()) {
                    hotKeyQueue.poll();
                    hotKeyQueue.offer(item);
                    existedSet.remove(head.getKey());
                    existedSet.add(key);
                }
            }
        } finally {
            incrLock.unlock();
        }
    }
}

package com.redis.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private final ThreadGroup threadGroup;
    private final String prefix;
    private final boolean isDaemon;
    private final AtomicInteger sequence = new AtomicInteger(1);

    public NamedThreadFactory(String threadNamePrefix) {
        this(threadNamePrefix, false);
    }

    public NamedThreadFactory(String threadNamePrefix, boolean isDaemon) {
        SecurityManager sm = System.getSecurityManager();
        this.threadGroup = (sm == null) ? Thread.currentThread().getThreadGroup() : sm.getThreadGroup();
        this.prefix = threadNamePrefix + "-thread-";
        this.isDaemon = isDaemon;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = prefix + sequence.getAndIncrement();
        Thread thread = new Thread(threadGroup, runnable, name, 0);
        thread.setDaemon(isDaemon);
        return thread;
    }
}
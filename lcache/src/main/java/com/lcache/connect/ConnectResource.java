package com.lcache.connect;

import java.util.concurrent.locks.StampedLock;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ConnectResource
 * @Description: 缓存连接资源
 * @date 2021/2/1 4:03 PM
 */
public class ConnectResource implements InterfaceConnectResource {
    /**
     * 连接资源控制锁，当资源替换时，通过此锁控制
     */
    private StampedLock stampedLock = new StampedLock();

    private InterfaceConnectResource connectResource;

    public ConnectResource setConnectResource(InterfaceConnectResource connectResource) {
        this.connectResource = connectResource;
        return this;
    }

    @Override
    public void close() throws Exception {
        this.connectResource.close();
    }

    public StampedLock getStampedLock() {
        return stampedLock;
    }

    public InterfaceConnectResource getResource() {
        return this.connectResource;
    }
}

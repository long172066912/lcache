package com.lcache.connect;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: InterfaceConnectResource
 * @Description: 缓存连接接口
 * @date 2021/1/19 4:17 PM
 */
public interface InterfaceConnectResource {
    /**
     * 关闭连接
     *
     * @throws Exception
     */
    void close() throws Exception;
}

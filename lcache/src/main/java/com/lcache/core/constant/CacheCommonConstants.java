package com.lcache.core.constant;

import com.lcache.exception.CacheExceptionFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CacheCommonConstants
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2021/8/24 4:08 PM
 */
public class CacheCommonConstants {

    public static final String IP = getIp();

    private static String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            CacheExceptionFactory.addErrorLog("获取IP失败！", e);
        }
        return "";
    }
}

package com.redis;

import com.redis.core.command.data.RedisDataRdb;
import com.redis.server.RedisProxyTcpServer;

public class RedisProxyServerApplication {

    public static void main(String[] args) {
        final String filePath = System.getProperty("file_path");
        if (null == filePath || "".equals(filePath)) {
            System.out.println("请设置文件路径参数：-Dfile_path");
            return;
        }
        RedisDataRdb.init(filePath);
        final RedisProxyTcpServer redisProxyTcpServer = new RedisProxyTcpServer(6379);
        redisProxyTcpServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(redisProxyTcpServer::stop));
    }
}

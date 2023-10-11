package com.redis;

import com.redis.server.RedisProxyTcpServer;

public class RedisProxyServerApplication {

    public static void main(String[] args) {
        final RedisProxyTcpServer redisProxyTcpServer = new RedisProxyTcpServer(6379);
        redisProxyTcpServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(redisProxyTcpServer::stop));
    }
}

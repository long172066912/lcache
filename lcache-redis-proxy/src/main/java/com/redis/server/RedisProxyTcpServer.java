package com.redis.server;

import com.redis.handler.RedisProxyServerHandler;
import com.redis.handler.codec.CommandDecoder;
import com.redis.handler.codec.ResponseEncoder;
import com.redis.utils.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetAddress;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RedisProxyTcpServer
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2023/9/25 14:12
 */
public class RedisProxyTcpServer {

    private static final int TCP_SO_BACKLOG_VAL = 5000;
    private static final int TCP_SO_RCVBUF_VAL = 2 * 1024 * 1024;
    private static final int TCP_SO_SNDBUF_VAL = 2 * 1024 * 1024;

    private ChannelFuture channelFuture;

    private final int port;

    public RedisProxyTcpServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("redis-proxy start !");
        try {
            String ip = "0.0.0.0";
            InetAddress bindInterface = InetAddress.getByName(ip);
            EventLoopGroup bossGroup = new NioEventLoopGroup(5, new NamedThreadFactory("redis-proxy-boss-eventloop"));
            EventLoopGroup wokerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new NamedThreadFactory("redis-proxy-worker-eventloop"));
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, wokerGroup).channel(NioServerSocketChannel.class)
                    .localAddress(port).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // add protocol decoder and encoder...
                            init(ch, wokerGroup);
                        }
                    });

            //set tcp network options
            bootstrap.option(ChannelOption.SO_BACKLOG, TCP_SO_BACKLOG_VAL);
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);
            bootstrap.option(ChannelOption.SO_RCVBUF, TCP_SO_RCVBUF_VAL);
            bootstrap.option(ChannelOption.SO_SNDBUF, TCP_SO_SNDBUF_VAL);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

            //bind rpc server on ip:port address
            this.channelFuture = bootstrap.bind(bindInterface, port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init(SocketChannel ch, EventLoopGroup wokerGroup) {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(
                new ResponseEncoder(),
                new CommandDecoder()
        );
        channelPipeline.addLast(wokerGroup, new RedisProxyServerHandler());
    }

    public void stop() {
        System.out.println("redis-proxy close !");
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }
}

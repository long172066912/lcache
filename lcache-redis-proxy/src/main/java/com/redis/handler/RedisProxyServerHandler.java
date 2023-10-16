package com.redis.handler;

import com.redis.core.RedisCommandHandlerFactory;
import com.redis.core.command.RedisData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RedisProxyServerHandler extends SimpleChannelInboundHandler<RedisData> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisData redisData) throws Exception {
        try {
            ctx.channel().writeAndFlush(RedisCommandHandlerFactory.execute(redisData.getCommandName(), redisData.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.redis.handler;

import com.redis.core.command.RedisData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RedisProxyServerHandler extends SimpleChannelInboundHandler<RedisData> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisData redisData) throws Exception {
        try {
            //根据RedisData获取CommandsHandler
            final CommandHandler commandHandler = CommandHandlerFactory.getCommandHandler(redisData.getCommandName());
            if (null != commandHandler) {
                final Object res = commandHandler.handle(redisData);
                if (null != res) {
                    ctx.channel().writeAndFlush(res);
                }
            } else {
                System.out.println("未找到命令对应的执行器, redisData : " + redisData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

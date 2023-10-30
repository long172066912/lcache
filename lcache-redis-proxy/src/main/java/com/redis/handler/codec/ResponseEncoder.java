package com.redis.handler.codec;

import com.redis.resp.Resp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseEncoder extends MessageToByteEncoder<Resp> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Resp resp, ByteBuf byteBuf) throws Exception {
        try {
            System.out.println("返回数据：" + resp);
            Resp.write(resp, byteBuf);
            byteBuf.writeBytes(byteBuf);
        } catch (Exception e) {
            channelHandlerContext.close();
        }
    }

}
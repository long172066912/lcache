package com.redis.handler.codec;

import com.redis.core.Resp;
import com.redis.core.resp.BulkString;
import com.redis.core.resp.Errors;
import com.redis.core.resp.RespArray;
import com.redis.core.resp.SimpleString;
import com.redis.handler.RedisReq;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class CommandDecoder extends LengthFieldBasedFrameDecoder {
    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;

    public CommandDecoder() {
        super(MAX_FRAME_LENGTH, 0, 4);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        while (in.readableBytes() != 0) {
            int mark = in.readerIndex();
            try {
                Resp resp = Resp.decode(in);
                if (!(resp instanceof RespArray || resp instanceof SimpleString)) {
                    throw new IllegalStateException("客户端发送的命令应该只能是Resp Array 和 单行命令 类型");
                }
                RedisReq redisData = null;
                if (resp instanceof RespArray) {
                    redisData = getRedisData((RespArray) resp);
                } else if (resp instanceof SimpleString) {
                    redisData = getRedisData((SimpleString) resp);

                }
                if (redisData == null) {
                    //取出命令
                    ctx.writeAndFlush(new Errors("unsupport command:" + ((BulkString) ((RespArray) resp).getArray()[0]).getContent().toUtf8String()));
                } else {
                    return redisData;
                }
            } catch (Exception e) {
                in.readerIndex(mark);
                break;
            }
        }
        return null;
    }

    public RedisReq getRedisData(RespArray arrays) {
        Resp[] array = arrays.getArray();
        String commandName = ((BulkString) array[0]).getContent().toUtf8String().toLowerCase();
        return new RedisReq(commandName, array);
    }

    public RedisReq getRedisData(SimpleString string) {
        return new RedisReq(string.getContent().toLowerCase(), null);
    }
}
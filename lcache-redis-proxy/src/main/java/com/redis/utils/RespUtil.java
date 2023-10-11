package com.redis.utils;

import com.redis.handler.codec.Resp;
import com.redis.handler.codec.resp.BulkString;
import com.redis.handler.codec.resp.RespInt;
import com.redis.handler.codec.resp.SimpleString;

public class RespUtil {

    public static String toString(Resp resp) {
        if (resp instanceof SimpleString) {
            return ((SimpleString) resp).getContent();
        }
        return ((BulkString) resp).getContent().toUtf8String();
    }

    public static int toInt(Resp resp) {
        if (resp instanceof BulkString) {
            return Integer.parseInt(((BulkString) resp).getContent().toUtf8String());
        }
        return ((RespInt) resp).getValue();
    }
}

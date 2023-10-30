package com.redis.utils;

import com.redis.resp.Resp;
import com.redis.resp.impl.BulkString;
import com.redis.resp.impl.RespInt;
import com.redis.resp.impl.SimpleString;

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

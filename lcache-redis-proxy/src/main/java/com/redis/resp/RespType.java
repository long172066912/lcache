package com.redis.resp;

public enum RespType {
    /**
     *
     */
    ERROR((byte) '-'),
    STATUS((byte) '+'),
    BULK((byte) '$'),
    INTEGER((byte) ':'),
    MULTYBULK((byte) '*'),
    R((byte) '\r'),
    N((byte) '\n'),
    ZERO((byte) '0'),
    ONE((byte) '0'),
    ;

    private byte code;

    RespType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return this.code;
    }
}
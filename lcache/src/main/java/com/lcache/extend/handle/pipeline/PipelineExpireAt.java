package com.lcache.extend.handle.pipeline;

public class PipelineExpireAt extends PipelineCmd {
    private String key;
    private long unixTime;

    public PipelineExpireAt(String key, long unixTime) {
        this.key = key;
        this.unixTime = unixTime;
    }

    @Override
    public CMD getCmd() {
        return CMD.EXPIREAT;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }
}

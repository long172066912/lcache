package com.lcache.extend.handle.pipeline;

public class PipelineExpire extends PipelineCmd {
    private String key;
    private int time;

    public PipelineExpire(String key, int time) {
        this.key = key;
        this.time = time;
    }

    @Override
    public CMD getCmd() {
        return CMD.EXPIRE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}

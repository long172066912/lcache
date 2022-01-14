package com.lcache.extend.handle.pipeline;

public class PipelineZREM extends PipelineCmd {
    private String key;
    private String[] member;

    public PipelineZREM(String key, String... member) {
        this.key = key;
        this.member = member;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZREM;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getMember() {
        return member;
    }

    public void setMember(String[] member) {
        this.member = member;
    }
}

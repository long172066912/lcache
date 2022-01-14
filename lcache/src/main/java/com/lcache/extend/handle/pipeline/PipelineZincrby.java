package com.lcache.extend.handle.pipeline;

public class PipelineZincrby extends PipelineCmd {
    private String key;
    private int count;
    private String member;

    public PipelineZincrby(String key, int count, String member) {
        this.key = key;
        this.count = count;
        this.member = member;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZINCRBY;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}

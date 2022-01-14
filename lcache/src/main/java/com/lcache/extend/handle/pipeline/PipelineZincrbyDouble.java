package com.lcache.extend.handle.pipeline;

public class PipelineZincrbyDouble extends PipelineCmd {
    private String key;
    private double count;
    private String member;

    public PipelineZincrbyDouble(String key, double count, String member) {
        this.key = key;
        this.count = count;
        this.member = member;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZINCRBYDOUBLE;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}

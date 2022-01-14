package com.lcache.extend.handle.pipeline;

public class PipelineSIsmember extends PipelineCmd {
    private String key;
    private String member;

    public PipelineSIsmember(String key, String member) {
        this.key = key;
        this.member = member;
    }

    @Override
    public CMD getCmd() {
        return CMD.SISMEMBER;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}

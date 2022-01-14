package com.lcache.extend.handle.pipeline;

public class PipelineSAdd extends PipelineCmd {
    private String key;
    private String[] members;

    public PipelineSAdd(String key, String... members) {
        this.key = key;
        this.members = members;
    }

    @Override
    public CMD getCmd() {
        return CMD.SADD;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }
}

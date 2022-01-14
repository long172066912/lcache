package com.lcache.extend.handle.pipeline;

public class PipelineZadd extends PipelineCmd {
    private String key;
    private double score;
    private String member;

    public PipelineZadd(String key, double score, String member) {
        this.key = key;
        this.score = score;
        this.member = member;
    }

    @Override
    public CMD getCmd() {
        return CMD.ZADD;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}

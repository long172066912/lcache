package com.lcache.core.model;

import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RankingList
 * @Description: 排行榜
 * @date 2021/8/20 1:43 PM
 */
public class RankingList {
    public RankingList() {
    }

    public RankingList(boolean isMore, Set<Tuple> rankingList) {
        this.isMore = isMore;
        this.rankingList = rankingList;
    }

    /**
     * 是否还有下一页
     */
    private boolean isMore;
    /**
     * 列表
     */
    private Set<Tuple> rankingList;

    public boolean isMore() {
        return isMore;
    }

    public Set<Tuple> getRankingList() {
        return rankingList;
    }
}

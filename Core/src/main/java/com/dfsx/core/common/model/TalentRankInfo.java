package com.dfsx.core.common.model;

import java.io.Serializable;

/**
 * Created by heyang on 2017/11/13.
 */
public class TalentRankInfo implements Serializable {


    /**
     * receive_agree_count : -1
     * comment_count : -1
     * fans_count : -1
     * send_gift_count : -1
     * send_gift_coins : 0.0
     * receive_agree_rank : -1
     * comment_rank : -1
     * fans_rank : -1
     * send_gift_rank : -1
     */

    private long receive_agree_count;
    private long comment_count;
    private long fans_count;
    private long send_gift_count;
    private double send_gift_coins;
    private long receive_agree_rank;
    private long comment_rank;
    private long fans_rank;
    private long send_gift_rank;

    public long getReceive_agree_count() {
        return receive_agree_count;
    }

    public void setReceive_agree_count(long receive_agree_count) {
        this.receive_agree_count = receive_agree_count;
    }

    public long getComment_count() {
        return comment_count;
    }

    public void setComment_count(long comment_count) {
        this.comment_count = comment_count;
    }

    public long getFans_count() {
        return fans_count;
    }

    public void setFans_count(long fans_count) {
        this.fans_count = fans_count;
    }

    public long getSend_gift_count() {
        return send_gift_count;
    }

    public void setSend_gift_count(long send_gift_count) {
        this.send_gift_count = send_gift_count;
    }

    public double getSend_gift_coins() {
        return send_gift_coins;
    }

    public void setSend_gift_coins(double send_gift_coins) {
        this.send_gift_coins = send_gift_coins;
    }

    public long getReceive_agree_rank() {
        return receive_agree_rank;
    }

    public void setReceive_agree_rank(long receive_agree_rank) {
        this.receive_agree_rank = receive_agree_rank;
    }

    public long getComment_rank() {
        return comment_rank;
    }

    public void setComment_rank(long comment_rank) {
        this.comment_rank = comment_rank;
    }

    public long getFans_rank() {
        return fans_rank;
    }

    public void setFans_rank(long fans_rank) {
        this.fans_rank = fans_rank;
    }

    public long getSend_gift_rank() {
        return send_gift_rank;
    }

    public void setSend_gift_rank(long send_gift_rank) {
        this.send_gift_rank = send_gift_rank;
    }
}

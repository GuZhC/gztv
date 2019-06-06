package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 *  达人榜
 * Created by heyang on 2017/11/10.
 */

public class TalentEntry implements Serializable {


    /**
     * user_id : -1
     * username :
     * nickname :
     * avatar_url :
     * receive_agree_count : -1
     * comment_count : -1
     * fans_count : -1
     * send_gift_count : -1
     * send_gift_coins : -1
     * rank : -1
     */

    private long user_id;
    private String username;
    private String nickname;
    private String avatar_url;
    private long receive_agree_count;
    private long comment_count;
    private long fans_count;
    private long send_gift_count;
    private double send_gift_coins;
    private long rank;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

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

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }
}

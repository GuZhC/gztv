package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2016/12/20.
 */
public class GuessRoomInfo implements Serializable {


    /**
     * subject : 直播主题
     * cover_id : 111111111111
     * cover_url : 直播封面URL
     * introduction : 直播介绍
     * plan_start_time : timestamp,计划直播开始时间, 可以为空
     * plan_stop_time : timestamp, 计划直播结束时间，可以为空
     * start_bet_time : timestamp, 下注开始时间
     * stop_bet_time : timestamp, 下注结束时间
     * royalty_percent : double, 系统提成比例
     * state : int, 1 – 未开始， 2 – 正在直播， 3 – 已结束
     * result : int, 竟猜结果：0 – 未揭晓，其它表示下注选项的ID
     */

    private String subject;
    @SerializedName("cover_id")
    private long coverId;
    @SerializedName("cover_url")
    private String coverUrl;
    private String introduction;
    @SerializedName("plan_start_time")
    private long planStartTime;
    @SerializedName("plan_stop_time")
    private long planStopTime;
    @SerializedName("start_bet_time")
    private long startBetTime;
    @SerializedName("stop_bet_time")
    private long stopBetTime;
    @SerializedName("royalty_percent")
    private double royaltyPercent;
    private int state;
    private int result;
    @SerializedName("players")
    private List<GuessPlayer> playerList;
    @SerializedName("bet_options")
    private List<BetOption> betOptionList;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getCoverId() {
        return coverId;
    }

    public void setCoverId(long coverId) {
        this.coverId = coverId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public long getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(long planStartTime) {
        this.planStartTime = planStartTime;
    }

    public long getPlanStopTime() {
        return planStopTime;
    }

    public void setPlanStopTime(long planStopTime) {
        this.planStopTime = planStopTime;
    }

    public long getStartBetTime() {
        return startBetTime;
    }

    public void setStartBetTime(long startBetTime) {
        this.startBetTime = startBetTime;
    }

    public long getStopBetTime() {
        return stopBetTime;
    }

    public void setStopBetTime(long stopBetTime) {
        this.stopBetTime = stopBetTime;
    }

    public double getRoyaltyPercent() {
        return royaltyPercent;
    }

    public void setRoyaltyPercent(double royaltyPercent) {
        this.royaltyPercent = royaltyPercent;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<GuessPlayer> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<GuessPlayer> playerList) {
        this.playerList = playerList;
    }

    public List<BetOption> getBetOptionList() {
        return betOptionList;
    }

    public void setBetOptionList(List<BetOption> betOptionList) {
        this.betOptionList = betOptionList;
    }
}

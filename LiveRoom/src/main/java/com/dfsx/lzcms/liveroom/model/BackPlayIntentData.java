package com.dfsx.lzcms.liveroom.model;

/**
 * 进入回放页面需要的数据
 * Created by liuwb on 2017/2/21.
 */
public class BackPlayIntentData extends FullScreenRoomIntentData {

    private long backPlayId;

    private int memberSize;

    private double roomTotalCoins;

    private int liveType;

    private long showId;

    public long getBackPlayId() {
        return backPlayId;
    }

    public void setBackPlayId(long backPlayId) {
        this.backPlayId = backPlayId;
    }

    public int getMemberSize() {
        return memberSize;
    }

    public void setMemberSize(int memberSize) {
        this.memberSize = memberSize;
    }

    public double getRoomTotalCoins() {
        return roomTotalCoins;
    }

    public void setRoomTotalCoins(double roomTotalCoins) {
        this.roomTotalCoins = roomTotalCoins;
    }

    public int getLiveType() {
        return liveType;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
    }

    public long getShowId() {
        return showId;
    }

    public void setShowId(long showId) {
        this.showId = showId;
    }
}

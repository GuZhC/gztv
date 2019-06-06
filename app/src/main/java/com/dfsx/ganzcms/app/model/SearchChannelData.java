package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuwb on 2016/11/2.
 */
public class SearchChannelData {

    private int total;
    @SerializedName("data")
    private List<LiveRoomInfo> roomInfoList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LiveRoomInfo> getRoomInfoList() {
        return roomInfoList;
    }

    public void setRoomInfoList(List<LiveRoomInfo> roomInfoList) {
        this.roomInfoList = roomInfoList;
    }
}

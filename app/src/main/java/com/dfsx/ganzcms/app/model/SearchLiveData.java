package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2017/7/3.
 */
public class SearchLiveData implements Serializable {
    private int total;
    @SerializedName("data")
    private List<LiveInfo> liveInfoList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LiveInfo> getLiveInfoList() {
        return liveInfoList;
    }

    public void setLiveInfoList(List<LiveInfo> liveInfoList) {
        this.liveInfoList = liveInfoList;
    }
}

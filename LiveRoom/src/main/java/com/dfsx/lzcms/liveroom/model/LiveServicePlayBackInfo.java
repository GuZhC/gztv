package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2017/7/6.
 */
public class LiveServicePlayBackInfo implements Serializable {

    private List<LiveServiceMultilineVideoInfo> multilineVideoInfoList;

    public LiveServicePlayBackInfo() {

    }

    public List<LiveServiceMultilineVideoInfo> getMultilineVideoInfoList() {
        return multilineVideoInfoList;
    }

    public void setMultilineVideoInfoList(List<LiveServiceMultilineVideoInfo> multilineVideoInfoList) {
        this.multilineVideoInfoList = multilineVideoInfoList;
    }
}

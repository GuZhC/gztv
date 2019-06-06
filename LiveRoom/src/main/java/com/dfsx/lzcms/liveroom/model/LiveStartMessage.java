package com.dfsx.lzcms.liveroom.model;

import com.dfsx.lzcms.liveroom.view.IMultilineVideo;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/7/4.
 */
public class LiveStartMessage extends LiveMessage {

    @SerializedName("view_streams")
    private List<LiveOutputStreamInfo> livetreamList;

    public List<LiveOutputStreamInfo> getLivetreamList() {
        return livetreamList;
    }

    public void setLivetreamList(List<LiveOutputStreamInfo> livetreamList) {
        this.livetreamList = livetreamList;
    }

}

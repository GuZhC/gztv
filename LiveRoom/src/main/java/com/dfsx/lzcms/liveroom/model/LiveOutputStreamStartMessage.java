package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

/**
 * 输出流开始消息
 * Created by liuwb on 2017/7/13.
 */
public class LiveOutputStreamStartMessage extends LiveMessage {

    @SerializedName("output_stream")
    private LiveOutputStreamInfo outputStreamInfo;

    public LiveOutputStreamInfo getOutputStreamInfo() {
        return outputStreamInfo;
    }

    public void setOutputStreamInfo(LiveOutputStreamInfo outputStreamInfo) {
        this.outputStreamInfo = outputStreamInfo;
    }
}

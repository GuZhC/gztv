package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

/**
 * 直播输入流消息
 * Created by liuwb on 2017/7/13.
 */
public class LiveInputStreamMessage extends LiveMessage {

    @SerializedName("input_stream")
    private LiveInputStreamInfo inputStreamInfo;

    public LiveInputStreamInfo getInputStreamInfo() {
        return inputStreamInfo;
    }

    public void setInputStreamInfo(LiveInputStreamInfo inputStreamInfo) {
        this.inputStreamInfo = inputStreamInfo;
    }
}

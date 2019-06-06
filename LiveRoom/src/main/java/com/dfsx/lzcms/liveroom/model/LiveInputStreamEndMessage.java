package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

public class LiveInputStreamEndMessage extends LiveMessage {
    @SerializedName("input_stream")
    private LiveInputStreamInfo inputStreamInfo;

    public LiveInputStreamInfo getInputStreamInfo() {
        return inputStreamInfo;
    }

    public void setInputStreamInfo(LiveInputStreamInfo inputStreamInfo) {
        this.inputStreamInfo = inputStreamInfo;
    }
}

package com.dfsx.lzcms.liveroom.model;

/**
 * 输出流结束消息(活动直播)
 * Created by liuwb on 2017/7/27.
 */
public class LiveOutputStreamEndMessage extends LiveMessage {
    private LiveOutputStreamInfo streamNameInfo;

    public LiveOutputStreamInfo getStreamNameInfo() {
        return streamNameInfo;
    }

    public void setStreamNameInfo(LiveOutputStreamInfo streamNameInfo) {
        this.streamNameInfo = streamNameInfo;
    }
}

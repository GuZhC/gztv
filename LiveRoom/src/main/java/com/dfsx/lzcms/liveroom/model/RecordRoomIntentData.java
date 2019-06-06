package com.dfsx.lzcms.liveroom.model;

/**
 * 调往录制直播间的数据
 * Created by liuwb on 2017/2/21.
 */
public class RecordRoomIntentData extends ChatRoomIntentData {
    private boolean isScreenPortrait;

    private String subject;

    private String coverImagePath;

    private String liveRTMPURL;

    /**
     * 是否是活动直播录制
     */
    private boolean isLiveServiceRecord;

    public boolean isScreenPortrait() {
        return isScreenPortrait;
    }

    public void setScreenPortrait(boolean screenPortrait) {
        isScreenPortrait = screenPortrait;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public String getLiveRTMPURL() {
        return liveRTMPURL;
    }

    public void setLiveRTMPURL(String liveRTMPURL) {
        this.liveRTMPURL = liveRTMPURL;
    }

    public boolean isLiveServiceRecord() {
        return isLiveServiceRecord;
    }

    public void setLiveServiceRecord(boolean liveServiceRecord) {
        isLiveServiceRecord = liveServiceRecord;
    }
}

package com.dfsx.lzcms.liveroom.model;

/**
 * Created by liuwb on 2017/2/21.
 */
public class FullScreenRoomIntentData extends ChatRoomIntentData {
    private String fullScreenVideoImagePath;
    /**
     * 是不是预告直播
     */
    private boolean isYuGaoLive;

    /**
     * 单位为秒
     * 直播的开始时间。只有在是预告的情况下有效果(isYuGaoLive = true)
     */
    private long startTimestamp;

    /**
     * 设置视屏是横屏的么
     */
    private boolean isLandVideo;

    public FullScreenRoomIntentData() {
    }


    public String getFullScreenVideoImagePath() {
        return fullScreenVideoImagePath;
    }

    public void setFullScreenVideoImagePath(String fullScreenVideoImagePath) {
        this.fullScreenVideoImagePath = fullScreenVideoImagePath;
    }

    public boolean isYuGaoLive() {
        return isYuGaoLive;
    }

    public void setYuGaoLive(boolean yuGaoLive) {
        isYuGaoLive = yuGaoLive;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * 单位为秒
     *
     * @param startTimestamp
     */
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public boolean isLandVideo() {
        return isLandVideo;
    }

    public void setLandVideo(boolean landVideo) {
        isLandVideo = landVideo;
    }
}

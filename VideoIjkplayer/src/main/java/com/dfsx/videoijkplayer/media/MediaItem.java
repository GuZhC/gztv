package com.dfsx.videoijkplayer.media;

import java.io.Serializable;

/**
 * Created by hy on 2017/10/10.
 */

public class MediaItem implements Serializable {

    private long id;
    private long adid;
    private int duration;
    private String url = "";
    private boolean isAdware;  //是否是广告
    private int type;//   1：视频  2:图片
    private int skinTime;  // 多少可以跳过视频
    private String linkUrl;   //链接地址
    private boolean isFull;   //  是否全屏
    private int videoDurtaion;  //视频长度

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAdid() {
        return adid;
    }

    public void setAdid(long adid) {
        this.adid = adid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAdware() {
        return isAdware;
    }

    public void setAdware(boolean adware) {
        isAdware = adware;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getSkinTime() {
        return skinTime;
    }

    public void setSkinTime(int skinTime) {
        this.skinTime = skinTime;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public int getVideoDurtaion() {
        return videoDurtaion;
    }

    public void setVideoDurtaion(int videoDurtaion) {
        this.videoDurtaion = videoDurtaion;
    }


}
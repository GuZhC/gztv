package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/12/15.
 */
public class ShowRoomInfo implements Serializable {


    /**
     * subject : 直播主题
     * cover_id : 11111111111
     * cover_url : 直播封面URL
     * introduction : 直播介绍
     * password : 直播密码
     * rtmp_url : 直播间RTMP流地址
     */

    /**
     * show id
     */
    private long id;
    private String subject;
    @SerializedName("cover_id")
    private long coverId;
    @SerializedName("cover_url")
    private String coverUrl;
    private String introduction;
    private String password;
    @SerializedName("rtmp_url")
    private String rtmpUrl;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getCoverId() {
        return coverId;
    }

    public void setCoverId(long coverId) {
        this.coverId = coverId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

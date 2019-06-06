package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 直播可以设置的信息
 * Created by liuwb on 2016/12/9.
 */
public class LiveSettingInfo implements Serializable {

    /**
     * subject : 直播主题
     * cover_id : 直播封面ID(ID和上传路径仅能有一个有效)
     * cover_path : 直播封面上传路径(ID和上传路径仅能有一个有效)
     * password : 直播密码
     * start_time : 1111111111111
     * introduction : 直播介绍
     */

    private String subject;
    @SerializedName("cover_id")
    private long coverId;
    @SerializedName("cover_path")
    private String coverPath;
    private String password;
    @SerializedName("start_time")
    private long startTime;
    private String introduction;

    public LiveSettingInfo() {

    }

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

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}

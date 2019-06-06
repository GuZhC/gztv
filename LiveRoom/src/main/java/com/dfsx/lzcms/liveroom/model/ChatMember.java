package com.dfsx.lzcms.liveroom.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/11/4.
 */
public class ChatMember implements Serializable {

    private long userId;

    private String userJid;

    private String nickName;

    private String userName;

    private String logo;

    @SerializedName("user_level_id")
    private long userLevelId;
    /**
     * 用户的全部详细信息。这个值可能为null
     * 只是起到缓存的作用
     */
    private UserDetailsInfo userDetailsInfo;

    public ChatMember() {
    }

    public String getNickName() {
        if (TextUtils.isEmpty(nickName)) {
            return userName;
        } else {
            return nickName;
        }
    }

    public void setNickName(String name) {
        this.nickName = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        if (TextUtils.isEmpty(userName)) {
            return "";
        } else {
            return userName;
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserDetailsInfo getUserDetailsInfo() {
        return userDetailsInfo;
    }

    public void setUserDetailsInfo(UserDetailsInfo userDetailsInfo) {
        this.userDetailsInfo = userDetailsInfo;
    }

    public String getUserJid() {
        return userJid;
    }

    public void setUserJid(String userJid) {
        this.userJid = userJid;
    }

    public long getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(long userLevelId) {
        this.userLevelId = userLevelId;
    }
}

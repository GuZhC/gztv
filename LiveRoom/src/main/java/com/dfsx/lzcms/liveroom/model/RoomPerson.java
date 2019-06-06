package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomPerson implements Serializable {
    /**
     * user_id : 用户ID
     * username : 用户名
     * nickname : 用户昵称
     * avatar_url : 用户头像地址
     * expired_time : 禁言到期时间
     */

    @SerializedName("user_id")
    private long userId;
    @SerializedName("username")
    private String userName;
    @SerializedName("nickname")
    private String nickName;
    @SerializedName("avatar_url")
    private String logo;
    @SerializedName("user_level_id")
    private long userLevelId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(long userLevelId) {
        this.userLevelId = userLevelId;
    }
}

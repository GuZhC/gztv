package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

import java.net.URLDecoder;

/**
 * Created by liuwb on 2017/7/4.
 */
public class UserChatMessage extends LiveMessage {

    /**
     * user_id : 10000
     * user_name : 用户名
     * user_nickname : 用户呢称
     * user_avatar_url : 用户头像地址
     * body : 消息内容
     */

    @SerializedName("user_id")
    private long userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_nickname")
    private String userNickName;
    @SerializedName("user_avatar_url")
    private String userAvatarUrl;
    @SerializedName("user_level_id")
    private long userLevelId;
    private String body;

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

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public long getChatUserId() {
        return getUserId();
    }

    @Override
    public String getChatUserNickName() {
        return getUserNickName();
    }

    @Override
    public String getChatUserLogo() {
        return getUserAvatarUrl();
    }

    @Override
    public CharSequence getChatContentText() {
        return getBody();
    }

    public long getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(long userLevelId) {
        this.userLevelId = userLevelId;
    }
}

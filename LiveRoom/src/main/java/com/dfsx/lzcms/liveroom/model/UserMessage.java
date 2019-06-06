package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuwb on 2017/7/4.
 */
public class UserMessage extends LiveMessage {

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
    @SerializedName("is_special")
    private boolean isSpecial;
    /**
     * 欢迎信息
     */
    private String message;
    /**
     * 当前用户总数
     */
    @SerializedName("current_user_count")
    private int currentUserCount;


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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCurrentUserCount() {
        return currentUserCount;
    }

    public void setCurrentUserCount(int currentUserCount) {
        this.currentUserCount = currentUserCount;
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
        return userNickName + "进入直播间";
    }

    public long getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(long userLevelId) {
        this.userLevelId = userLevelId;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }
}

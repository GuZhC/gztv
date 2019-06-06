package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 图文消息
 * Created by liuwb on 2017/7/4.
 */
public class ImageTextMessage extends LiveMessage {

    private String text;
    @SerializedName("image_urls")
    private List<String> imageList;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

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

    public long getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(long userLevelId) {
        this.userLevelId = userLevelId;
    }
}

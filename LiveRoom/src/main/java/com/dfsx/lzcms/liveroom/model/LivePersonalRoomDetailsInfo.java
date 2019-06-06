package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2017/7/4.
 */
public class LivePersonalRoomDetailsInfo implements Serializable {

    /**
     * password : false
     * total_coins : 0
     * start_time : 1499155048
     * creation_time : 1499155047
     * playback_state : 1
     * privacy : false
     * title : 张三1的直播
     * cover_id : 0
     * owner_id : 2
     * cover_url :
     * plan_start_time : 1499155050
     * screen_mode : 2
     * introduction : 张三1的直播
     * current_visitor_count : 0
     * category_key : default
     * owner_username : zhangsan
     * owner_nickname : 张三1
     * owner_avatar_url : http://192.168.6.32:8101/general/pictures/20170704/6CD3B5C2CE1E32779773B154DFD7F3E8/6CD3B5C2CE1E32779773B154DFD7F3E8.jpg
     * category_name : 默认分类
     * id : 1060475
     * state : 2
     * type : 1
     */

    private boolean password;
    @SerializedName("total_coins")
    private int totalCoins;
    @SerializedName("start_time")
    private long startTime;
    @SerializedName("creation_time")
    private long creationTime;
    @SerializedName("playback_state")
    private int playbackState;
    private boolean privacy;
    private String title;
    @SerializedName("cover_id")
    private long coverId;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("cover_url")
    private String coverUrl;
    @SerializedName("plan_start_time")
    private long planStartTime;
    @SerializedName("screen_mode")
    private int screenMode;
    private String introduction;
    @SerializedName("current_visitor_count")
    private long currentVisitorCount;
    @SerializedName("category_key")
    private String categoryKey;
    @SerializedName("owner_username")
    private String ownerUserName;
    @SerializedName("owner_nickname")
    private String ownerNickName;
    @SerializedName("owner_avatar_url")
    private String ownerAvatarUrl;
    @SerializedName("category_name")
    private String categoryName;
    private long id;
    private int state;
    private int type;
    @SerializedName("rtmp_url")
    private String rtmpUrl;

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(int totalCoins) {
        this.totalCoins = totalCoins;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public int getPlaybackState() {
        return playbackState;
    }

    public void setPlaybackState(int playbackState) {
        this.playbackState = playbackState;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCoverId() {
        return coverId;
    }

    public void setCoverId(long coverId) {
        this.coverId = coverId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public long getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(long planStartTime) {
        this.planStartTime = planStartTime;
    }

    public int getScreenMode() {
        return screenMode;
    }

    public void setScreenMode(int screenMode) {
        this.screenMode = screenMode;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public long getCurrentVisitorCount() {
        return currentVisitorCount;
    }

    public void setCurrentVisitorCount(long currentVisitorCount) {
        this.currentVisitorCount = currentVisitorCount;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public String getOwnerNickName() {
        return ownerNickName;
    }

    public void setOwnerNickName(String ownerNickName) {
        this.ownerNickName = ownerNickName;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }
}

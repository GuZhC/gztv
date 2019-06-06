package com.dfsx.lzcms.liveroom.model;

import android.text.TextUtils;
import com.dfsx.lzcms.liveroom.view.IMultilineVideo;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/7/5.
 */
public class LiveServiceDetailsInfo implements Serializable {


    /**
     * password : false
     * total_coins : 0
     * interaction_plugins : chat,send-gift,image-text,intro
     * start_time : 1499248397
     * creation_time : 1499248206
     * playback_state : 1
     * privacy : false
     * title : 第一个活动直播
     * cover_id : 0
     * owner_id : 2
     * cover_url : http://192.168.6.32:8101/general/pictures/20170704/6CD3B5C2CE1E32779773B154DFD7F3E8/6CD3B5C2CE1E32779773B154DFD7F3E8.jpg
     * plan_start_time : 1499162400
     * screen_mode : 1
     * introduction : null
     * current_visitor_count : 0
     * category_key : default
     * owner_username : zhangsan
     * owner_nickname : 张三1
     * owner_avatar_url : http://192.168.6.32:8101/general/pictures/20170704/6CD3B5C2CE1E32779773B154DFD7F3E8/6CD3B5C2CE1E32779773B154DFD7F3E8.jpg
     * category_name : 默认分类
     * id : 1065536
     * state : 2
     * type : 2
     */

    private boolean password;
    @SerializedName("total_coins")
    private int totalCoins;
    @SerializedName("interaction_plugins")
    private String interactionPlugins;
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
    private Object introduction;
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
    @SerializedName("output_streams")
    private List<LiveServiceMultiline> multilineList;

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

    public String getInteractionPlugins() {
        return interactionPlugins;
    }

    public void setInteractionPlugins(String interactionPlugins) {
        this.interactionPlugins = interactionPlugins;
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

    public void setCreationTime(int creationTime) {
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

    public Object getIntroduction() {
        return introduction;
    }

    public void setIntroduction(Object introduction) {
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

    public List<LiveServiceMultiline> getMultilineList() {
        return multilineList;
    }

    public void setMultilineList(List<LiveServiceMultiline> multilineList) {
        this.multilineList = multilineList;
    }

    public static class LiveServiceMultiline implements Serializable, IMultilineVideo {
        private long id;
        private String name;
        private String videoUrl;

        private boolean isSelected;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String getLineTitle() {
            return "线路";
        }

        @Override
        public List<String> getVideoUrlList() {
            if (!TextUtils.isEmpty(videoUrl)) {
                List<String> urlList = new ArrayList<>();
                urlList.add(videoUrl);
                return urlList;
            }
            return null;
        }

        @Override
        public List<Long> getVideoDurationList() {
            return null;
        }

        @Override
        public void setVideoUrlList(List<String> urlList) {
            if (urlList != null && !urlList.isEmpty()) {
                videoUrl = urlList.get(0);
            }
        }

        @Override
        public boolean isSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }
}

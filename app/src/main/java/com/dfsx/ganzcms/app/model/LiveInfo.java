package com.dfsx.ganzcms.app.model;

import com.dfsx.lzcms.liveroom.business.AppManager;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/7/3.
 */
public class LiveInfo implements Room, ILiveService {

    /**
     * password : false
     * creation_time : 1499054241
     * start_time : null
     * playback_state : 1
     * privacy : false
     * title : 张三1的直播
     * cover_id : 0
     * owner_id : 2
     * cover_url :
     * plan_start_time : 1499054243
     * screen_mode : 2
     * introduction : 张三1的直播
     * current_visitor_count : 0
     * category_key : default
     * owner_username : zhangsan
     * owner_nickname : 张三1
     * category_name : 默认分类
     * “screen_mode”: <int, 屏幕显示模式 1-横屏 2-竖屏>,
     * owner_avatar_url : http://192.168.6.32:8101/general/pictures/20170522/5217E08EDC5B97C83D8B054CA37789EB/5217E08EDC5B97C83D8B054CA37789EB.png
     * id : 1037123
     * state : 1
     * type : 1
     */

    private boolean password;
    @SerializedName("creation_time")
    private long creationTime;
    private Long startTime;
    @SerializedName("playback_state")
    private int playbackState;
    private boolean privacy;
    private String title;
    @SerializedName("cover_id")
    private long coverId;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("owner_level_id")
    private long ownerLevelId;
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
    @SerializedName("category_name")
    private String categoryName;
    @SerializedName("owner_avatar_url")
    private String ownerAvatarUrl;
    private long id;
    private int state;
    private int type;
    private List<String> tags;


    private boolean isSelected;

    private ArrayList<ILiveInputStream> liveInputStreamList;


    @Override
    public String getRoomTitle() {
        return title;
    }

    @Override
    public String getRoomStatus() {
        String status = "";
        if (state == 1) {
            status = "预告";
        } else if (state == 2) {
            status = "直播中";
        } else if (state == 3) {
            status = "回放";
        } else {
            status = "其他";
        }
        return status;
    }

    @Override
    public String getRoomImagePath() {
        return coverUrl;
    }

    @Override
    public String getRoomOwnerName() {
        return ownerNickName;
    }

    @Override
    public long getRoomOwnerId() {
        return ownerId;
    }

    @Override
    public long getRoomOwnerLevelId() {
        return getOwnerLevelId();
    }

    @Override
    public String getRoomOwnerLogo() {
        return ownerAvatarUrl;
    }

    @Override
    public long getRoomMessageCount() {
        return currentVisitorCount;
    }

    @Override
    public long getRoomTime() {
        return getCreationTime();
    }

    @Override
    public long getRoomId() {
        return id;
    }

    @Override
    public LiveType getRoomLivetype() {
        return type == 1 ? LiveType.PersonalLive : LiveType.EventLive;
    }

    @Override
    public int getRoomFlag() {
        if (isPrivacy()) {
            return FLAG_PRIVACY;
        } else if (state == 1) {
            return FLAG_YUGAO;
        } else if (state == 2) {
            return FLAG_LIVE_ROOM;
        } else {
            return FLAG_BACK_PLAY;
        }
    }

    @Override
    public boolean isNeedRoomPassword() {
        return isPassword();
    }

    @Override
    public boolean isOnlyUserRoom() {
        return getRoomOwnerId() == AppManager.getInstance().getIApp().getLoginUserId();
    }

    @Override
    public String[] getRoomLabel() {
        if (tags != null) {
            return tags.toArray(new String[0]);
        }
        return null;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public long getServiceId() {
        return getId();
    }

    @Override
    public String getServiceTitle() {
        return getTitle();
    }

    @Override
    public ArrayList<ILiveInputStream> getLiveInputStreamList() {
        return liveInputStreamList;
    }

    @Override
    public void setLiveInputStream(ArrayList<ILiveInputStream> inputStreamList) {
        this.liveInputStreamList = inputStreamList;
    }

    public long getOwnerLevelId() {
        return ownerLevelId;
    }

    public void setOwnerLevelId(long ownerLevelId) {
        this.ownerLevelId = ownerLevelId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

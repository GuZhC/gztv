package com.dfsx.ganzcms.app.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/10/10.
 */
public class LiveRoomInfo implements Room {

    /**
     * id : 频道ID号
     * type : 频道类型: 1 –个人, 2 – 节目单 3- 竟猜
     * owner_id : 所属人ID
     * owner_username : 所属人用户名
     * owner_nickname
     * owner_avatar_url : 所属人头像地址
     * title : 频道标题
     * introduction : 频道简介
     * category_key : 所属分类Key
     * category_name : 所属分类名称
     * cover_id : 封面图片ID
     * cover_url : 封面图片地址
     * creation_time : 频道创建时间
     * max_visitor_count : 2556
     * state : 频道状态：1 – 未直播，2 – 正在直播, 3 – 已关闭
     * current_visitor_count : 当前访问人数
     * “flags”: <int, 频道标记，按位运算，1 – 推荐>,
     * “password”:< bool, 是否需要密码>
     * “total_coins”: <double, 频道主播总共收到的虚拟币总额>
     */

    private long id;
    private int type;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("owner_username")
    private String ownerUsername;
    @SerializedName("owner_nickname")
    private String ownerNickname;
    @SerializedName("owner_avatar_url")
    private String ownerAvatarUrl;
    private String title;
    private String introduction;
    @SerializedName("category_key")
    private String categoryKey;
    @SerializedName("category_name")
    private String categoryName;
    @SerializedName("cover_id")
    private long coverId;
    @SerializedName("cover_url")
    private String coverUrl;
    @SerializedName("creation_time")
    private long creationTime;
    @SerializedName("max_visitor_count")
    private long maxVisitorCount;
    private int state;
    @SerializedName("current_visitor_count")
    private long currentVisitorCount;
    @SerializedName("channel_id")
    private long channelId;
    @SerializedName("start_time")
    private long startTime;
    private int flags;
    @SerializedName("password")
    private boolean isPassword;
    @SerializedName("total_coins")
    private double totalCoins;

    /**
     * 是否只是某一个人的房间信息
     * 用于个人页的信息展示
     */
    private boolean isOnlyUserRoom;

    /**
     * 所属分类的详情
     */
    private Category category;

    /**
     * 当前房间所属分类的权限
     */
    private CategoryPermission categoryPermission;

    /**
     * 直播类型，直接与界面显示有联系
     * 通过权限来设置当前的直播类型
     */
    private LiveType liveType;

    public LiveRoomInfo() {

    }

    public LiveRoomInfo(long id, String title, LiveType liveType) {
        this.id = id;
        this.title = title;
        this.liveType = liveType;
    }

    public long getId() {
        return id == 0 ? channelId : id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getMaxVisitorCount() {
        return maxVisitorCount;
    }

    public void setMaxVisitorCount(long maxVisitorCount) {
        this.maxVisitorCount = maxVisitorCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getCurrentVisitorCount() {
        return currentVisitorCount;
    }

    public void setCurrentVisitorCount(long currentVisitorCount) {
        this.currentVisitorCount = currentVisitorCount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LiveType getLiveType() {
        if (liveType == null) {
            if (type == 3) {
                liveType = LiveType.EventLive;
            } else {
                liveType = LiveType.PersonalLive;
            }
        }
        return liveType;
    }

    public void setLiveType(LiveType liveType) {
        this.liveType = liveType;
    }

    public CategoryPermission getCategoryPermission() {
        return categoryPermission;
    }

    public void setCategoryPermission(CategoryPermission categoryPermission) {
        this.categoryPermission = categoryPermission;
    }

    public long getChannelId() {
        return channelId == 0 ? id : channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String getRoomTitle() {
        return getTitle();
    }

    @Override
    public String getRoomStatus() {
        return "直播中";
    }

    @Override
    public String getRoomOwnerName() {
        return getOwnerNickname();
    }

    @Override
    public long getRoomOwnerId() {
        return getOwnerId();
    }

    @Override
    public long getRoomOwnerLevelId() {
        return 0;
    }

    @Override
    public String getRoomOwnerLogo() {
        return getOwnerAvatarUrl();
    }

    @Override
    public long getRoomMessageCount() {
        return getCurrentVisitorCount();
    }

    @Override
    public long getRoomTime() {
        return getStartTime();
    }

    @Override
    public long getRoomId() {
        return getId();
    }

    @Override
    public LiveType getRoomLivetype() {

        return getLiveType();
    }

    @Override
    public int getRoomFlag() {
        return FLAG_LIVE_ROOM;
    }

    @Override
    public boolean isNeedRoomPassword() {
        return isPassword();
    }

    @Override
    public boolean isOnlyUserRoom() {
        return isOnlyUserRoom;
    }

    @Override
    public String[] getRoomLabel() {
        ArrayList<String> testList = new ArrayList<>();
        testList.add("测试");
        return testList.toArray(new String[0]);
    }

    @Override
    public String getRoomImagePath() {
        return TextUtils.isEmpty(coverUrl) ? getOwnerAvatarUrl() : getCoverUrl();
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public boolean isPassword() {
        return isPassword;
    }

    public void setPassword(boolean password) {
        isPassword = password;
    }

    public String getOwnerNickname() {
        return TextUtils.isEmpty(ownerNickname) ? ownerUsername : ownerNickname;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public double getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(double totalCoins) {
        this.totalCoins = totalCoins;
    }

    public void setOnlyUserRoom(boolean onlyUserRoom) {
        isOnlyUserRoom = onlyUserRoom;
    }
}

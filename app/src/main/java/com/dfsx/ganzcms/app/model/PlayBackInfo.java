package com.dfsx.ganzcms.app.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 直播的回放
 * Created by liuwb on 2016/12/13.
 */
public class PlayBackInfo implements Room {
    /**
     * id : 2222222222222
     * show_id
     * “type”: <int, 频道类型: 1 – 个人, 2 – 节目单, 3- 竟猜>,
     * title : 回放标题
     * duration : 33333333333333332
     * thumb_id : 111111111111111100
     * thumb_url : 回放缩略图地址
     * play_times : 1111111111111111200
     * like_count : 1.1111111111111111E21
     * creation_time : 11111111111111110000
     * channel_id : 1111111111111111200
     * channel_title : 来源的频道标题
     * category_key : 所属分类Key
     * category_name : 所属分类名称
     * closed : true
     * flags : 1 <int, 回放标记，按位运算，1 – 推荐>
     * “total_coins”: <double, 频道主播总共收到的虚拟币总额,
     * “owner_username”: <string, 所属人用户名>,
     * “owner_nickname”:<string, 所属人昵称>,
     * “owner_avatar_url”: <string, 所属人头像地址>,
     * “privacy”:<bool, 是否公开回放，true(不公开)/false(公开)>
     */

    private long id;
    @SerializedName("show_id")
    private long showId;
    private int type;
    private String title;
    private long duration;
    @SerializedName("thumb_id")
    private long thumbId;
    @SerializedName("thumb_url")
    private String thumbUrl;
    @SerializedName("play_times")
    private long playTimes;
    @SerializedName("like_count")
    private long likeCount;
    @SerializedName("creation_time")
    private long creationTime;
    @SerializedName("channel_id")
    private long channelId;
    @SerializedName("channel_title")
    private String channelTitle;
    @SerializedName("category_key")
    private String categoryKey;
    @SerializedName("category_name")
    private String categoryName;
    private boolean closed;
    private int flags;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("owner_username")
    private String ownerUsername;
    @SerializedName("owner_nickname")
    private String ownerNickname;
    @SerializedName("owner_avatar_url")
    private String ownerAvatarUrl;
    @SerializedName("end_time")
    private long endTime;
    @SerializedName("begin_time")
    private long beginTime;
    @SerializedName("total_coins")
    private double totalCoins;
    @SerializedName("privacy")
    private boolean isPrivacy;

    /**
     * 是否只是某一个人的房间信息
     * 用于个人页的信息展示
     */
    private boolean isOnlyUserRoom;

    public PlayBackInfo() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getThumbId() {
        return thumbId;
    }

    public void setThumbId(long thumbId) {
        this.thumbId = thumbId;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public long getPlayTimes() {
        return playTimes;
    }

    public void setPlayTimes(long playTimes) {
        this.playTimes = playTimes;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public String getRoomTitle() {
        return getTitle();
    }

    @Override
    public String getRoomStatus() {
        return isClosed() ? "已关闭" : "可回放";
    }

    @Override
    public String getRoomImagePath() {
        return TextUtils.isEmpty(thumbUrl) ? getOwnerAvatarUrl() : getThumbUrl();
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
        return getPlayTimes();
    }

    @Override
    public long getRoomTime() {
        return getEndTime();
    }

    @Override
    public long getRoomId() {
        return getChannelId();
    }

    @Override
    public LiveType getRoomLivetype() {
        return type == 3 ? LiveType.EventLive
                : LiveType.PersonalLive;
    }

    @Override
    public int getRoomFlag() {
        return FLAG_BACK_PLAY;
    }

    @Override
    public boolean isNeedRoomPassword() {
        return false;
    }

    @Override
    public boolean isOnlyUserRoom() {
        return isOnlyUserRoom;
    }

    @Override
    public String[] getRoomLabel() {
        ArrayList<String> testList = new ArrayList<>();
        //        testList.add("娱乐");
        testList.add("测试");
        return testList.toArray(new String[0]);
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

    public String getOwnerNickname() {
        return ownerNickname;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public double getTotalCoins() {
        return totalCoins;
    }

    public void setTotalCoins(double totalCoins) {
        this.totalCoins = totalCoins;
    }

    public long getShowId() {
        return showId;
    }

    public void setShowId(long showId) {
        this.showId = showId;
    }

    public void setOnlyUserRoom(boolean onlyUserRoom) {
        isOnlyUserRoom = onlyUserRoom;
    }

    public boolean isPrivacy() {
        return isPrivacy;
    }

    public void setPrivacy(boolean privacy) {
        isPrivacy = privacy;
    }
}

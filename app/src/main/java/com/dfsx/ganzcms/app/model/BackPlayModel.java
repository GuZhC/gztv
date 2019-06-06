package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2016/10/28.
 */
public class BackPlayModel implements Serializable {

    /**
     * total : 1258
     * data : [{"id":1111,"title":"回放标题","duration":111256544,"thumb_id":111,"thumb_url":"回放缩略图地址","play_times":12,"like_count":1156,"creation_time":"创建时间","channel_id":56,"channel_title":"来源的频道标题","category_key":"所属分类Key","category_name":"所属分类名称","begin_time":"录制的开始时间","end_time":"录制的结束时间","closed":false}]
     */

    private long total;
    /**
     * id : 1111
     * title : 回放标题
     * duration : 111256544
     * thumb_id : 111
     * thumb_url : 回放缩略图地址
     * play_times : 12
     * like_count : 1156
     * creation_time : 创建时间
     * channel_id : 56
     * channel_title : 来源的频道标题
     * category_key : 所属分类Key
     * category_name : 所属分类名称
     * begin_time : 录制的开始时间
     * end_time : 录制的结束时间
     * closed : false
     */

    private List<BackPlayItem> data;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<BackPlayItem> getData() {
        return data;
    }

    public void setData(List<BackPlayItem> data) {
        this.data = data;
    }

    public static class BackPlayItem {
        private long id;
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
        private String creationTime;
        @SerializedName("channel_id")
        private long channelId;
        @SerializedName("channel_title")
        private String channelTitle;
        @SerializedName("category_key")
        private String categoryKey;
        @SerializedName("category_name")
        private String categoryName;
        @SerializedName("begin_time")
        private String beginTime;
        @SerializedName("end_time")
        private String endTime;
        private boolean closed;

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

        public String getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(String creationTime) {
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

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public boolean isClosed() {
            return closed;
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }
    }
}

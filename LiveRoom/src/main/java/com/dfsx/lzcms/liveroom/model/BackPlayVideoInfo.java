package com.dfsx.lzcms.liveroom.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/12/14.
 */
public class BackPlayVideoInfo implements Serializable, Comparable<BackPlayVideoInfo> {

    /**
     * start_time : 1481525340
     * file_url : http://192.168.6.15:8101/com.dfsx.nmip.live.domain.user/videos/live/bba6a83e-50fa-4535-9c5a-e85dcce29b95/0.flv
     * file_extra_urls : {"网站MP4":"http://192.168.6.15:8101/com.dfsx.nmip.live.domain.user/videos/2016-12-12/1189/0MP4.mp4","网站FLV":"http://192.168.6.15:8101/com.dfsx.nmip.live.domain.user/videos/2016-12-12/1189/0FLV.flv"}
     * stop_time : 1481525362
     * file_id : 1189
     * type : 1 <int, 录制文件类型：2 – 音视频流, 1 – 即时聊天流>
     */

    @SerializedName("start_time")
    private long startTime;
    @SerializedName("file_url")
    private String fileUrl;
    @SerializedName("file_extra_urls")
    private FileExtraUrlsBean fileExtraUrls;
    @SerializedName("stop_time")
    private long stopTime;
    @SerializedName("file_id")
    private long fileId;
    private int type = 2;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public FileExtraUrlsBean getFileExtraUrls() {
        return fileExtraUrls;
    }

    public void setFileExtraUrls(FileExtraUrlsBean fileExtraUrls) {
        this.fileExtraUrls = fileExtraUrls;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int compareTo(@NonNull BackPlayVideoInfo another) {
        return (int) (this.getStartTime() - another.getStartTime());
    }

    public static class FileExtraUrlsBean implements Serializable {
        /**
         * 网站MP4 : http://192.168.6.15:8101/com.dfsx.nmip.live.domain.user/videos/2016-12-12/1189/0MP4.mp4
         * 网站FLV : http://192.168.6.15:8101/com.dfsx.nmip.live.domain.user/videos/2016-12-12/1189/0FLV.flv
         */

        @SerializedName("网站MP4")
        private String webMp4Url;
        @SerializedName("网站FLV")
        private String webFLVUrl;
        @SerializedName("标清M3U8")
        private String standardVideoM3u8Url;
        @SerializedName("高清M3U8")
        private String highVideoM3u8Url;
        @SerializedName("标清")
        private String standardVideoUrl;

        public String getWebMp4Url() {
            return webMp4Url;
        }

        public void setWebMp4Url(String webMp4Url) {
            this.webMp4Url = webMp4Url;
        }

        public String getWebFLVUrl() {
            return webFLVUrl;
        }

        public void setWebFLVUrl(String webFLVUrl) {
            this.webFLVUrl = webFLVUrl;
        }

        public String getStandardVideoM3u8Url() {
            return standardVideoM3u8Url;
        }

        public void setStandardVideoM3u8Url(String standardVideoM3u8Url) {
            this.standardVideoM3u8Url = standardVideoM3u8Url;
        }

        public String getHighVideoM3u8Url() {
            return highVideoM3u8Url;
        }

        public void setHighVideoM3u8Url(String highVideoM3u8Url) {
            this.highVideoM3u8Url = highVideoM3u8Url;
        }

        public String getStandardVideoUrl() {
            return standardVideoUrl;
        }

        public void setStandardVideoUrl(String standardVideoUrl) {
            this.standardVideoUrl = standardVideoUrl;
        }

        public String getVideoM3u8Url() {
            if (!TextUtils.isEmpty(highVideoM3u8Url)) {
                return highVideoM3u8Url;
            }
            if (!TextUtils.isEmpty(standardVideoM3u8Url)) {
                return standardVideoM3u8Url;
            }
            return null;
        }
    }
}

package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by  heyang on 2017/3/13.   我的视频列表子项
 */


public class MyVideoItem implements Serializable {


    /**
     * id : -1
     * title :
     * introduction :
     * cover_id : -1
     * cover_url :
     * creation_time : 0
     * modification_time : 0
     * duration : 0
     * versions : [{"name":"","width":0,"height":0,"bitrate":0,"url":""}]
     */

    private long id;
    private String title;
    private String introduction;
    private long cover_id;
    private String cover_url;
    private long creation_time;
    private long modification_time;
    private long duration;

    /**
     * name :
     * width : 0
     * height : 0
     * bitrate : 0.0
     * url :
     */

    private List<VersionsBean> versions;

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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public long getCover_id() {
        return cover_id;
    }

    public void setCover_id(long cover_id) {
        this.cover_id = cover_id;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public long getModification_time() {
        return modification_time;
    }

    public void setModification_time(long modification_time) {
        this.modification_time = modification_time;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<VersionsBean> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionsBean> versions) {
        this.versions = versions;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    //define
    private String  videoUrl;

    public static class VersionsBean {
        private String name;
        private int width;
        private int height;
        private double bitrate;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public double getBitrate() {
            return bitrate;
        }

        public void setBitrate(double bitrate) {
            this.bitrate = bitrate;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

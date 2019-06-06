package com.dfsx.ganzcms.app.model;

import java.util.List;

public class VideoContent {


    /**
     * id : long, 视频ID
     * title : string, 视频标题
     * introduction : string, 视频简介
     * cover_url : string, 封面图片地址
     * creation_time : long, 创建时间
     * modification_time : long, 最后更新时间
     * duration : long, 视频时长，单位秒
     * versions : [{"name":"string, 版本名称","width":"int, 视频宽度","height":"int, 视频高度","bitrate":"double, 视频码率，单位KB/s","url":"string, 视频地址"}]
     */

    private long id;
    private String title;
    private String introduction;
    private String cover_url;
    private long creation_time;
    private long modification_time;
    private long duration;
    private List<VideoVersion> versions;

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

    public List<VideoVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<VideoVersion> versions) {
        this.versions = versions;
    }

    public static class VideoVersion {
        /**
         * name : string, 版本名称
         * width : int, 视频宽度
         * height : int, 视频高度
         * bitrate : double, 视频码率，单位KB/s
         * url : string, 视频地址
         */

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

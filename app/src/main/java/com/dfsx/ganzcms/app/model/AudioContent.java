package com.dfsx.ganzcms.app.model;

import java.util.List;

/**
 * CMS 音频内容
 */
public class AudioContent {


    /**
     * id : long, 音频ID
     * title : string, 音频标题
     * introduction : string, 音频简介
     * creation_time : long, 创建时间
     * modification_time : long, 最后更新时间
     * duration : long, 音频时长，单位秒
     * versions : [{"name":"string, 版本名称","sample_rate":"double, 音频采样率","bitrate":"double, 视频码率，单位KB/s","url":"string, 视频地址"}]
     */

    private long id;
    private String title;
    private String introduction;
    private long creation_time;
    private long modification_time;
    private long duration;
    private List<VersionsData> versions;

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

    public List<VersionsData> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionsData> versions) {
        this.versions = versions;
    }

    public static class VersionsData {
        /**
         * name : string, 版本名称
         * sample_rate : double, 音频采样率
         * bitrate : double, 视频码率，单位KB/s
         * url : string, 视频地址
         */

        private String name;
        private double sample_rate;
        private double bitrate;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getSample_rate() {
            return sample_rate;
        }

        public void setSample_rate(double sample_rate) {
            this.sample_rate = sample_rate;
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

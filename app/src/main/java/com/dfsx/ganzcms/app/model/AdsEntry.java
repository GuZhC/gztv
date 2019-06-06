package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 *  广告类
 *  Created by hy on 2017/10/10.
 */

public class AdsEntry implements Serializable {


    /**
     * id : -1
     * name :
     * duration : 0
     * description :
     * picture_url :
     * link_url :
     */

    private long id;
    private String name;
    private int duration;     // 无用，1.4版本不用这个，为兼容以前老版本
    private int skip_time;   //<int, 跳过时长(单位:秒),0-不能跳过
    private int show_type;   //int, 显示方式 0-全屏显示 1-半屏显示

    //列表广告
    private int  list_mode;  // 展示模式 0-单图 1-三图 2-大图 4-横幅
    private List<String> showpicturesList;

    private List<AdItem> adItems;


    private String picture_url="";   //无用，1.4版本不用这个，为兼容以前老版本
    private String link_url;         //无用，1.4版本不用这个

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSkip_time() {
        return skip_time;
    }

    public void setSkip_time(int skip_time) {
        this.skip_time = skip_time;
    }

    public int getShow_type() {
        return show_type;
    }

    public void setShow_type(int show_type) {
        this.show_type = show_type;
    }

    public List<AdItem> getAdItems() {
        return adItems;
    }

    public void setAdItems(List<AdItem> adItems) {
        this.adItems = adItems;
    }

    public int getList_mode() {
        return list_mode;
    }

    public void setList_mode(int list_mode) {
        this.list_mode = list_mode;
    }

    public List<String> getShowpicturesList() {
        return showpicturesList;
    }

    public void setShowpicturesList(List<String> showpicturesList) {
        this.showpicturesList = showpicturesList;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public static class AdItem implements Serializable {
        private long id;
        private String link_url;
        private String name;
        private int type; //int, 广告类型 1-视频 2-图片
        private int duration;
        private String picture_url;
        private VideoAdItem videoAdItem;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getLink_url() {
            return link_url;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPicture_url() {
            return picture_url;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void setPicture_url(String picture_url) {
            this.picture_url = picture_url;
        }

        public VideoAdItem getVideoAdItem() {
            return videoAdItem;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setVideoAdItem(VideoAdItem videoAdItem) {
            this.videoAdItem = videoAdItem;
        }
    }

    public static class VideoAdItem implements Serializable {
        private int duration;
        private VideoVersion versions;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public VideoVersion getVersions() {
            return versions;
        }

        public void setVersions(VideoVersion versions) {
            this.versions = versions;
        }
    }

    public static class VideoVersion implements Serializable {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

package com.dfsx.procamera.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2018-6-19
 */
public class ContentModeInfo implements Serializable {


    /**
     * id : -1
     * title :
     * cover_url :
     * author_id : -1
     * author_name :
     * author_nickname :
     * author_avatar_url :
     * geo_longitude : 0.0
     * geo_latitude : 0.0
     * geo_address :
     * creation_time : -1
     * view_count : -1
     * share_count : -1
     * like_count : -1
     * comment_count : -1
     * has_like : false
     * has_favorite : false
     * pre_content_id : -1
     * next_content_id : -1
     * duration : -1
     */

    private long id;
    private String title;
    private String cover_url;
    private long author_id;
    private String author_name;
    private String author_nickname;
    private String author_avatar_url;
    private double geo_longitude;
    private double geo_latitude;
    private String geo_address;
    private long creation_time;
    private long view_count;
    private long favorite_count;
    private long share_count;
    private long like_count;
    private long comment_count;
    private boolean has_like;
    private boolean has_favorite;
    private long pre_content_id;
    private long next_content_id;
    private long duration;
    private Versions versionspl;
    private boolean  isAttion=false;  //是否被关注

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

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(long author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public void setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public String getAuthor_avatar_url() {
        return author_avatar_url;
    }

    public void setAuthor_avatar_url(String author_avatar_url) {
        this.author_avatar_url = author_avatar_url;
    }

    public double getGeo_longitude() {
        return geo_longitude;
    }

    public void setGeo_longitude(double geo_longitude) {
        this.geo_longitude = geo_longitude;
    }

    public double getGeo_latitude() {
        return geo_latitude;
    }

    public void setGeo_latitude(double geo_latitude) {
        this.geo_latitude = geo_latitude;
    }

    public String getGeo_address() {
        return geo_address;
    }

    public void setGeo_address(String geo_address) {
        this.geo_address = geo_address;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public long getView_count() {
        return view_count;
    }

    public void setView_count(long view_count) {
        this.view_count = view_count;
    }

    public long getFavorite_count() {
        return favorite_count;
    }

    public boolean isAttion() {
        return isAttion;
    }

    public void setAttion(boolean attion) {
        isAttion = attion;
    }

    public void setFavorite_count(long favorite_count) {
        this.favorite_count = favorite_count;
    }

    public long getShare_count() {
        return share_count;
    }

    public void setShare_count(long share_count) {
        this.share_count = share_count;
    }

    public long getLike_count() {
        return like_count;
    }

    public void setLike_count(long like_count) {
        this.like_count = like_count;
    }

    public long getComment_count() {
        return comment_count;
    }

    public void setComment_count(long comment_count) {
        this.comment_count = comment_count;
    }

    public boolean isHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }

    public boolean isHas_favorite() {
        return has_favorite;
    }

    public void setHas_favorite(boolean has_favorite) {
        this.has_favorite = has_favorite;
    }

    public long getPre_content_id() {
        return pre_content_id;
    }

    public void setPre_content_id(long pre_content_id) {
        this.pre_content_id = pre_content_id;
    }

    public long getNext_content_id() {
        return next_content_id;
    }

    public void setNext_content_id(long next_content_id) {
        this.next_content_id = next_content_id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Versions getVersions() {
        return versionspl;
    }

    public void setVersions(Versions versions) {
        this.versionspl = versions;
    }


    public static class Versions implements  Serializable{

        /**
         * name :
         * width : 0
         * height : 0
         * bitrate : 0.0
         * url :
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


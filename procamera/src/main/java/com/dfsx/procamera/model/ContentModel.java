package com.dfsx.procamera.model;

import java.io.Serializable;

/**
 * Created by heyang on 2018-6-19 内容
 */
public class ContentModel implements Serializable {


    /**
     * id : -1
     * title :
     * cover_url :
     * author_id : -1
     * author_name :
     * author_nickname :
     * author_avatar_url :
     * creation_time : 0
     * view_count : 0
     * share_count : 0
     * like_count : 0
     * comment_count : 0
     */

    private long id;
    private String title;
    private String cover_url;
    private long author_id;
    private String author_name;
    private String author_nickname;
    private String author_avatar_url;
    private long creation_time;
    private long view_count;
    private long share_count;
    private long like_count;
    private long comment_count;

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
}


package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/12/7.
 */
public class RoomDetails implements Serializable {


    /**
     * id : 1111
     * type : 1
     * owner_id : 111111111
     * owner_username :  所属人用户名
     * owner_nickname :  所属用户昵称
     * owner_avatar_url :  所属人头像地址
     * title :  频道标题
     * introduction :  频道简介
     * category_key :  所属分类Key
     * category_name :  所属分类名称
     * cover_id : 1111111111111
     * cover_url :  封面图片地址
     * creation_time : 1000000000
     * max_ visitor_count : 111000
     * state : 1
     * current_visitor_count : 12345
     */

    private long id;
    private int type;
    private long owner_id;
    private String owner_username;
    private String owner_nickname;
    private String owner_avatar_url;
    private String title;
    private String introduction;
    private String category_key;
    private String category_name;
    private long cover_id;
    private String cover_url;
    private long creation_time;

    public long getId() {
        return id;
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

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_username() {
        return owner_username;
    }

    public void setOwner_username(String owner_username) {
        this.owner_username = owner_username;
    }

    public String getOwner_nickname() {
        return owner_nickname;
    }

    public void setOwner_nickname(String owner_nickname) {
        this.owner_nickname = owner_nickname;
    }

    public String getOwner_avatar_url() {
        return owner_avatar_url;
    }

    public void setOwner_avatar_url(String owner_avatar_url) {
        this.owner_avatar_url = owner_avatar_url;
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

    public String getCategory_key() {
        return category_key;
    }

    public void setCategory_key(String category_key) {
        this.category_key = category_key;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
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
}

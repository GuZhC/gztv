package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * 用户的频道信息
 * Created by liuwb on 2017/2/20.
 */
public class ChannelInfo implements Serializable {

    /**
     * flags : 0
     * creation_time : 1484813434
     * type : 1
     * cover_id : 0
     * poster_id : 0
     * title : 斯温的频道
     * current_visitor_count : 0
     * introduction : 斯温的频道
     * total_coins : 239
     * max_visitor_count : 4
     * cover_url :
     * category_key : default
     * owner_avatar_url : http://file.dfsxcms.cn:8101/general/pictures/20161221/A5BDF1DA7461009DC036A7BA73D11E71/A5BDF1DA7461009DC036A7BA73D11E71.jpg
     * poster_url :
     * owner_username : bowen
     * owner_id : 53
     * owner_nickname : 斯温
     * category_name : 默认分类
     * id : 95
     * state : 1
     */

    private int flags;
    private long creation_time;
    private int type;
    private long cover_id;
    private long poster_id;
    private String title;
    private int current_visitor_count;
    private String introduction;
    private int total_coins;
    private int max_visitor_count;
    private String cover_url;
    private String category_key;
    private String owner_avatar_url;
    private String poster_url;
    private String owner_username;
    private long owner_id;
    private String owner_nickname;
    private String category_name;
    private long id;
    private int state; //<int, 频道状态：1 – 未直播，2 – 正在直播, 3 – 已关闭>

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCover_id() {
        return cover_id;
    }

    public void setCover_id(long cover_id) {
        this.cover_id = cover_id;
    }

    public long getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(long poster_id) {
        this.poster_id = poster_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrent_visitor_count() {
        return current_visitor_count;
    }

    public void setCurrent_visitor_count(int current_visitor_count) {
        this.current_visitor_count = current_visitor_count;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getTotal_coins() {
        return total_coins;
    }

    public void setTotal_coins(int total_coins) {
        this.total_coins = total_coins;
    }

    public int getMax_visitor_count() {
        return max_visitor_count;
    }

    public void setMax_visitor_count(int max_visitor_count) {
        this.max_visitor_count = max_visitor_count;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getCategory_key() {
        return category_key;
    }

    public void setCategory_key(String category_key) {
        this.category_key = category_key;
    }

    public String getOwner_avatar_url() {
        return owner_avatar_url;
    }

    public void setOwner_avatar_url(String owner_avatar_url) {
        this.owner_avatar_url = owner_avatar_url;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getOwner_username() {
        return owner_username;
    }

    public void setOwner_username(String owner_username) {
        this.owner_username = owner_username;
    }

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_nickname() {
        return owner_nickname;
    }

    public void setOwner_nickname(String owner_nickname) {
        this.owner_nickname = owner_nickname;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

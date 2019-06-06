package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2016/10/24  栏目实体类
 */
public class ColumnEntry implements Serializable {

    /**
     * id : 0
     * parent_id : 0
     * name :
     * description :
     * type : 0
     * latest_thread_id : 0
     * latest_thread_title :
     * latest_thread_author_id :
     * latest_thread_author_name :
     * latest_thread_post_time :
     * weight : 0
     * can_post : false
     * can_post_reply : false
     * admins : [{"id":0,"username":"","nickname":""},"\u2026\u2026"]
     */

    private long id;
    private long parent_id;
    private String name;
    private String description;
    private int type;
    private long latest_thread_id;
    private String latest_thread_title;
    private String latest_thread_author_id;
    private String latest_thread_author_name;
    private String latest_thread_post_time;
    private int weight;
    private boolean can_post;
    private boolean can_post_reply;
    /**
     * id : 0
     * username :
     * nickname :
     */

    private List<AdminsBean> admins;
    private List<String> tags;
    private String  icon_url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLatest_thread_id() {
        return latest_thread_id;
    }

    public void setLatest_thread_id(long latest_thread_id) {
        this.latest_thread_id = latest_thread_id;
    }

    public String getLatest_thread_title() {
        return latest_thread_title;
    }

    public void setLatest_thread_title(String latest_thread_title) {
        this.latest_thread_title = latest_thread_title;
    }

    public String getLatest_thread_author_id() {
        return latest_thread_author_id;
    }

    public void setLatest_thread_author_id(String latest_thread_author_id) {
        this.latest_thread_author_id = latest_thread_author_id;
    }

    public String getLatest_thread_author_name() {
        return latest_thread_author_name;
    }

    public void setLatest_thread_author_name(String latest_thread_author_name) {
        this.latest_thread_author_name = latest_thread_author_name;
    }

    public String getLatest_thread_post_time() {
        return latest_thread_post_time;
    }

    public void setLatest_thread_post_time(String latest_thread_post_time) {
        this.latest_thread_post_time = latest_thread_post_time;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isCan_post() {
        return can_post;
    }

    public void setCan_post(boolean can_post) {
        this.can_post = can_post;
    }

    public boolean isCan_post_reply() {
        return can_post_reply;
    }

    public void setCan_post_reply(boolean can_post_reply) {
        this.can_post_reply = can_post_reply;
    }

    public List<AdminsBean> getAdmins() {
        return admins;
    }

    public void setAdmins(List<AdminsBean> admins) {
        this.admins = admins;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public static class AdminsBean implements  Serializable{
        private long id;
        private String username;
        private String nickname;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}


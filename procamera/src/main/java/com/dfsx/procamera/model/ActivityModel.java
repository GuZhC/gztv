package com.dfsx.procamera.model;

import java.io.Serializable;

/**
 * Created by heyang on 2018-6-19  活动
 */
public class ActivityModel  implements Serializable {


    /**
     * id : 0
     * name :
     * poster_id : 0
     * poster_url :
     * flag_id : 0
     * flag_name :
     * content_count : 0
     * creation_time : 0
     */

    private long id;
    private String name;
    private long poster_id;
    private String poster_url;
    private long flag_id;
    private String flag_name;
    private String flag_icon_url;
    private int state;
    private int content_count;
    private long creation_time;

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

    public long getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(long poster_id) {
        this.poster_id = poster_id;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public long getFlag_id() {
        return flag_id;
    }

    public void setFlag_id(long flag_id) {
        this.flag_id = flag_id;
    }

    public String getFlag_name() {
        return flag_name;
    }

    public void setFlag_name(String flag_name) {
        this.flag_name = flag_name;
    }

    public int getContent_count() {
        return content_count;
    }

    public void setContent_count(int content_count) {
        this.content_count = content_count;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public String getFlag_icon_url() {
        return flag_icon_url;
    }

    public void setFlag_icon_url(String flag_icon_url) {
        this.flag_icon_url = flag_icon_url;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}


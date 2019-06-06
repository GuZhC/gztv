package com.dfsx.procamera.model;
import java.io.Serializable;

/**
 * Created by heyang on 2017/1/2   获取内容
 */
public class ActivityInfoEntry implements Serializable {


    /**
     * id : -1
     * name :
     * intro :
     * rule :
     * poster_id : 0
     * poster_url :
     * video_upload_duration : 0
     * flag_id : 0
     * flag_name :
     * start_time : 0
     * stop_time : 0
     * content_count : 0
     * creation_time : 0
     */

    private long id;
    private String name;
    private String intro;
    private String rule;
    private long poster_id;
    private String poster_url;
    private int video_upload_duration;
    private long flag_id;
    private String flag_name;
    private long start_time;
    private long stop_time;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
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

    public int getVideo_upload_duration() {
        return video_upload_duration;
    }

    public void setVideo_upload_duration(int video_upload_duration) {
        this.video_upload_duration = video_upload_duration;
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

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getStop_time() {
        return stop_time;
    }

    public void setStop_time(long stop_time) {
        this.stop_time = stop_time;
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
}
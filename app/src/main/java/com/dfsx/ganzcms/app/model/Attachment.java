package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * Created by heyang on 2016/11/2.
 */
public class Attachment implements Serializable {

    /**
     * id : 0
     * type : 1
     * name :
     * url :
     */

    private long id;
    private int type;   //1；图片  2:文件
    private String name;
    private String url;
    private String frame;
    private int width;
    private int height;
    private String thumbnail_url;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

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

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }
}

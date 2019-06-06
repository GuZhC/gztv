package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/9/6.
 */
public class ImageNewsChannel implements Serializable {

    private long id;//key
    private String img;
    private String text;
    private String title;

    public ImageNewsChannel() {

    }

    public ImageNewsChannel(String img, String text) {
        this.img = img;
        this.text = text;
    }

    public ImageNewsChannel(long id, String img, String text) {
        this.id = id;
        this.img = img;
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

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
}

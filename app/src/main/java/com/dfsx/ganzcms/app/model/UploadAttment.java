package com.dfsx.ganzcms.app.model;

import com.dfsx.selectedmedia.MediaModel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by heyang on 2017/7/31
 */
public class UploadAttment<T extends Serializable> implements Serializable {

    public long id;
    public String title;
    public String content;
    public ArrayList<MediaModel> attments;
    public T param;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int  type;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<MediaModel> getAttments() {
        return attments;
    }

    public void setAttments(ArrayList<MediaModel> attments) {
        this.attments = attments;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

}

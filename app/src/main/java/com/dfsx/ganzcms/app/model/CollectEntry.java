package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2016/12/5 栏目实体类
 */
public class CollectEntry extends TopicalEntry implements Serializable {

    /**
     * id : 0
     * item_source :
     * item_type :
     * item_id : -1
     * creation_time : 0
     */

    private long cid;
    private int showType;
//    private int type;
    private String item_source;
    private String item_type;
    private long item_id;
    private long creation_time;
    private long commendNumber;
    private String url;
    private String thumbImg;
    private List<String> thumbnail_urls;
    private List<ContentCmsInfoEntry.GroupImgsBean> groupimgs;
    private int  modeType;


    public String getThumbImg() {
        return thumbImg;
    }

    public void setThumbImg(String thumbImg) {
        this.thumbImg = thumbImg;
    }

    public List<String> getThumbnail_urls() {
        return thumbnail_urls;
    }

    public void setThumbnail_urls(List<String> thumbnail_urls) {
        this.thumbnail_urls = thumbnail_urls;
    }

    public long getCommendNumber() {
        return commendNumber;
    }

    public void setCommendNumber(long commendNumber) {
        this.commendNumber = commendNumber;
    }

//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public long getId() {
        return cid;
    }

    public void setId(long id) {
        this.cid = id;
    }

    public String getItem_source() {
        return item_source;
    }

    public void setItem_source(String item_source) {
        this.item_source = item_source;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public long getItem_id() {
        return item_id;
    }

    public void setItem_id(long item_id) {
        this.item_id = item_id;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public List<ContentCmsInfoEntry.GroupImgsBean> getGroupimgs() {
        return groupimgs;
    }

    public void setGroupimgs(List<ContentCmsInfoEntry.GroupImgsBean> groupimgs) {
        this.groupimgs = groupimgs;
    }

    public int getModeType() {
        return modeType;
    }

    public void setModeType(int modeType) {
        this.modeType = modeType;
    }
}


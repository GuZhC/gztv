package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * Created by heyang on 2017/7/13.
 * 保存 点赞 和 点踩的实体类
 */
public class PriseModel implements Serializable {

    long id;
    boolean isPraise;  //是否被赞
    boolean isStrmp;  //是否被踩
    boolean isRead;    //是否浏览过
    int  isAttion;  //是否关注     //0:没关注  1：关注
    boolean isFav;      //是否收藏

    public PriseModel(long nid, boolean isPraises, boolean isStramps,boolean isRed) {
        this.id = nid;
        this.isPraise = isPraises;
        this.isStrmp = isStramps;
        this.isRead=isRed;
    }

    public PriseModel(long nid,int isAttion,boolean isAfv) {
        this.id = nid;
        this.isAttion = isAttion;
        this.isFav=isAfv;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setIsPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isStrmp() {
        return isStrmp;
    }

    public void setIsStrmp(boolean isStrmp) {
        this.isStrmp = isStrmp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public int isAttion() {
        return isAttion;
    }

    public void setAttion(int attion) {
        isAttion = attion;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }
}



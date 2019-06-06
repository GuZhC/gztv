package com.dfsx.selectedmedia;/*
 *  heyang  2015-11-23  查询遍历图片 视频的保存的实体
 */


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MediaModel implements Parcelable,Serializable {

    public String name;
    public String url = null;
    public int type = 0;
    public double latf = 0.0f;
    public double longf = 0.0f;
    public boolean status = false;
    public int duration = 0;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getLatf() {
        return latf;
    }

    public void setLatf(double latf) {
        this.latf = latf;
    }

    public double getLongf() {
        return longf;
    }

    public void setLongf(double longf) {
        this.longf = longf;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    private String  thumb;


    public MediaModel() {
        name = "";
        url = "";
        type = 0;
        latf = 0.0f;
        longf = 0.0f;
        status = false;
        duration = 0;
        thumb="";
    }

    public MediaModel(String na, String urls, int dur) {
        this.name = na;
        this.url = urls;
        this.duration = dur;
    }

    public MediaModel(String na, String path) {
        this.name = na;
        this.url = path;
    }


    public MediaModel(String url, boolean status) {
        this.url = url;
        this.status = status;
    }

    public MediaModel(String url, int ty) {
        this.url = url;
        this.type = ty;
    }

    public MediaModel(String url, double lat, double log, boolean status) {
        this.url = url;
        this.latf = lat;
        this.longf = log;
        this.status = status;
    }

    public MediaModel(String url, double lat, double log, boolean status, int dur) {
        this.url = url;
        this.latf = lat;
        this.longf = log;
        this.status = status;
        this.duration = dur;
    }

    public MediaModel(String url,int  ty, boolean status, int dur) {
        this.url = url;
        this.type=ty;
        this.status = status;
        this.duration = dur;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(name);
        out.writeString(url);
        out.writeInt(type);
        out.writeDouble(latf);
        out.writeDouble(longf);
        out.writeInt(status == true ? 1 : 0);
        out.writeInt(duration);
        out.writeString(thumb);
    }


    public static final Parcelable.Creator<MediaModel> CREATOR = new Parcelable.Creator<MediaModel>() {
        @Override
        public MediaModel createFromParcel(Parcel in) {
            MediaModel mediaModel = new MediaModel();
            mediaModel.name = in.readString();
            mediaModel.url = in.readString();
            mediaModel.type = in.readInt();
            mediaModel.latf = in.readDouble();
            mediaModel.longf = in.readDouble();
            mediaModel.status = in.readInt() == 1 ? true : false;
            mediaModel.duration = in.readInt();
            mediaModel.thumb = in.readString();
            return mediaModel;
        }

        @Override
        public MediaModel[] newArray(int size) {
            return new MediaModel[size];
        }
    };

}

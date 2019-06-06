package com.dfsx.ganzcms.app.model;
/*
 *  heyang  2015-11-23  查询遍历图片 视频的保存的实体
 */


import android.os.Parcel;
import android.os.Parcelable;

public class MediaModel implements Parcelable {

	public String url = null;
    public double  latf=0.0f;
    public double  longf=0.0f;
	public boolean status = false;
    public int  duration=0;

    public MediaModel() {
        url="";
        latf=0.0f;
        longf=0.0f;
        status=false;
        duration=0;
    }

	public MediaModel(String url, boolean status) {
        this.url = url;
        this.status = status;
    }

    public MediaModel(String url,double  lat,double log, boolean status) {
        this.url = url;
        this.latf=lat;
        this.longf=log;
        this.status = status;
    }

    public MediaModel(String url,double  lat,double log, boolean status,int dur) {
        this.url = url;
        this.latf=lat;
        this.longf=log;
        this.status = status;
        this.duration=dur;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(url);
        out.writeDouble(latf);
        out.writeDouble(longf);
        out.writeInt(status==true?1:0);
        out.writeInt(duration);
    }


    public static final Creator<MediaModel> CREATOR = new Creator<MediaModel>() {
        @Override
        public MediaModel createFromParcel(Parcel in) {
            MediaModel mediaModel = new MediaModel();
            mediaModel.url = in.readString();
            mediaModel.latf=in.readDouble();
            mediaModel.longf=in.readDouble();
            mediaModel.status = in.readInt()==1?true:false;
            mediaModel.duration=in.readInt();
            return mediaModel;
        }

        @Override
        public MediaModel[] newArray(int size) {
            return new MediaModel[size];
        }
    };

}

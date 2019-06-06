package com.dfsx.ganzcms.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by halim on 2015/8/5.
 */
public class SingleRecord implements Parcelable {


    public long id;
    public String update_time;
    public String channelName;
    public String url;
    public String thumb;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getThumb() {
        return this.thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(channelName);
        out.writeString(url);
        out.writeString(thumb);

    }

    public static final Creator<SingleRecord> CREATOR = new Creator<SingleRecord>() {
        @Override
        public SingleRecord createFromParcel(Parcel in) {
            SingleRecord channel = new SingleRecord();
            channel.id = in.readLong();
            channel.channelName = in.readString();
            channel.url = in.readString();
            channel.thumb = in.readString();
            return channel;
        }

        @Override
        public SingleRecord[] newArray(int size) {
            return new SingleRecord[size];
        }
    };


}

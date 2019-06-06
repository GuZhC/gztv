package com.dfsx.ganzcms.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by zr on 2015-04-02.
 */
final public class LiveEntity implements Serializable {

    static public class LiveChannel implements Parcelable, Serializable {
        public long channelID;
        public long columnId;
        public String channelName;
        public String content;
        public String url;
        public String thumb;
        public String frequency;
        public String creaTime;
        public String stopTime;
        public boolean isPlayback = false;  // 是不是回看
        public boolean isLive = false;   //是不是直播
        public boolean isValid=true;   // url是不是

        public int describeContents() {
            return 0;
        }

        public LiveChannel() {
        }

        public LiveChannel(String paht) {
            this.url = paht;
        }

        public LiveChannel(long channelID, String channelName, String content,
                           String url, String thumb, String frequency) {
            this.channelID = channelID;
            this.channelName = channelName;
            this.content = content;
            this.url = url;
            this.thumb = thumb;
            this.frequency = frequency;
        }

        public LiveChannel(long channelID, String channelName, String content,
                           String url, String thumb, String time, String s) {
            this.channelID = channelID;
            this.channelName = channelName;
            this.content = content;
            this.url = url;
            this.thumb = thumb;
            this.creaTime = time;
        }

        public LiveChannel(long channelID, long colId,String channelName, String content,
                           String url, String thumb) {
            this.channelID = channelID;
            this.columnId=colId;
            this.channelName = channelName;
            this.content = content;
            this.url = url;
            this.thumb = thumb;
            this.frequency = frequency;
        }

        public LiveChannel(String url, String channelName, String content, String time, String endtime) {
            this.url = url;
            this.channelName = channelName;
            this.content = content;
            this.creaTime = time;
            this.stopTime = endtime;
        }


        public void writeToParcel(Parcel out, int flags) {
            out.writeLong(channelID);
            out.writeString(channelName);
            out.writeString(content);
            out.writeString(url);
            out.writeString(thumb);
            out.writeString(frequency);
            out.writeString(creaTime);
            out.writeString(stopTime);
        }

        public static final Creator<LiveChannel> CREATOR = new Creator<LiveChannel>() {
            @Override
            public LiveChannel createFromParcel(Parcel in) {
                LiveChannel channel = new LiveChannel();
                channel.channelID = in.readLong();
                channel.channelName = in.readString();
                channel.content = in.readString();
                channel.url = in.readString();
                channel.thumb = in.readString();
                channel.frequency = in.readString();
                channel.creaTime = in.readString();
                channel.stopTime = in.readString();
                return channel;
            }

            @Override
            public LiveChannel[] newArray(int size) {
                return new LiveChannel[size];
            }
        };

    }

}

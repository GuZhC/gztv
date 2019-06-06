package com.dfsx.ganzcms.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by heyang on 2015-04-07.
 */

public class SocirtyNewsChannel implements Parcelable, Serializable {
    public long id;
    public int typeId;   //0:news  1:video
    public String newsTitle;
    public String newsThumb;
    public String newsContent;
    public String newsTime;
    public int commengNumber;
    public int counter;
    public String author;
    public String athumbils;

    public SocirtyNewsChannel() {
        id = 0;
        typeId = 0;
        newsTitle = "";
        newsThumb = "";
        newsTime = "";
        newsContent = "";
        commengNumber = 0;
        counter = 0;
        author="";
        athumbils="";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeInt(typeId);
        out.writeString(newsTitle);
        out.writeString(newsThumb);
        out.writeString(newsContent);
        out.writeString(newsTime);
        out.writeInt(commengNumber);
        out.writeInt(counter);
        out.writeString(author);
        out.writeString(athumbils);
    }

    public static final Creator<SocirtyNewsChannel> CREATOR = new Creator<SocirtyNewsChannel>() {
        @Override
        public SocirtyNewsChannel createFromParcel(Parcel in) {
            SocirtyNewsChannel newsChannel = new SocirtyNewsChannel();
            newsChannel.id = in.readLong();
            newsChannel.typeId = in.readInt();
            newsChannel.newsTitle = in.readString();
            newsChannel.newsThumb = in.readString();
            newsChannel.newsContent = in.readString();
            newsChannel.newsTime = in.readString();
            newsChannel.commengNumber = in.readInt();
            newsChannel.counter = in.readInt();
            newsChannel.author = in.readString();
            newsChannel.athumbils = in.readString();
            return newsChannel;
        }

        @Override
        public SocirtyNewsChannel[] newArray(int size) {
            return new SocirtyNewsChannel[size];
        }
    };
}


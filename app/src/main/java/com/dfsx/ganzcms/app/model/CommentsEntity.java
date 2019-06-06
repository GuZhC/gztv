package com.dfsx.ganzcms.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by heyang on 2015-04-28.
 */
public class CommentsEntity {

    static public class  CommentsItem implements Parcelable, Serializable {
        public long id;
        public  String  username;
        public  String  userimg;
        public  String   site;
        public String strTitle;
        public String crateTime;
        public long number;

        public CommentsItem() {
            id = 0;
            username="";
            userimg="";
            site="";
            strTitle = "";
            crateTime = "";
            number=0;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeLong(id);
            out.writeString(username);
            out.writeString(userimg);
            out.writeString(site);
            out.writeString(strTitle);
            out.writeString(crateTime);
            out.writeLong(number);
        }

        public static final Creator<CommentsItem> CREATOR = new Creator<CommentsItem>() {
            @Override
            public CommentsItem createFromParcel(Parcel in) {
                CommentsItem newsChannel = new CommentsItem();
                newsChannel.id = in.readLong();
                newsChannel.username=in.readString();
                newsChannel.userimg=in.readString();
                newsChannel.site=in.readString();
                newsChannel.strTitle = in.readString();
                newsChannel.crateTime=in.readString();
                newsChannel.number=in.readLong();
                return newsChannel;
            }

            @Override
            public CommentsItem[] newArray(int size) {
                return new CommentsItem[size];
            }
        };
    }
}


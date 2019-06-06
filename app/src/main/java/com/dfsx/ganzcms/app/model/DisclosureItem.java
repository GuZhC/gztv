package com.dfsx.ganzcms.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by heyang on 2015-04-28.
 */
public class DisclosureItem implements Parcelable, Serializable {
    public long id;
    public long uId;
    public String userImag;
    public String username;
    public String title;
    public String content;
    public String images;
    public ArrayList<String> imgs;
    public long time;
    public int purerNum;
    public int commendNum;
    public int type;
    public int imgWidth;
    public int imgHeight;

    public DisclosureItem() {
        id = 0;
        uId = 0;
        userImag = "";
        username = "";
        title = "";
        content = "";
        images = "";
        imgs = null;
        time = 0;
        purerNum = 0;
        commendNum = 0;
        type = 0;
        imgWidth = 0;
        imgHeight = 0;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(uId);
        out.writeString(userImag);
        out.writeString(username);
        out.writeString(title);
        out.writeString(content);
        out.writeString(images);
        out.writeStringList(imgs);
        out.writeLong(time);
        out.writeInt(purerNum);
        out.writeInt(commendNum);
        out.writeInt(type);
        out.writeInt(imgWidth);
        out.writeInt(imgHeight);
    }

    public static final Creator<DisclosureItem> CREATOR = new Creator<DisclosureItem>() {
        @Override
        public DisclosureItem createFromParcel(Parcel in) {
            DisclosureItem dItem = new DisclosureItem();
            dItem.id = in.readLong();
            dItem.uId = in.readLong();
            dItem.userImag = in.readString();
            dItem.username = in.readString();
            dItem.title = in.readString();
            dItem.content = in.readString();
            dItem.images = in.readString();
            dItem.imgs = in.createStringArrayList();
            dItem.time = in.readLong();
            dItem.purerNum = in.readInt();
            dItem.commendNum = in.readInt();
            dItem.type = in.readInt();
            dItem.imgWidth = in.readInt();
            dItem.imgHeight = in.readInt();
            return dItem;
        }

        @Override
        public DisclosureItem[] newArray(int size) {
            return new DisclosureItem[size];
        }
    };
}


package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

public class NoTalkUser extends RoomPerson {


    @SerializedName("expired_time")
    private long expiredTime;

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }
}

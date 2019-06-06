package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2017/7/13.
 */
public class LiveInputStreamInfo implements Serializable {
    private long id;
    private String name;
    @SerializedName("flv_address")
    private String flvAddress;
    @SerializedName("m3u8_address")
    private String m3u8Address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlvAddress() {
        return flvAddress;
    }

    public void setFlvAddress(String flvAddress) {
        this.flvAddress = flvAddress;
    }

    public String getM3u8Address() {
        return m3u8Address;
    }

    public void setM3u8Address(String m3u8Address) {
        this.m3u8Address = m3u8Address;
    }
}

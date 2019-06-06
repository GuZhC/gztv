package com.dfsx.lzcms.liveroom.model;

import com.dfsx.lzcms.liveroom.view.IMultilineVideo;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播输出流信息
 * Created by liuwb on 2017/7/13.
 */
public class LiveOutputStreamInfo implements Serializable, IMultilineVideo {
    /**
     * flv_address : http://room.dfsxcms.cn:8100/live/live1058457-1499139739229.flv
     * m3u8_address : null
     * name : 个人直播线路
     */

    @SerializedName("flv_address")
    private String flvAddress;
    @SerializedName("m3u8_address")
    private String m3u8Address;
    private String name;
    private long id;
    private boolean isSelected;


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

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getLineTitle() {
        return "线路";
    }

    @Override
    public List<String> getVideoUrlList() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(flvAddress);
        return arrayList;
    }

    @Override
    public List<Long> getVideoDurationList() {
        return null;
    }

    @Override
    public void setVideoUrlList(List<String> urlList) {
        if (urlList != null && !urlList.isEmpty()) {
            flvAddress = urlList.get(0);
        }
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }
}

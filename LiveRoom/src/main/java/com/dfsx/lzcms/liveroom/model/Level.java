package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Level implements Serializable {
    private long id;
    private String name;
    @SerializedName("icon_id")
    private long iconId;
    @SerializedName("icon_url")
    private String iconUrl;
    @SerializedName("min_exp")
    private long minExp;
    @SerializedName("max_exp")
    private long maxExp;


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

    public long getIconId() {
        return iconId;
    }

    public void setIconId(long iconId) {
        this.iconId = iconId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public long getMinExp() {
        return minExp;
    }

    public void setMinExp(long minExp) {
        this.minExp = minExp;
    }

    public long getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(long maxExp) {
        this.maxExp = maxExp;
    }
}

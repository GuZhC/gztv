package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/10/10.
 */
public class Person implements Serializable {

    @SerializedName("user_id")
    private long id;
    @SerializedName("username")
    private String name;
    @SerializedName("nickname")
    private String nickName;
    @SerializedName("avatar_url")
    private String logo;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}

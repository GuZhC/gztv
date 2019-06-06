package com.dfsx.lzcms.liveroom.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/12/20.
 */
public class BetPersonInfo implements Serializable, Comparable<BetPersonInfo> {


    /**
     * user_id : long, 投注人ID
     * username : string, 投注人用户名
     * avatar_url : string, 投注人头像地址
     * nickname : string, 投注人昵称
     * option_name : string, 投注选项
     * bet_time : long, 下注时间
     * bet_coins : double, 下注的虚拟币
     */

    @SerializedName("user_id")
    private long userId;
    @SerializedName("username")
    private String userName;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private String nickname;
    @SerializedName("option_name")
    private String optionName;
    @SerializedName("bet_time")
    private long betTime;
    @SerializedName("bet_coins")
    private double betCoins;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public long getBetTime() {
        return betTime;
    }

    public void setBetTime(long betTime) {
        this.betTime = betTime;
    }

    public double getBetCoins() {
        return betCoins;
    }

    public void setBetCoins(double betCoins) {
        this.betCoins = betCoins;
    }

    @Override
    public int compareTo(@NonNull BetPersonInfo another) {
        return (int) (another.getBetTime() - betTime);
    }
}

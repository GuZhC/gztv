package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.util.Map;

/**
 * 下注消息
 * Created by liuwb on 2017/7/4.
 */
public class BetGuessMessage extends LiveMessage {

    /**
     * user_id : 10000000
     * user_name : 用户名
     * user_nickname : 用户呢称
     * user_avatar_url : 用户头像地址
     * option_id : 2000000
     * coins : 23.56
     * stat : {"512":1000.56,"513":1000.788}
     */

    @SerializedName("user_id")
    private long userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_nickname")
    private String userNickName;
    @SerializedName("user_avatar_url")
    private String userAvatarUrl;
    @SerializedName("user_level_id")
    private long userLevelId;
    @SerializedName("option_id")
    private long optionId;
    private double coins;
    /**
     * 统计的数据
     */
    private Map<String, Double> stat;

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

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public Map getStat() {

        return stat;
    }

    public void setStat(Map stat) {
        this.stat = stat;
    }

    @Override
    public long getChatUserId() {
        return getUserId();
    }

    @Override
    public String getChatUserNickName() {
        return getUserNickName();
    }

    @Override
    public String getChatUserLogo() {
        return getUserAvatarUrl();
    }

    @Override
    public CharSequence getChatContentText() {
        return super.getChatContentText();
    }

    public long getUserLevelId() {
        return userLevelId;
    }

    public void setUserLevelId(long userLevelId) {
        this.userLevelId = userLevelId;
    }
}

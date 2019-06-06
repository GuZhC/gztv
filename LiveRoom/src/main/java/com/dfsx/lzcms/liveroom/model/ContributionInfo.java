package com.dfsx.lzcms.liveroom.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 贡献的具体信息
 * Created by liuwb on 2016/10/17.
 */
public class ContributionInfo implements Serializable, Comparable<ContributionInfo> {

    @SerializedName("user_id")
    private long userId;
    @SerializedName("username")
    private String userAccountName;
    @SerializedName("nickname")
    private String name;
    @SerializedName("avatar_url")
    private String userLogo;
    @SerializedName("coins")
    private long contributionValue;//贡献值
    private int contributionLevel;//贡献等级， 第一

    public ContributionInfo() {

    }

    public ContributionInfo(String name, String logo, long value, int level) {
        this.contributionLevel = level;
        this.contributionValue = value;
        this.name = name;
        this.userLogo = logo;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserAccountName() {
        return userAccountName;
    }

    public void setUserAccountName(String userAccountName) {
        this.userAccountName = userAccountName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public long getContributionValue() {
        return contributionValue;
    }

    public void setContributionValue(long contributionValue) {
        this.contributionValue = contributionValue;
    }

    public int getContributionLevel() {
        return contributionLevel;
    }

    public void setContributionLevel(int contributionLevel) {
        this.contributionLevel = contributionLevel;
    }

    @Override
    public int compareTo(@NonNull ContributionInfo another) {
        return (int) (another.getContributionValue() - this.getContributionValue());
    }
}

package com.dfsx.lzcms.liveroom.model;

import android.view.View;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuwb on 2016/12/20.
 * 下注的选项
 */
public class BetOption {

    /**
     * id : 11111111111
     * name : 下注选项名称
     * detail : 下注选择详细说明
     * total_amount : 5.555
     */

    private long id;
    private String name;
    private String detail;
    @SerializedName("total_amount")
    private double totalAmount;
    @SerializedName("icon_url")
    private String optionLogo;
    @SerializedName("icon_id")
    private long iconId;
    @SerializedName("coins")
    private double currentUserBetCoins;

    private boolean isEnableBet;

    /**
     * 显示在列表中的位置
     */
    private int listPosition;

    //    private View clickView;

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOptionLogo() {
        return optionLogo;
    }

    public void setOptionLogo(String optionLogo) {
        this.optionLogo = optionLogo;
    }

    public double getCurrentUserBetCoins() {
        return currentUserBetCoins;
    }

    public void setCurrentUserBetCoins(double currentUserBetCoins) {
        this.currentUserBetCoins = currentUserBetCoins;
    }

    public boolean isEnableBet() {
        return isEnableBet;
    }

    public void setEnableBet(boolean enableBet) {
        isEnableBet = enableBet;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public long getIconId() {
        return iconId;
    }

    public void setIconId(long iconId) {
        this.iconId = iconId;
    }
}

package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 竞猜收入
 * Created by liuwb on 2016/12/29.
 */
public class BetBonusAction implements TradeAction {

    /**
     * “id”: <long, 操作ID>,
     * “option_id”: <string, 投注选项ID>,
     * “option_name”:<string, 投注选项>,
     * option_icon_url<string, 投注选项的图片>,
     * bet_time : long, 下注时间
     * bet_coins : double, 下注的虚拟币
     * return_coins : double, 赢得的虚拟币
     * show_id : long, 演出ID
     * show_title : string, 演出标题
     */
    private long id;
    private long option_id;
    @SerializedName("option_name")
    private String optionName;
    @SerializedName("option_icon_url")
    private String optionIconUrl;
    private long bet_time;
    private double bet_coins;
    private double return_coins;
    private long show_id;
    private String show_title;

    @Override
    public long getTAId() {
        return getId();
    }

    @Override
    public double getTACoins() {
        return getReturn_coions();
    }

    @Override
    public long getTATagPersonId() {
        return getOption_id();
    }

    @Override
    public String getTATagPersonName() {
        return getOptionName();
    }

    @Override
    public String getTATagPersonNickName() {
        return getOptionName();
    }

    @Override
    public String getTATagPersonLogo() {
        return getOptionIconUrl();
    }

    @Override
    public long getTATime() {
        return getBet_time();
    }

    @Override
    public String getTADescribeText() {
        return "竞猜收入";
    }

    public long getBet_time() {
        return bet_time;
    }

    public void setBet_time(long bet_time) {
        this.bet_time = bet_time;
    }

    public double getBet_coins() {
        return bet_coins;
    }

    public void setBet_coins(double bet_coins) {
        this.bet_coins = bet_coins;
    }

    public double getReturn_coions() {
        return return_coins;
    }

    public void setReturn_coions(double return_coions) {
        this.return_coins = return_coions;
    }

    public long getShow_id() {
        return show_id;
    }

    public void setShow_id(long show_id) {
        this.show_id = show_id;
    }

    public String getShow_title() {
        return show_title;
    }

    public void setShow_title(String show_title) {
        this.show_title = show_title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOption_id() {
        return option_id;
    }

    public void setOption_id(long option_id) {
        this.option_id = option_id;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionIconUrl() {
        return optionIconUrl;
    }

    public void setOptionIconUrl(String optionIconUrl) {
        this.optionIconUrl = optionIconUrl;
    }
}

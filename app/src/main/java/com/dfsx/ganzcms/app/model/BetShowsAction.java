package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 竞猜支出
 * Created by liuwb on 2016/12/29.
 */
public class BetShowsAction implements TradeAction {

    /**
     * “id”: <long, 操作ID>
     * option_id
     * “option_name”:<string, 投注选项>,
     * bet_time : long, 下注时间
     * bet_coins : double, 下注的虚拟币
     * show_id : long, 演出ID
     * show_title : string, 演出标题
     */

    private long id;
    private long option_id;
    private String option_name;
    @SerializedName("option_icon_url")
    private String optionIconUrl;
    private long bet_time;
    private double bet_coins;
    private long show_id;
    private String show_title;

    @Override
    public long getTAId() {
        return getId();
    }

    @Override
    public double getTACoins() {
        return getBet_coins();
    }

    @Override
    public long getTATagPersonId() {
        return getOption_id();
    }

    @Override
    public String getTATagPersonName() {
        return getOption_name();
    }

    @Override
    public String getTATagPersonNickName() {
        return getOption_name();
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
        return "竞猜押注";
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

    public String getOption_name() {
        return option_name;
    }

    public void setOption_name(String option_name) {
        this.option_name = option_name;
    }

    public String getOptionIconUrl() {
        return optionIconUrl;
    }

    public void setOptionIconUrl(String optionIconUrl) {
        this.optionIconUrl = optionIconUrl;
    }
}

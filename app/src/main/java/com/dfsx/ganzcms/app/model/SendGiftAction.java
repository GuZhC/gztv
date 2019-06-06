package com.dfsx.ganzcms.app.model;

import com.dfsx.ganzcms.app.App;

/**
 * 送礼物
 * Created by liuwb on 2016/12/29.
 */
public class SendGiftAction implements TradeAction {

    /**
     * id : long, 操作ID
     * gift_id : long, 交易礼物ID
     * gift_name : string, 交易礼物名称
     * quantity : long, 礼物个数
     * price : double,礼物单价
     * from_user_id : long, 赠出人ID
     * from_user_username : string, 赠出人名称
     * from_user_nickname
     * from_user_avatar_url : string, 赠出人头像地址
     * to_user_id : long, 受赠人ID
     * to_user_username : string, 受赠人名称
     * to_user_avatar_url : string, 受赠人头像地址
     * occur_time : timestamp, 发生时间
     * show_id : long, 演出ID
     * show_title : string, 演出标题
     */

    private long id;
    private long gift_id;
    private String gift_name;
    private long quantity;
    private double price;
    private long from_user_id;
    private String from_user_username;
    private String from_user_nickname;
    private String from_user_avatar_url;
    private long to_user_id;
    private String to_user_username;
    private String to_user_nickname;
    private String to_user_avatar_url;
    private long occur_time;
    private long show_id;
    private String show_title;

    @Override
    public long getTAId() {
        return getId();
    }

    @Override
    public double getTACoins() {
        return getPrice() * getQuantity();
    }

    @Override
    public long getTATagPersonId() {
        return isToCurrentUser() ? getFrom_user_id() : getTo_user_id();
    }

    @Override
    public String getTATagPersonName() {
        return isToCurrentUser() ? getFrom_user_username() : getTo_user_username();
    }

    @Override
    public String getTATagPersonNickName() {
        return isToCurrentUser() ? getFrom_user_nickname() : getTo_user_nickname();
    }

    @Override
    public String getTATagPersonLogo() {
        return isToCurrentUser() ? getFrom_user_avatar_url() : getTo_user_avatar_url();
    }

    private boolean isToCurrentUser() {
        long currId = App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null
                ? App.getInstance().getUser().getUser().getId() : 0;
        return currId == to_user_id;
    }

    @Override
    public long getTATime() {
        return getOccur_time();
    }

    @Override
    public String getTADescribeText() {
        String userText = isToCurrentUser() ? "我" : "他";
        return "送给" + userText + quantity + "个" + gift_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGift_id() {
        return gift_id;
    }

    public void setGift_id(long gift_id) {
        this.gift_id = gift_id;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(long from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getFrom_user_username() {
        return from_user_username;
    }

    public void setFrom_user_username(String from_user_username) {
        this.from_user_username = from_user_username;
    }

    public String getFrom_user_avatar_url() {
        return from_user_avatar_url;
    }

    public void setFrom_user_avatar_url(String from_user_avatar_url) {
        this.from_user_avatar_url = from_user_avatar_url;
    }

    public long getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(long to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTo_user_username() {
        return to_user_username;
    }

    public void setTo_user_username(String to_user_username) {
        this.to_user_username = to_user_username;
    }

    public String getTo_user_avatar_url() {
        return to_user_avatar_url;
    }

    public void setTo_user_avatar_url(String to_user_avatar_url) {
        this.to_user_avatar_url = to_user_avatar_url;
    }

    public long getOccur_time() {
        return occur_time;
    }

    public void setOccur_time(long occur_time) {
        this.occur_time = occur_time;
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

    public String getTo_user_nickname() {
        return to_user_nickname;
    }

    public void setTo_user_nickname(String to_user_nickname) {
        this.to_user_nickname = to_user_nickname;
    }

    public String getFrom_user_nickname() {
        return from_user_nickname;
    }

    public void setFrom_user_nickname(String from_user_nickname) {
        this.from_user_nickname = from_user_nickname;
    }
}

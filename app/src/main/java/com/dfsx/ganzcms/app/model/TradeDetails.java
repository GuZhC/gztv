package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/11/1.
 */
public class TradeDetails implements Serializable{

    /**
     * id : 123456
     * record_sn : 也是订单流水号
     * type : 2
     * action : 交易操作：charge, transfer, pay, increase, decrease, systemIncrease, systemDecrease
     * coins : 125.8
     * remain : 122.5
     * trade_time : 交易时间
     * source : 产生交易的应用来源
     * related_user_id : 123456789
     * related_account_id : 转入/转出用户账户ID
     * remark : 啊啊啊是发生
     */

    private long id;
    private String record_sn;
    private int type;
    private String action;
    private double coins;
    private double remain;
    @SerializedName("trade_time")
    private String tradeTime;
    private String source;
    @SerializedName("related_user_id")
    private long relatedUserId;
    @SerializedName("related_account_id")
    private String relatedAccountId;
    private String remark;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecord_sn() {
        return record_sn;
    }

    public void setRecord_sn(String record_sn) {
        this.record_sn = record_sn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public double getRemain() {
        return remain;
    }

    public void setRemain(double remain) {
        this.remain = remain;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getRelatedUserId() {
        return relatedUserId;
    }

    public void setRelatedUserId(long relatedUserId) {
        this.relatedUserId = relatedUserId;
    }

    public String getRelatedAccountId() {
        return relatedAccountId;
    }

    public void setRelatedAccountId(String relatedAccountId) {
        this.relatedAccountId = relatedAccountId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

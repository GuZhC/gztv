package com.dfsx.ganzcms.app.model;

/**
 * 手工扣除
 * Created by liuwb on 2016/12/29.
 */
public class DecreaseAction implements TradeAction {

    /**
     * id : long, 手工扣除ID
     * operator_id : long, 操作用户ID
     * operator_username : string, 操作用户名称
     * operator_nickname : string, 操作用户呢称
     * operator_avatar_url : string, 操作用户头像地址
     * coins : double, 扣除的虚拟币数
     * occur_time : timestamp, 扣除时间
     * remark : string, 扣除备注
     */

    private long id;
    private long operator_id;
    private String operator_username;
    private String operator_nickname;
    private String operator_avatar_url;
    private double coins;
    private long occur_time;
    private String remark;

    @Override
    public long getTAId() {
        return getId();
    }

    @Override
    public double getTACoins() {
        return getCoins();
    }

    @Override
    public long getTATagPersonId() {
        return operator_id;
    }

    @Override
    public String getTATagPersonName() {
        return operator_username;
    }

    @Override
    public String getTATagPersonNickName() {
        return operator_nickname;
    }

    @Override
    public String getTATagPersonLogo() {
        return operator_avatar_url;
    }

    @Override
    public long getTATime() {
        return getOccur_time();
    }

    @Override
    public String getTADescribeText() {
        return "超级管理员扣除";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(long operator_id) {
        this.operator_id = operator_id;
    }

    public String getOperator_username() {
        return operator_username;
    }

    public void setOperator_username(String operator_username) {
        this.operator_username = operator_username;
    }

    public String getOperator_nickname() {
        return operator_nickname;
    }

    public void setOperator_nickname(String operator_nickname) {
        this.operator_nickname = operator_nickname;
    }

    public String getOperator_avatar_url() {
        return operator_avatar_url;
    }

    public void setOperator_avatar_url(String operator_avatar_url) {
        this.operator_avatar_url = operator_avatar_url;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public long getOccur_time() {
        return occur_time;
    }

    public void setOccur_time(long occur_time) {
        this.occur_time = occur_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

package com.dfsx.ganzcms.app.model;

import com.dfsx.ganzcms.app.App;

/**
 * 转账
 * Created by liuwb on 2016/12/29.
 */
public class TransferAction implements TradeAction {

    /**
     * id : long, 转账ID
     * source_user_id : long, 源用户ID
     * source_user_name : string, 源用户名称
     * source_user_nickname : string, 源用户呢称
     * source_user_avatar_url : string, 源用户头像地址
     * target_user_id : long, 目标用户ID
     * target_user_name : string, 目标用户名称
     * target_user_nickname : string, 目标用户呢称
     * target_user_avatar_url : string, 目标用户头像地址
     * coins : double, 转账的虚拟币数
     * occur_time : timestamp, 转账时间
     * remark : string, 转账备注
     */

    private long id;
    private long source_user_id;
    private String source_user_name;
    private String source_user_nickname;
    private String source_user_avatar_url;
    private long target_user_id;
    private String target_user_name;
    private String target_user_nickname;
    private String target_user_avatar_url;
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
        return getTarget_user_id();
    }

    @Override
    public String getTATagPersonName() {
        return getTarget_user_name();
    }

    @Override
    public String getTATagPersonNickName() {
        return getTarget_user_nickname();
    }

    @Override
    public String getTATagPersonLogo() {
        return getTarget_user_avatar_url();
    }

    @Override
    public long getTATime() {
        return getOccur_time();
    }

    @Override
    public String getTADescribeText() {
        String text = getSource_user_id() == App.getInstance().getUser().getUser().getId() ?
                "给他转账" + getTACoins() + "乐币" :
                "给我转账" + getTACoins() + "乐币";
        return text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSource_user_id() {
        return source_user_id;
    }

    public void setSource_user_id(long source_user_id) {
        this.source_user_id = source_user_id;
    }

    public String getSource_user_name() {
        return source_user_name;
    }

    public void setSource_user_name(String source_user_name) {
        this.source_user_name = source_user_name;
    }

    public String getSource_user_nickname() {
        return source_user_nickname;
    }

    public void setSource_user_nickname(String source_user_nickname) {
        this.source_user_nickname = source_user_nickname;
    }

    public String getSource_user_avatar_url() {
        return source_user_avatar_url;
    }

    public void setSource_user_avatar_url(String source_user_avatar_url) {
        this.source_user_avatar_url = source_user_avatar_url;
    }

    public long getTarget_user_id() {
        return target_user_id;
    }

    public void setTarget_user_id(long target_user_id) {
        this.target_user_id = target_user_id;
    }

    public String getTarget_user_name() {
        return target_user_name;
    }

    public void setTarget_user_name(String target_user_name) {
        this.target_user_name = target_user_name;
    }

    public String getTarget_user_nickname() {
        return target_user_nickname;
    }

    public void setTarget_user_nickname(String target_user_nickname) {
        this.target_user_nickname = target_user_nickname;
    }

    public String getTarget_user_avatar_url() {
        return target_user_avatar_url;
    }

    public void setTarget_user_avatar_url(String target_user_avatar_url) {
        this.target_user_avatar_url = target_user_avatar_url;
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

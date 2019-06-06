package com.dfsx.ganzcms.app.model;

import android.content.ContentResolver;
import android.content.res.Resources;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;

/**
 * 充值
 * Created by liuwb on 2016/12/29.
 */
public class ChargeAction implements TradeAction {


    /**
     * id : long, 充值ID
     * order_sn : string, 订单流水号
     * money : double, 充值所花费的真实货币
     * coins : double, 充值虚拟币数
     * creation_time : timestamp, 创建时间
     * completion_time : timestamp, 完成时间
     * creation_remark : string, 创建备注
     * completion_remark : string, 完成备注
     * pay_type 支付类型 0-未知 1-银联 2-支付宝 3-微信
     */

    private long id;
    private String order_sn;
    private double money;
    private double coins;
    private long creation_time;
    private long completion_time;
    private String creation_remark;
    private String completion_remark;
    private int pay_type;

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
        return App.getInstance().getUser().getUser().getId();
    }

    @Override
    public String getTATagPersonName() {
        return App.getInstance().getUser().getUser().getUsername();
    }

    @Override
    public String getTATagPersonNickName() {
        return getChangeNameByPayType();
    }

    @Override
    public String getTATagPersonLogo() {
        return getLogoByType();
    }

    @Override
    public long getTATime() {
        return getCompletion_time();
    }

    @Override
    public String getTADescribeText() {
        return "充值乐币";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }

    public long getCompletion_time() {
        return completion_time;
    }

    public void setCompletion_time(long completion_time) {
        this.completion_time = completion_time;
    }

    public String getCreation_remark() {
        return creation_remark;
    }

    public void setCreation_remark(String creation_remark) {
        this.creation_remark = creation_remark;
    }

    public String getCompletion_remark() {
        return completion_remark;
    }

    public void setCompletion_remark(String completion_remark) {
        this.completion_remark = completion_remark;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    private String getChangeNameByPayType() {
        if (pay_type == 0) {
            return "未知";
        } else if (pay_type == 1) {
            return "银联";
        } else if (pay_type == 2) {
            return "支付宝";
        } else if (pay_type == 3) {
            return "微信";
        } else {
            return "未知";
        }
    }

    private String getLogoByType() {
        if (pay_type == 0) {
            return "";
        } else if (pay_type == 1) {
            return getResourceImageUrl(R.drawable.icon_yinlian);
        } else if (pay_type == 2) {
            return getResourceImageUrl(R.drawable.icon_pay_ali);
        } else if (pay_type == 3) {
            return getResourceImageUrl(R.drawable.icon_pay_wx);
        } else {
            return "";
        }
    }

    private String getResourceImageUrl(int resId) {
        Resources r = App.getInstance().getResources();
        String url = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId);
        return url;
    }
}

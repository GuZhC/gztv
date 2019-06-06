package com.dfsx.lzcms.liveroom.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/10/31.
 */
public class UserMoneyInfo implements Serializable {


    /**
     * account_id : 交易账户Id
     * coins : 125.5
     * total_charge_money : 125.5
     * total_charge_coins : 12.5
     * total_spending_coins : 1.5
     */

    @SerializedName("account_id")
    private String accountId;
    private double coins;
    @SerializedName("total_charge_money")
    private double totalChargeMoney;
    @SerializedName("total_charge_coins")
    private double totalChargeCoins;
    @SerializedName("total_spending_coins")
    private double totalSpendingCoins;
    @SerializedName("total_earning_coins")
    private double totalEarningCoins;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public double getTotalChargeMoney() {
        return totalChargeMoney;
    }

    public void setTotalChargeMoney(double totalChargeMoney) {
        this.totalChargeMoney = totalChargeMoney;
    }

    public double getTotalChargeCoins() {
        return totalChargeCoins;
    }

    public void setTotalChargeCoins(double totalChargeCoins) {
        this.totalChargeCoins = totalChargeCoins;
    }

    public double getTotalSpendingCoins() {
        return totalSpendingCoins;
    }

    public void setTotalSpendingCoins(double totalSpendingCoins) {
        this.totalSpendingCoins = totalSpendingCoins;
    }

    /**
     * 获取规则的乐币表达字符串
     *
     * @return
     */
    public String getCoinsDescribeText() {
        String coinsDescribeText;
        if (coins / 10000 > 0) {
            coinsDescribeText = String.format("%.1f", coins / 10000) + "w乐币";
        } else {
            coinsDescribeText = String.format("%.0f", coins) + "乐币";
        }
        return coinsDescribeText;
    }

    public double getTotalEarningCoins() {
        return totalEarningCoins;
    }

    public void setTotalEarningCoins(double totalEarningCoins) {
        this.totalEarningCoins = totalEarningCoins;
    }
}

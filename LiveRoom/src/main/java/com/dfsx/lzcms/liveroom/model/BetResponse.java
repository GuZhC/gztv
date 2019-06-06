package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/12/20.
 */
public class BetResponse implements Serializable {

    private boolean isSuccess;
    private int errorCode;
    private String errorMsg;

    private BetOption betOption;
    private double betCoins;

    public BetResponse() {

    }

    public BetResponse(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public BetOption getBetOption() {
        return betOption;
    }

    public void setBetOption(BetOption betOption) {
        this.betOption = betOption;
    }

    public double getBetCoins() {
        return betCoins;
    }

    public void setBetCoins(double betCoins) {
        this.betCoins = betCoins;
    }
}

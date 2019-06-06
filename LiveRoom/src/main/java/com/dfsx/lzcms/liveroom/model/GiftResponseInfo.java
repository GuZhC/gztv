package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/12/2.
 */
public class GiftResponseInfo implements Serializable {

    private boolean isSuccess;

    private int errorCode;

    private String errorMsg;

    /**
     * 当前房主（房间）有多少乐币了
     *
     * @Deprecated 在收到礼物的时候获得此值
     */
    private double roomOwerTotalCoin;

    public GiftResponseInfo() {

    }

    public GiftResponseInfo(boolean isOk) {
        this.isSuccess = isOk;
    }

    public GiftResponseInfo(boolean isOk, int code, String msg) {
        this(isOk, code, msg, 0);
    }

    public GiftResponseInfo(boolean isOk, int code, String msg, double coin) {
        this.isSuccess = isOk;
        this.errorCode = code;
        this.errorMsg = msg;
        this.roomOwerTotalCoin = coin;
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

    public boolean isNoEnoughMoney() {
        return errorCode == 10010;
    }

    public double getRoomOwerTotalCoin() {
        return roomOwerTotalCoin;
    }

    public void setRoomOwerTotalCoin(double totalCoin) {
        this.roomOwerTotalCoin = totalCoin;
    }
}

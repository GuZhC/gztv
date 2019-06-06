package com.dfsx.lzcms.liveroom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuwb on 2017/7/4.
 */
public class GuessResultMessage extends LiveMessage {
    @SerializedName("result_option_id")
    private long resultOptionId;
    /**
     * 下注选项总金额的统计
     */
    private String stat;

    public long getResultOptionId() {
        return resultOptionId;
    }

    public void setResultOptionId(long resultOptionId) {
        this.resultOptionId = resultOptionId;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}

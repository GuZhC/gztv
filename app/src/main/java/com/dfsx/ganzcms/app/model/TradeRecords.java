package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuwb on 2016/10/31.
 */
public class TradeRecords implements Serializable {

    /**
     * total : 记录总数
     * data : [{"id":1111,"record_sn":"交易流水号","type":2,"action":"交易操作：charge, transfer, pay, increase, decrease, move, systemIncrease, systemDecrease, systemMove","coins":555.5,"remain":125.5,"trade_time":"交易时间","source":"产生交易的应用来源"}]
     */

    private String total;
    /**
     * id : 1111
     * record_sn : 交易流水号
     * type : <int, 交易类型：1 – 收入, 2 – 支出>
     * action : 交易操作：charge, transfer, pay, increase, decrease, move, systemIncrease, systemDecrease, systemMove
     * coins : 555.5 <double, 交易的虚拟币数>
     * remain : 125.5 <double, 剩余虚拟币额>
     * trade_time : 交易时间
     * source : 产生交易的应用来源
     */

    private List<TradeRecordItem> data;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<TradeRecordItem> getData() {
        return data;
    }

    public void setData(List<TradeRecordItem> data) {
        this.data = data;
    }

    public static class TradeRecordItem {
        private long id;
        private String record_sn;
        private int type;
        private String action;
        private double coins;
        private double remain;
        @SerializedName("trade_time")
        private long tradeTime;
        private String source;
        private long source_detail_id;
        private TradeAction tradeActionInfo;

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

        public long getTradeTime() {
            return tradeTime;
        }

        public void setTradeTime(long tradeTime) {
            this.tradeTime = tradeTime;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public long getSource_detail_id() {
            return source_detail_id;
        }

        public void setSource_detail_id(long source_detail_id) {
            this.source_detail_id = source_detail_id;
        }

        public TradeAction getTradeActionInfo() {
            return tradeActionInfo;
        }

        public void setTradeActionInfo(TradeAction tradeActionInfo) {
            this.tradeActionInfo = tradeActionInfo;
        }
    }
}

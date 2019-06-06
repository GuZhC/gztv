package com.dfsx.shop.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by heyang on 207/11/16
 *
 *
 *   交易操作记录
 */
final public class TradeActionRecord implements Serializable {

    /**
     * id : -1
     * order_no :
     * contact_name :
     * contact_phone_number :
     * contact_address :
     * category_name :
     * commodity_id : -1
     * commodity _name :
     * price : 0.0
     * count : 0
     * amount : 0.0
     * exchange_no :
     * remark :
     * status : 0
     * end_time : -1
     * creation_time : -1
     */

    private long id;
    private String order_no;
    private String contact_name;
    private String contact_phone_number;
    private String contact_address;
    private String category_name;
    private long commodity_id;
    @SerializedName("commodity _name")
    private String _$Commodity_name42; // FIXME check this code
    private double price;
    private int count;
    private double amount;
    private String exchange_no;
    private String remark;
    private int status;
    private long end_time;
    private long creation_time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_phone_number() {
        return contact_phone_number;
    }

    public void setContact_phone_number(String contact_phone_number) {
        this.contact_phone_number = contact_phone_number;
    }

    public String getContact_address() {
        return contact_address;
    }

    public void setContact_address(String contact_address) {
        this.contact_address = contact_address;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public long getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(long commodity_id) {
        this.commodity_id = commodity_id;
    }

    public String get_$Commodity_name42() {
        return _$Commodity_name42;
    }

    public void set_$Commodity_name42(String _$Commodity_name42) {
        this._$Commodity_name42 = _$Commodity_name42;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getExchange_no() {
        return exchange_no;
    }

    public void setExchange_no(String exchange_no) {
        this.exchange_no = exchange_no;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }
}

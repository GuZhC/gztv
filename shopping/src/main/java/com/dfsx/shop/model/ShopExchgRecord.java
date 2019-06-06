package com.dfsx.shop.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by heyang on 207/11/16
 *
 *
 *   商城兑换记录
 */
final public class ShopExchgRecord implements Serializable {

    /**
     * order_no :
     * commodity_id : -1
     * commodity _name :
     * price : 0.0
     * count : 0
     * amount : 0.0
     * status : 0
     * creation_time : -1
     */

    private String order_no;
    private long commodity_id;
    private String commodity_name;
    private double price;
    private int count;
    private double amount;
    private int status;
    private long creation_time;

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public long getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(long commodity_id) {
        this.commodity_id = commodity_id;
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

    public String getCommodity_name() {
        return commodity_name;
    }

    public void setCommodity_name(String commodity_name) {
        this.commodity_name = commodity_name;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(long creation_time) {
        this.creation_time = creation_time;
    }
}

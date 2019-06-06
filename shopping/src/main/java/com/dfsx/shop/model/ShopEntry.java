package com.dfsx.shop.model;

import java.io.Serializable;

/**
 * Created by heyang on 207/11/16
 *
 *
 *  商品類
 */
final public class ShopEntry implements Serializable {


    /**
     * id : -1
     * name :
     * cover_id : -1
     * cover_url :
     * exchange_count : 0
     * price : 0.0
     * status : 1
     */

    private long id;
    private String name;
    private long cover_id;
    private String cover_url;
    private int exchange_count;
    private double price;
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCover_id() {
        return cover_id;
    }

    public void setCover_id(long cover_id) {
        this.cover_id = cover_id;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public int getExchange_count() {
        return exchange_count;
    }

    public void setExchange_count(int exchange_count) {
        this.exchange_count = exchange_count;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

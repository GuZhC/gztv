package com.dfsx.shop.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by heyang on 207/11/16
 *
 *
 *  商品详情类
 */
final public class ShopEntryInfo implements Serializable {


    /**
     * id : -1
     * name :
     * detail :
     * remark :
     * stock : 0
     * price : 0.0
     * status : 0
     * exchange_count : 0
     * exchange_mode : 0
     * exchange_rule : {"exchange_introduction":"","exchange_days":0}
     * images : {"id":-1,"url":""}
     */

    private long id;
    private String name;
    private String detail;
    private String remark;
    private int stock;
    private double price;
    private int status;
    private int exchange_count;
    private int exchange_mode;
    private ExchangeRuleBean exchange_rule;
    private ArrayList<ImagesBean> imagesList;

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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

    public int getExchange_count() {
        return exchange_count;
    }

    public void setExchange_count(int exchange_count) {
        this.exchange_count = exchange_count;
    }

    public int getExchange_mode() {
        return exchange_mode;
    }

    public void setExchange_mode(int exchange_mode) {
        this.exchange_mode = exchange_mode;
    }

    public ExchangeRuleBean getExchange_rule() {
        return exchange_rule;
    }

    public void setExchange_rule(ExchangeRuleBean exchange_rule) {
        this.exchange_rule = exchange_rule;
    }

    public ArrayList<ImagesBean> getImages() {
        return imagesList;
    }

    public void setImages(ArrayList<ImagesBean> images) {
        this.imagesList = images;
    }

    public static class ExchangeRuleBean implements  Serializable{
        /**
         * exchange_introduction :
         * exchange_days : 0
         */

        private String exchange_introduction;
        private int exchange_days;

        public String getExchange_introduction() {
            return exchange_introduction;
        }

        public void setExchange_introduction(String exchange_introduction) {
            this.exchange_introduction = exchange_introduction;
        }

        public int getExchange_days() {
            return exchange_days;
        }

        public void setExchange_days(int exchange_days) {
            this.exchange_days = exchange_days;
        }
    }

    public static class ImagesBean implements Serializable {

        /**
         * id : -1
         * url :
         */

        private long id;
        private String url;
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

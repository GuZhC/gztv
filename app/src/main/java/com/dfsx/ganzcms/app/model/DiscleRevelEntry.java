package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * Created  heyang  2018/1/15
 * 爆料提交類
 */

public class DiscleRevelEntry implements Serializable {


    /**
     * body :
     * real_name :
     * phone_number :
     * geo_latitude : 0.0
     * geo_longitude : 0.0
     * geo_address :
     */

    private String body;
    private String real_name;
    private String phone_number;
    private double geo_latitude;
    private double geo_longitude;
    private String geo_address;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public double getGeo_latitude() {
        return geo_latitude;
    }

    public void setGeo_latitude(double geo_latitude) {
        this.geo_latitude = geo_latitude;
    }

    public double getGeo_longitude() {
        return geo_longitude;
    }

    public void setGeo_longitude(double geo_longitude) {
        this.geo_longitude = geo_longitude;
    }

    public String getGeo_address() {
        return geo_address;
    }

    public void setGeo_address(String geo_address) {
        this.geo_address = geo_address;
    }
}

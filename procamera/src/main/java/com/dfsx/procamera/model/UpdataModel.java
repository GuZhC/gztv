package com.dfsx.procamera.model;

import java.io.Serializable;

/**
 * Created by heyang on 2018-6-19  活动
 */
public class UpdataModel implements Serializable {


    /**
     * activity_id : -1
     * title :
     * paths :
     * geo_latitude : 0
     * geo_longitude : 0
     * geo_address :
     */

    private long activity_id;
    private String title;
    private String paths;
    private double geo_latitude;
    private double geo_longitude;
    private String geo_address;

    public long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(long activity_id) {
        this.activity_id = activity_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
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


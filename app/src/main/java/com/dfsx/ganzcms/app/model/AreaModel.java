package com.dfsx.ganzcms.app.model;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/11/3.
 */
public class AreaModel {

    private String area;

    private ArrayList<AreaModel> childArea;

    public AreaModel(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public ArrayList<AreaModel> getChildArea() {
        return childArea;
    }

    public void setChildArea(ArrayList<AreaModel> childArea) {
        this.childArea = childArea;
    }
}

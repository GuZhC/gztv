package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * 乐币
 * Created by liuwb on 2016/10/27.
 */
public class LSGold implements Serializable {

    private boolean isSelected;

    public LSGold() {

    }

    public LSGold(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}

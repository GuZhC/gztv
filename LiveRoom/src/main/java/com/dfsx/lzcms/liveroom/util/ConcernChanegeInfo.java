package com.dfsx.lzcms.liveroom.util;

import java.io.Serializable;

/**
 * 关注信息发生变的实体类
 * Created by liuwb on 2017/3/6.
 */
public class ConcernChanegeInfo implements Serializable {

    /**
     * 标记是否是添加
     */
    private boolean isAdd;

    /**
     * 变化的数目
     */
    private int changeNum;

    public ConcernChanegeInfo() {

    }

    public ConcernChanegeInfo(boolean isAdd, int num) {
        this.isAdd = isAdd;
        this.changeNum = num;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public int getChangeNum() {
        return changeNum;
    }

    public void setChangeNum(int changeNum) {
        this.changeNum = changeNum;
    }
}

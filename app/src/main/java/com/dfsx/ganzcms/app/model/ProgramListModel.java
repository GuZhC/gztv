package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wxl on 2016/11/14.
 */

public class ProgramListModel implements Serializable {

    //回看
    public final static int MODE_HUIKAN = 0x11;
    //预约
    public final static int MODE_YUYUE = 0x12;
    //已预约
    public final static int MODE_YI_YUYUE = 0x13;

    public void setTime(long time) {
        this.time = time;
    }

    private long time;
    private String programName;
    private String url;
    //模式: 回看\预约\已预约
    private int mode;
    //
    private int yuyue;


    public ProgramListModel(long time, String programName, String url, int yuyue) {
        this.time = time;
        this.programName = programName;
        this.url = url;
        this.mode = getMode(time,yuyue);
        this.yuyue = yuyue;
    }

    private int getMode(long time,int yuyue) {
        long now = new Date().getTime();
        if (time > new Date().getTime()) {
            if (yuyue == 1)
                return MODE_YI_YUYUE;
            else
                return MODE_YUYUE;
        } else
            return MODE_HUIKAN;
    }

    public int getYuyue() {
        return yuyue;
    }

    public void setYuyue(int yuyue) {
        this.yuyue = yuyue;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getUrl() {
        return url;
    }

    public long getTime() {
        return time;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}

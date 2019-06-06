package com.dfsx.lzcms.liveroom.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by liuwb on 2016/12/14.
 */
public class LRCRow implements Serializable, Comparable<LRCRow> {

    /**
     * 该行歌词要开始播放的时间，格式如下：[02:34.14]
     */
    private String strTime;

    /**
     * 该行歌词要开始播放的时间，由[02:34.14]格式转换为long型，
     * 即将2分34秒14毫秒都转为毫秒后 得到的long型值：time=02*60*1000+34*1000+14
     */
    private long time;

    private String content;

    /**
     * 相对时间
     */
    private long dTimeStamp;

    public LRCRow() {

    }

    public LRCRow(String strTime, String content) {
        this.strTime = strTime;
        this.content = content;
        this.time = timeConvert(strTime);
    }

    public static LRCRow createRows(String standardLrcLine) {
        /**
         一行歌词只有一个时间的  例如：徐佳莹   《我好想你》
         [2016-12-23 14:24:45]我好想你 好想你
         **/
        try {
            if (standardLrcLine.indexOf("[") != 0 || standardLrcLine.indexOf("]") != 20) {
                return null;
            }
            int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
            String content = standardLrcLine.substring(lastIndexOfRightBracket + 1, standardLrcLine.length());

            /**
             将时间格式转换一下  [2016-12-23 14:24:45] 转换为  2016-12-23 14:24:45
             */
            String times = standardLrcLine.substring(0, lastIndexOfRightBracket + 1).replace("[", "").replace("]", "");
            LRCRow lrcRow = new LRCRow(times, content);
            return lrcRow;
        } catch (Exception e) {
            Log.e("TAG", "createRows exception:" + e.getMessage());
            return null;
        }
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 将解析得到的表示时间的字符转化为Long型
     */
    private long timeConvert(String timeString) {
        //因为给如的字符串的时间格式为2016-12-23 14:24:58,返回的long要求是以毫秒为单位
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        long jtime = 0;
        try {
            jtime = sdf.parse(timeString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jtime;
    }

    @Override
    public int compareTo(@NonNull LRCRow another) {
        return (int) (this.getTime() - another.getTime());
    }

    public long getdTimeStamp() {
        return dTimeStamp;
    }

    public void setdTimeStamp(long dTimeStamp) {
        this.dTimeStamp = dTimeStamp;
    }
}

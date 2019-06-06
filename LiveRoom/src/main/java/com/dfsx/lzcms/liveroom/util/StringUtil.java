package com.dfsx.lzcms.liveroom.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.dfsx.core.exception.ApiException;
import com.dfsx.lzcms.liveroom.business.AppManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liuwb on 2016/11/4.
 */
public class StringUtil {
    public static final String HEART_TEXT = "[HEART]";

    public static String getRoomName(String roomAddress) {
        if (TextUtils.isEmpty(roomAddress)) {
            return null;
        }
        int lastSplit = roomAddress.indexOf("@");
        String roomName = lastSplit > 0 ?
                roomAddress.substring(0, lastSplit) :
                roomAddress;
        return roomName;
    }

    /**
     * 用来取 Message中From的名字。 取/后面的名称
     *
     * @param userName
     * @return
     */
    public static String getRoomMemberName(String userName) {
        return userName;
    }

    /**
     * 取@之前的字符为名字
     * 通常用来取服务器的主动发过来的From 和 取JID的名字
     *
     * @param jidNameString
     * @return
     */
    public static String getRoomJidSimpleName(String jidNameString) {
        if (TextUtils.isEmpty(jidNameString)) {
            return jidNameString;
        }
        int lastSplit = jidNameString.indexOf("@");
        String name = lastSplit > 0 ?
                jidNameString.substring(0, lastSplit) :
                jidNameString;
        return name;
    }

    public static boolean isCurrentUserName(String userName) {
        String name1 = userName;
        String name2 = AppManager.getInstance().getIApp().getUserName();

        return name1.equals(name2);
    }

    /**
     * 时间为妙   长度换算视频时长
     *
     * @param time
     * @return
     */
    public static String formatTime(int time) {
        if (time == 0) {
            return "00:00:00";
        } else {
            Date date = new Date(time);
            long hour = time / (60 * 60);
            long minute = (time - hour * 60 * 60) / 60;
            long second = time - hour * 60 * 60 - minute * 60;
            return (hour == 0 ? "00" : (hour > 10 ? hour : ("0" + hour))) + ":" + (minute == 0 ? "00" : (minute > 10 ? minute : ("0" + minute))) + ":" + (second == 0 ? "00" : (second > 10 ? second : ("0" + second)));
        }
    }

    /**
     * 返回的时间格式为 yyyy-MM-dd HH:mm
     *
     * @param timestamp
     * @return
     */
    public static String getTimeText(long timestamp) {
        String pp = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pp);
        return sdf.format(new Date(timestamp * 1000));
    }

    public static String getTimeAgoText(long timestamp) {
        long cTime = new Date().getTime();
        long time = timestamp * 1000;
        long dT = cTime - time;

        long seconds = dT / 1000;
        int h = (int) (seconds / (60 * 60));
        int m = (int) (seconds / 60);
        int s = (int) seconds;
        String text = "";
        int maxHour = 7 * 24;
        if (h > maxHour) {//大于7天
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            int curYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(time);
            int timeYear = calendar.get(Calendar.YEAR);
            if (curYear == timeYear) {
                text = getDayTimeText(timestamp);
            } else {
                text = getTimeText(timestamp);
            }
        } else if (h >= 24) {
            text = h / 24 + "天前";
        } else if (h > 0) {
            text = h + "小时前";
        } else if (m > 3) {
            text = m + "分钟前";
        } else {
            text = "刚刚";
        }
        return text;
    }

    public static String getChatTimeText(long timestamp) {
        long time = timestamp * 1000;
        String pp = "yyyy-MM-dd HH:mm";
        if (isTodayTime(time)) {
            pp = "HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pp);
        return sdf.format(new Date(time));
    }

    public static String getDayTimeText(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(new Date(timestamp * 1000));
    }

    public static boolean isTodayTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long dtime = time - calendar.getTimeInMillis();
        int hour = (int) (dtime / 1000 / 3600);
        return hour < 24;
    }

    public static String getTimeFurtureText(long timestamp) {
        long time = timestamp * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long currentToday0 = calendar.getTimeInMillis();
        long dtime = time - currentToday0;
        int hour = (int) (dtime / 1000 / 3600);
        String pp = "yyyy-MM-dd HH:mm";
        String text = "";
        if (hour < 0) {
            pp = "yyyy-MM-dd HH:mm";
        } else if (hour < 24) {
            text = "今天";
            pp = "HH:mm";
        } else if (hour < 48) {
            text = "明天";
            pp = "HH:mm";
        } else if (hour < 72) {
            text = "后天";
            pp = "HH:mm";
        } else {
            pp = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pp);
        return text + sdf.format(new Date(time));
    }

    public static String getHourMinutesTimeText(long time) {
        String pp = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pp);
        return sdf.format(new Date(time));
    }

    /**
     * 检查网络请求的字符串的返回是否为错误的类型
     * 如果为错误的类型则抛出 异常
     *
     * @param response
     * @throws ApiException
     */
    public static void checkHttpResponseError(String response) throws ApiException {
        if (response == null || TextUtils.isEmpty(response)) {
            String errorMsg = "连接超时";
            throw new ApiException(errorMsg);
        }
        try {
            if (TextUtils.isEmpty(response) ||
                    response.startsWith("[")) {
                return;
            }
            JSONObject res = new JSONObject(response);
            int error = res.optInt("error");
            if (!(error >= 200 && error < 300 || error == 0)) {
                String errorMsg = res.optString("message");
                throw new ApiException(errorMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public static int getHttpResponseErrorCode(String response) {
        try {
            if (TextUtils.isEmpty(response) ||
                    response.startsWith("[")) {
                return 0;
            }
            JSONObject res = new JSONObject(response);
            int error = res.optInt("error");
            return error;
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return 0;
    }

    public static String getHttpResponseErrorMessage(String response) {
        try {
            if (TextUtils.isEmpty(response) ||
                    response.startsWith("[")) {
                return "";
            }
            JSONObject res = new JSONObject(response);
            String errorMessage = res.optString("message");
            return errorMessage;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 把余额转成字符串表达
     *
     * @param coins
     * @return
     */
    public static String moneyToString(double coins) {
        String coinsDescribeText;
        if (coins / 10000 > 1) {
            coinsDescribeText = String.format("%.1f", coins / 10000f) + "w乐币";
        } else {
            coinsDescribeText = String.format("%.0f", coins) + "乐币";
        }
        return coinsDescribeText;
    }

    public static String getLiveShareTitle() {
        return "乐山城事，全民直播带你玩转乐山！";
    }

    public static String getLiveShareContent(String name, String liveTitle) {
        return name + "正在直播【" + liveTitle + "】乐山城事，全民直播带你玩转乐山！";
    }

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    /**
     * 返回12.5K样式
     *
     * @param num
     * @return
     */
    public static String getNumKString(double num) {
        String text = "";
        if (num > 1000) {
            text = String.format("%.1f", num / 1000f) + "K";
        } else {
            text = (int) num + "";
        }
        return text;
    }

    public static String getNumWString(double num) {
        String text = "";
        if (num > 10000) {
            text = String.format("%.1f", num / 10000f) + "万";
        } else {
            text = (int) num + "";
        }
        return text;
    }
}

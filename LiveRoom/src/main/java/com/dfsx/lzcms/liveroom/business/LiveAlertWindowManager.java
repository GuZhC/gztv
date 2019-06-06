package com.dfsx.lzcms.liveroom.business;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import com.dfsx.lzcms.liveroom.view.LiveCreateAlertPopupwindow;

/**
 * Created by liuwb on 2017/3/28.
 */
public class LiveAlertWindowManager {

    public static boolean isNeedShowWindow(Context context) {
        return !getSPData(context);
    }

    public static void autoShowWindow(Activity context, View anchor, String alertText) {
        if (isNeedShowWindow(context)) {
            showWindow(context, anchor, alertText);
        }
    }

    public static void showWindow(Activity context, View anchor, String alertText) {
        LiveCreateAlertPopupwindow createAlertPopupwindow = new LiveCreateAlertPopupwindow(context);
        createAlertPopupwindow.setShowText(alertText);
        if (context != null && !context.isFinishing()) {
            createAlertPopupwindow.show(anchor);
        }
    }

    /**
     * 设置是否已经显示过了
     *
     * @param context
     * @param isShowed
     */
    public static void setIsShowed(Context context, boolean isShowed) {
        setSPData(context, isShowed);
    }

    /**
     * @param context
     * @param isShowed TRUE 表示已经显示过了， 下次不显示
     */
    private static void setSPData(Context context, boolean isShowed) {
        SharedPreferences sp = context.getSharedPreferences("ls_cms_live_alert_sp", 0);
        sp.edit().
                putBoolean("ls_cms_live_alert_is_showed", isShowed)
                .commit();
    }

    private static boolean getSPData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("ls_cms_live_alert_sp", 0);
        return sp.getBoolean("ls_cms_live_alert_is_showed", false);
    }
}

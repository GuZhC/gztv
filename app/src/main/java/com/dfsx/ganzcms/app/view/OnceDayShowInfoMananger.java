package com.dfsx.ganzcms.app.view;

import android.content.SharedPreferences;
import com.dfsx.ganzcms.app.App;

import java.util.Calendar;
import java.util.Locale;

/**
 * 一天显示一次管理器
 *
 * @author liuwb
 */
public class OnceDayShowInfoMananger {

    public static final String KEY_SP = "com.dfsx_once_day_show_info";

    public static final String KEY_TIME = "_time";
    /**
     * 任务打开客户端
     */
    public static final String KEY_TASK_OPEN_APP = "com_dfsx_once_task_open_APP";


    /**
     * 今天是否显示
     *
     * @return
     */
    public static boolean isShowToday(String key) {
        SharedPreferences sp = App.getInstance().getApplicationContext()
                .getSharedPreferences(KEY_SP, 0);
        Calendar startToday = Calendar.getInstance(Locale.CHINA);
        startToday.set(Calendar.HOUR_OF_DAY, 0);
        startToday.set(Calendar.MINUTE, 0);
        startToday.set(Calendar.SECOND, 0);
        startToday.set(Calendar.MILLISECOND, 0);
        long saveTime = sp.getLong(key + KEY_TIME, 0);
        boolean isToadyTime = saveTime != 0 && saveTime >= startToday.getTimeInMillis();
        return sp.getBoolean(key, false) && isToadyTime;
    }

    /**
     * 更新保存Key的状态
     *
     * @param key
     * @param is
     */
    public static void updateKeyStatus(String key, boolean is) {
        SharedPreferences sp = App.getInstance().getApplicationContext()
                .getSharedPreferences(KEY_SP, 0);
        sp.edit().putBoolean(key, is).commit();
        sp.edit().putLong(key + KEY_TIME, System.currentTimeMillis()).commit();
    }
}

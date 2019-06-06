package com.dfsx.core.common.business;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.dfsx.core.common.Util.SharedPreferencesUtils;
import com.dfsx.core.common.act.BaseActivity;

import java.util.Locale;

public class LanguageUtil {

    public static void switchLanguage(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (language) {
            case "zh_TW":
                configuration.locale = Locale.TRADITIONAL_CHINESE;
                break;
            case "zh_CN":
                configuration.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case "tbt"://藏语 Tibetan
                configuration.locale = new Locale("TBT", "tbt");
                break;
            default:
                configuration.locale = Locale.getDefault();
                break;
        }
        resources.updateConfiguration(configuration, displayMetrics);
        SharedPreferencesUtils.getInstance(context).putExtra("key", language);
    }

    /**
     * 是否是藏语环境
     *
     * @param context
     * @return
     */
    public static boolean isTibetanLanguage(Context context) {

        String language = SharedPreferencesUtils.getInstance(context).getString("key", "zh");
        return TextUtils.equals(language, "tbt");
    }
}

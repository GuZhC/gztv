package com.dfsx.core.common.act;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.SharedPreferencesUtils;
import com.dfsx.core.common.business.LanguageUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.Locale;

/**
 * Created by liuwb on 2017/6/30.
 * 所有activity的公共类
 */
public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        CoreApp.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        LanguageUtil.switchLanguage(this,
                SharedPreferencesUtils.getInstance(BaseActivity.this).getString("key", "zh"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        CoreApp.getInstance().removeActivity(this);
        super.onDestroy();
    }

}

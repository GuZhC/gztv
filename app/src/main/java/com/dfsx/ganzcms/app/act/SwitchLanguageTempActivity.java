package com.dfsx.ganzcms.app.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.dfsx.core.common.Util.SharedPreferencesUtils;
import com.dfsx.core.common.business.LanguageUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;

/**
 * 切换语言的中间页面
 * 具体实现切换语言
 */
public class SwitchLanguageTempActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_switch_laug_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String language = SharedPreferencesUtils.getInstance(this).getString("key", "zh");
        if (TextUtils.equals(language, "zh")) {
            LanguageUtil.switchLanguage(this, "tbt");
        } else {
            LanguageUtil.switchLanguage(this, "zh");
        }
        App.getInstance().exitApp();
        Intent intent = new Intent(this, MainTabActivity.class);
        startActivity(intent);
        finish();
    }
}

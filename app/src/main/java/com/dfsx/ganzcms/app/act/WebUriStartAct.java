package com.dfsx.ganzcms.app.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.ganzcms.app.business.WebUrlStartManager;

/**
 * Created by liuwb on 2017/6/5.
 */
public class WebUriStartAct extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebUrlStartManager.getInstance().onReceiveIntent(this, getIntent());
        finish();
    }
}

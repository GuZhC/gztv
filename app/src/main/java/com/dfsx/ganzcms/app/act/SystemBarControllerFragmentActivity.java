package com.dfsx.ganzcms.app.act;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2016/8/24.
 */
public class SystemBarControllerFragmentActivity extends FragmentActivity {

    protected SystemBarTintManager systemBarTintManager;
    protected FrameLayout activityContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemBarTintManager = Util.applyKitKatTranslucency(this,
                getStatusBarColor());
    }

    public int getStatusBarColor() {
        return this.getResources().getColor(R.color.bar_red);
    }

    @Override
    public void setContentView(int layoutResID) {
        View activityView = getLayoutInflater().inflate(layoutResID, null);
        setContentView(activityView);
    }

    @Override
    public void setContentView(View view) {
        activityContainer = new FrameLayout(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        activityContainer.setLayoutParams(params);
        activityContainer.setPadding(0, systemBarTintManager.getConfig().getStatusBarHeight(), 0, 0);
        activityContainer.addView(view);
        super.setContentView(activityContainer);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        activityContainer = new FrameLayout(this);
        ViewGroup.LayoutParams containerParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        activityContainer.setLayoutParams(containerParams);
        activityContainer.setPadding(0, systemBarTintManager.getConfig().getStatusBarHeight(), 0, 0);
        activityContainer.addView(view);
        super.setContentView(activityContainer, params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (activityContainer != null) {
                activityContainer.setPadding(0, systemBarTintManager.getConfig().getStatusBarHeight(), 0, 0);
            }
        } else {
            if (activityContainer != null) {
                activityContainer.setPadding(0, 0, 0, 0);
            }
        }
    }
}

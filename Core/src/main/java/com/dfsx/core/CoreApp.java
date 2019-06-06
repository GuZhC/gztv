package com.dfsx.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.dfsx.core.common.Util.LoginCacheFileUtil;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.common.model.Account;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/7/8.
 */
public class CoreApp extends Application {
    private static CoreApp mApplication = null;

    //阿里云推送的设备id
    private String deviceId;

    //heyang  2016-10-27
    private Account mCurAccount = null;

    //  手机号  heyang  2017-11-7
    private String _telePhone = "";
    private String _verfityNumber = "";

    /**
     * 存储当前activity活动中
     */
    private ArrayList<BaseActivity> activityExistList;

    public boolean isSuggestFlag() {
        return isSuggestFlag;
    }

    public void setSuggestFlag(boolean suggestFlag) {
        isSuggestFlag = suggestFlag;
    }

    private boolean isSuggestFlag = true;   //是否提交意见反馈

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        android.support.multidex.MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        this.mCurAccount = LoginCacheFileUtil.getAccountFromCache();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static CoreApp getInstance() {
        return mApplication;
    }

    public void setCurrentAccount(Account account) {
        this.mCurAccount = account;
    }

    public Account getUser() {
//        return mCurAccount;

        if (mCurAccount != null) {
            Account.UserBean bean = mCurAccount.getUser();
            if (bean == null)
                mCurAccount = null;
        }
        return mCurAccount;
    }


    public String getPotrtServerUrl() {
        return AppApiManager.getInstance().getAppApi().getBaseServerUrl();
    }

    public String getShoppServerUrl() {
        return AppApiManager.getInstance().getAppApi().getShopServerUrl();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void addActivity(BaseActivity activity) {
        if (activityExistList == null) {
            activityExistList = new ArrayList<BaseActivity>();
        }
        activityExistList.add(activity);
    }

    public void removeActivity(BaseActivity activity) {
        if (activityExistList != null) {
            activityExistList.remove(activity);
        }
    }

    public String getCurrentToken() {
        return mCurAccount == null ? null : mCurAccount.getToken();
    }

    public boolean isOpenVerityTele() {
        return true;
    }

    public BaseActivity getTopActivity() {
        if (activityExistList != null && activityExistList.size() > 0) {
            return activityExistList.get(activityExistList.size() - 1);
        }
        return null;
    }

    public void exitApp() {
        if (activityExistList != null) {
            for (BaseActivity act : activityExistList) {
                if (act != null && !act.isFinishing()) {
                    act.finish();
                }
            }
        }
    }

    /**
     * 下载跟新APP
     */
    public boolean downloadAndUpdateApp() {

        return false;
    }

    public String get_telePhone() {
        return _telePhone;
    }

    public void set_telePhone(String _telePhone) {
        this._telePhone = _telePhone;
    }

    public String get_verfityNumber() {
        return _verfityNumber;
    }

    public void set_verfityNumber(String _verfityNumber) {
        this._verfityNumber = _verfityNumber;
    }

}

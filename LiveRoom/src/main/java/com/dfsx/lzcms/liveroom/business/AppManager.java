package com.dfsx.lzcms.liveroom.business;

/**
 * IApp的管理类
 * Created by liuwb on 2016/10/24.
 */
public class AppManager {

    private static AppManager instance = new AppManager();

    private IApp mApp;

    private AppManager() {

    }

    public static AppManager getInstance() {
        return instance;
    }

    public void setIApp(IApp app) {
        this.mApp = app;
    }

    public IApp getIApp() {
        return mApp;
    }
}

package com.dfsx.core;

/**
 * Created by liuwb on 2016/9/2.
 */
public class AppApiManager {

    private static AppApiManager instance = new AppApiManager();

    private AppApi appApi;

    private AppApiManager() {

    }

    public void setAppApi(AppApi api) {
        this.appApi = api;
    }

    public static AppApiManager getInstance() {
        return instance;
    }

    public AppApi getAppApi() {
        return appApi;
    }

}

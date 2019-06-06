package com.dfsx.core;

/**
 * Created by liuwb on 2016/9/2.
 */
public interface AppApi {

    String getSession();

    String getSessionName();

    //heyang 2016-10-27  获取登录门户接口地址 给登陆模块
    String getBaseServerUrl();

    //heyang 2017-12-1  获取积分商城地址
    String getShopServerUrl();
}

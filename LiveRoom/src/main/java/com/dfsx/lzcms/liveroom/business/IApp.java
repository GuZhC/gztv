package com.dfsx.lzcms.liveroom.business;

/**
 * 同过IApp接口条用平台的数据
 * Created by liuwb on 2016/10/24.
 */
public interface IApp {

    boolean isLogin();

    long getLoginUserId();

    String getUserName();

    String getUserPassword();

    String getUserNickName();

    String getCurrentToken();

    String getHttpBaseUrl();

    String getCommonHttpUrl();

    /**
     * 获取直播的分享链接
     *
     * @return
     */
    String getLiveShareUrl();

    /**
     * 获取竞猜直播的分享链接
     *
     * @return
     */
    String getGuessLiveShareUrl();

    /**
     * 获取直播回放的分享链接
     *
     * @return
     */
    String getLiveBackPlayShareUrl();

    String getMobileWebUrl();


    IGetPlayBackInfo getPlayBackManager();
}

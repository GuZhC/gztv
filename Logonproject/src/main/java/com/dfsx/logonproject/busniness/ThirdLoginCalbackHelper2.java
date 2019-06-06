package com.dfsx.logonproject.busniness;

import com.dfsx.thirdloginandshare.login.AbsThirdLogin;

import java.util.ArrayList;

/**
 * 第三方登录回调代理
 * 主要是因为微信登录会关闭启动登录的页面，导致不能很好的接收到登录的回调
 * Created by liuwb on 2016/11/22.
 */
public class ThirdLoginCalbackHelper2 implements AbsThirdLogin.OnThirdLoginListener {
    private static ThirdLoginCalbackHelper2 instance = new ThirdLoginCalbackHelper2();

    private String accessToken;
    private String openId;
    private int thirdType;

    private boolean isThirdCallBack = false;

    private boolean isError = false;

    private ArrayList<AbsThirdLogin.OnThirdLoginListener> listeners = new ArrayList<AbsThirdLogin.OnThirdLoginListener>();

    private ThirdLoginCalbackHelper2() {

    }

    public static ThirdLoginCalbackHelper2 getInstance() {
        return instance;
    }

    @Override
    public void onThirdLoginCompelete(String access_token, String openId, int oauthtype) {
        if (listeners != null) {
            for (AbsThirdLogin.OnThirdLoginListener listener : listeners) {
                listener.onThirdLoginCompelete(access_token, openId, oauthtype);
            }
        } else {
            isThirdCallBack = true;
            this.accessToken = access_token;
            this.openId = openId;
            this.thirdType = oauthtype;
            this.isError = false;
        }

    }

    @Override
    public void onThirdLoginError(int oauthtype) {
        if (listeners != null) {
            for (AbsThirdLogin.OnThirdLoginListener listener : listeners) {
                listener.onThirdLoginError(oauthtype);
            }
        } else {
            isThirdCallBack = true;
            isError = true;
        }
    }

    public void addOnThirdLoginListener(AbsThirdLogin.OnThirdLoginListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    public void removeOnThirdLoginListener(AbsThirdLogin.OnThirdLoginListener l) {
        if (listeners != null && l != null) {
            listeners.remove(l);
        }
    }

    public boolean isAvailableCallBack() {
        boolean is = isThirdCallBack;
        this.isThirdCallBack = false;
        return is;
    }

    public boolean isError() {
        boolean is = isError;
        this.isError = false;
        return is;
    }

    public int getThirdType() {
        return thirdType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getOpenId() {
        return openId;
    }
}

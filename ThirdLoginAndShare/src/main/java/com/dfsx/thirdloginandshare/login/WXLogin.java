package com.dfsx.thirdloginandshare.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpUtils;
import com.dfsx.core.network.IHttpResponseListener;
import com.dfsx.core.rx.RxBus;
import com.dfsx.thirdloginandshare.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Administrator on 2015/12/24.
 */
public class WXLogin extends AbsThirdLogin implements IHttpResponseListener {

    public static final String GET_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    public static final String REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";
    public static final String GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?";

    public static final int TYPE_GET_TOKEN = 11;
    public static final int TYPE_GET_USER_INFO = 12;
    public static final int TYPE_REFERSH_TOKEN = 13;

    private int currentReqType;

    private String accessToken;
    private String refreshToken;
    private String openId;
    private Subscription rxBus;

    public WXLogin(Context context, OnThirdLoginListener thirdLoginListener) {
        super(context, thirdLoginListener);
        rxBus = RxBus.getInstance().toObserverable(Intent.class)
                .subscribe(new Observer<Intent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(Intent intent) {
                        onEvent(intent);
                    }
                });
    }

    @Override
    public void login() {
        WXUtil.sendAuthRequest(context);
    }

    public void onEvent(Intent intent) {
        if (intent != null && ACTION_WX_AUTH.equals(intent.getAction())) {
            Bundle b = intent.getBundleExtra(KEY_AUTH_DATA);
            /**
             *  微信登录，手机并没有登录微信号，无法返回取消登录
             *  heyang  2018-11-13
             */
            if (b == null) {
                thirdLoginListener.onThirdLoginError(Weixin);
                return;
            }
            String code = b.getString("_wxapi_sendauth_resp_token");
            if (!TextUtils.isEmpty(code)) {
                currentReqType = TYPE_GET_TOKEN;
                thirdLoginListener.onThirdLoginCompelete(code, "", Weixin);
//                getAccessToken(code, this);
            } else {
                thirdLoginListener.onThirdLoginError(Weixin);
            }
        }
    }

    @Override
    public void onDestory() {
        if (rxBus != null) {
            rxBus.unsubscribe();
        }
    }


    public void getAccessToken(String code, IHttpResponseListener responseListener) {
        String url = GET_TOKEN;
        url += "appid=" + Constants.WeChat_APP_ID;
        url += "&secret=" + Constants.WeChat_APP_SECRET;
        url += "&code=" + code;
        url += "&grant_type=" + "authorization_code";
        HttpUtils.doGet(url, null, responseListener);
    }

    public void refreshToken(String refreshCode, IHttpResponseListener responseListener) {
        String url = REFRESH_TOKEN;
        url += "appid=" + Constants.WeChat_APP_ID;
        url += "&refresh_token=" + refreshToken;
        url += "&grant_type=" + "refresh_token";
        HttpUtils.doGet(url, null, responseListener);
    }

    public void getUserInfo(String tokern, String openid, IHttpResponseListener responseListener) {
        String url = GET_USER_INFO;
        url += "openid=" + openid;
        url += "&access_token=" + tokern;
        HttpUtils.doGet(url, null, responseListener);
    }

    @Override
    public void onComplete(Object tag, String response) {
        try {
            switch (currentReqType) {
                case TYPE_GET_TOKEN:
                    JSONObject tokenObject = new JSONObject(response);
                    accessToken = tokenObject.optString("access_token");
                    refreshToken = tokenObject.optString("refresh_token");
                    openId = tokenObject.optString("openid");
                    currentReqType = TYPE_GET_USER_INFO;
                    getUserInfo(accessToken, openId, this);
                    break;
                case TYPE_REFERSH_TOKEN:
                    break;
                case TYPE_GET_USER_INFO:
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject res = new JSONObject(response);
                        long errorCode = res.optLong("errcode");
                        if (errorCode == 0) {
                            saveOnCompleteString(openId, response.toString(), Weixin);
                            thirdLoginListener.onThirdLoginCompelete(accessToken, openId, Weixin);
                        } else {
                            thirdLoginListener.onThirdLoginError(Weixin);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Object tag, ApiException e) {
        thirdLoginListener.onThirdLoginError(Weixin);
    }
}

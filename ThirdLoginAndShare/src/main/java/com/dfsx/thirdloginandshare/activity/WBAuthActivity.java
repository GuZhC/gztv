/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfsx.thirdloginandshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.dfsx.core.rx.RxBus;
import com.dfsx.thirdloginandshare.Constants;
import com.dfsx.thirdloginandshare.R;
import com.dfsx.thirdloginandshare.login.UsersAPI;
import com.dfsx.thirdloginandshare.share.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

/**
 * 该类主要演示如何进行授权、SSO登陆。
 *
 * @author SINA
 * @since 2013-09-29
 */
public class WBAuthActivity extends Activity {
    public static final String KEY_RESPONSE_ACTION = "com.tixa.plugin.activity.WBAuthActivity_RESPONSE_TYPE";
    public static final String KEY_RESPONSE_DATA = "com.tixa.plugin.activity.WBAuthActivity_RESPONSE_DATA";
    public static final String KEY_RESPONSE_OPENID = "com.tixa.plugin.activity.WBAuthActivity_RESPONSE_openid";
    public static final String KEY_ACCESS_TOKEN = "com.tixa.plugin.activity.WBAuthActivity_RESPONSE_access_token";


    public static final String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    private static final String TAG = "weibosdk";

    private AuthInfo mAuthInfo;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;
    private String responseAction;

    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        responseAction = getIntent().getStringExtra(KEY_RESPONSE_ACTION);

        // 创建微博实例
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, Constants.WEIBO_APP_KEY,
                Constants.REDIRECT_URL, WEIBO_SCOPE);
        mSsoHandler = new SsoHandler(WBAuthActivity.this, mAuthInfo);

//        authorizeAll();

        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken != null && mAccessToken.isSessionValid()) {
            updateTokenView(true);
        } else {
            authorizeAll();
        }
    }


    public void authorizeByClient() {
        mSsoHandler.authorizeClientSso(new AuthListener());
    }

    public void authorizeByWeb() {
        mSsoHandler.authorizeWeb(new AuthListener());
    }

    public void authorizeAll() {
        mSsoHandler.authorize(new AuthListener());
    }

    public void loginOut() {
        AccessTokenKeeper.clear(getApplicationContext());
        mAccessToken = new Oauth2AccessToken();
        updateTokenView(false);
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息 
            String phoneNum = mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                updateTokenView(false);

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(WBAuthActivity.this, mAccessToken);
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(WBAuthActivity.this, message, Toast.LENGTH_LONG).show();
                postError();
            }
            finish();
        }

        @Override
        public void onCancel() {
            postError();
            finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WBAuthActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            postError();
            finish();
        }
    }

    private void postError() {
        Intent errorIntent = new Intent(responseAction);
        RxBus.getInstance().post(errorIntent);
    }

    /**
     * 显示当前 Token 信息。
     *
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
        Intent intent = new Intent(responseAction);
        intent.putExtra(KEY_RESPONSE_DATA, "");
        intent.putExtra(KEY_RESPONSE_OPENID, mAccessToken.getUid());
        intent.putExtra(KEY_ACCESS_TOKEN, mAccessToken.getToken());
//            EventBus.getDefault().post(intent);
        RxBus.getInstance().post(intent);
        finish();
        //乐山版本不需要获取用户信息
//        UsersAPI mUsersAPI = new UsersAPI(this, Constants.WEIBO_APP_KEY, mAccessToken);
//        long uid = Long.parseLong(mAccessToken.getUid());
//        mUsersAPI.show(uid, mListener);
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            Intent intent = new Intent(responseAction);
            intent.putExtra(KEY_RESPONSE_DATA, response);
            intent.putExtra(KEY_RESPONSE_OPENID, mAccessToken.getUid());
            intent.putExtra(KEY_ACCESS_TOKEN, mAccessToken.getToken());
//            EventBus.getDefault().post(intent);
            RxBus.getInstance().post(intent);
            finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WBAuthActivity.this, "获取人物信息失败", Toast.LENGTH_LONG).show();
            postError();
            finish();
        }
    };
}
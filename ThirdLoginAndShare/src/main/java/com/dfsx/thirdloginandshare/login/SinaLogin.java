package com.dfsx.thirdloginandshare.login;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.dfsx.core.rx.RxBus;
import com.dfsx.thirdloginandshare.activity.WBAuthActivity;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Administrator on 2015/12/28.
 */
public class SinaLogin extends AbsThirdLogin {
    public static final String ACTION = "com.tixa.plugin.login.thirdLogin_SinaLogin";

    private Subscription rxBus;

    public SinaLogin(Context context, OnThirdLoginListener thirdLoginListener) {
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
                        if ((TextUtils.equals(intent.getAction(), ACTION)))   // 新加的这句  heyang 2018-8-21 微新登录会发Intent , 也会受到，报微博授权失败
                            onEvent(intent);
                    }
                });
    }

    @Override
    public void login() {
        Intent intent = new Intent(context, WBAuthActivity.class);
        intent.putExtra(WBAuthActivity.KEY_RESPONSE_ACTION, ACTION);
        context.startActivity(intent);
    }

    public void onEvent(Intent intent) {
        if (intent != null) {
            if (ACTION.equals(intent.getAction())) {
                String openid = intent.getStringExtra(WBAuthActivity.KEY_RESPONSE_OPENID);
                String access_token = intent.getStringExtra(WBAuthActivity.KEY_ACCESS_TOKEN);
                String userInfo = intent.getStringExtra(WBAuthActivity.KEY_RESPONSE_DATA);
                if (!TextUtils.isEmpty(openid) &&
                        !TextUtils.isEmpty(access_token)) {
                    if (!TextUtils.isEmpty(userInfo)) {
                        saveOnCompleteString(openid, userInfo, Sinaweibo);
                    }
                    thirdLoginListener.onThirdLoginCompelete(access_token, openid, Sinaweibo);
                } else {
                    thirdLoginListener.onThirdLoginError(Sinaweibo);
                }
            } else {
                thirdLoginListener.onThirdLoginError(Sinaweibo);
            }
        }
    }

    @Override
    public void onDestory() {
        if (rxBus != null) {
            rxBus.unsubscribe();
        }
    }
}

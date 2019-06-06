package com.dfsx.thirdloginandshare.login;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.dfsx.core.rx.RxBus;
import com.dfsx.thirdloginandshare.activity.QQCallBackAct;
import rx.Observer;
import rx.Subscription;

/**
 * QQ登录还没有做测试的
 * Created by liuwb on 2015/12/24.
 */
public class QQLogin extends AbsThirdLogin {

    public static final String ACTION = "com.tixa.plugin.login.thirdLogin_QQLogin";

    private Subscription busRegesitor;

    public QQLogin(Context context, OnThirdLoginListener thirdLoginListener) {
        super(context, thirdLoginListener);
        init();
    }

    private void init() {
        busRegesitor = RxBus.getInstance().toObserverable(Intent.class)
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
        Intent intent = new Intent(context, QQCallBackAct.class);
        intent.putExtra(QQCallBackAct.KEY_ACT_TYPE, QQCallBackAct.TYPE_LOGIN_ACT);
        intent.putExtra(QQCallBackAct.KEY_RESPONSE_ACTION, ACTION);
        context.startActivity(intent);
    }

    public void onEvent(Intent intent) {
        if (intent != null) {
            if (ACTION.equals(intent.getAction())) {
                String openid = intent.getStringExtra(QQCallBackAct.KEY_RESPONSE_OPENID);
                String access_token = intent.getStringExtra(QQCallBackAct.KEY_ACCESS_TOKEN);
                String userInfo = intent.getStringExtra(QQCallBackAct.KEY_RESPONSE_DATA);
                if (!TextUtils.isEmpty(openid) &&
                        !TextUtils.isEmpty(access_token)) {
                    if (!TextUtils.isEmpty(userInfo)) {
                        saveOnCompleteString(openid, userInfo, Qq);
                    }
                    thirdLoginListener.onThirdLoginCompelete(access_token, openid, Qq);
                } else {
                    thirdLoginListener.onThirdLoginError(Qq);
                }
            } else {
                thirdLoginListener.onThirdLoginError(Qq);
            }
        }
    }


    @Override
    public void onDestory() {
        if (busRegesitor != null) {
            busRegesitor.unsubscribe();
        }
    }
}

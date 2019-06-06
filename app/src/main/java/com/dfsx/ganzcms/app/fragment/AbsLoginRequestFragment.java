package com.dfsx.ganzcms.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.rx.RxBus;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 要求用户登录的功能的公共的Fragment
 * Created by liuwb on 2017/7/14.
 */
public abstract class AbsLoginRequestFragment extends Fragment {

    protected IsLoginCheck isLoginCheck;
    private Subscription loginSubscription;

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onBeforeCheckLogin(view, savedInstanceState);
        isLoginCheck = new IsLoginCheck(getContext());
        if (isLoginCheck.checkLogin()) {
            onLogined();
        }
        initRegister();
    }

    protected void initRegister() {
        loginSubscription = RxBus.getInstance().toObserverable(Intent.class)
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (IntentUtil.ACTION_LOGIN_OK.equals(intent.getAction())) {
                            onLogined();
                        }
                    }
                });
    }


    public abstract void onBeforeCheckLogin(View view, @Nullable Bundle savedInstanceState);

    /**
     * 已经登录过了
     */
    public abstract void onLogined();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginSubscription != null) {
            loginSubscription.unsubscribe();
        }
    }
}

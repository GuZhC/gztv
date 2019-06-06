package com.dfsx.core.common.view;

import android.content.Context;
import android.view.View;

/**
 * Created by liuwb on 2017/4/21.
 */
public interface IUserAgreement {

    void showAgreement(Context context, View anchor, String agreementText, CallBack callBack);

    void dismiss();

    /**
     * 用户协议的回调
     */
    public interface CallBack {
        void callback(boolean isUserAgree);
    }
}

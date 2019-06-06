package com.dfsx.core.common.view;

import android.app.Activity;
import android.view.View;

/**
 * 用户协议
 * Created by liuwb on 2017/4/21.
 */
public class UserAgreementManger {

    private static UserAgreementManger instance = new UserAgreementManger();

    private IUserAgreement userAgreementWindow;

    public static UserAgreementManger getInstance() {
        return instance;
    }

    private UserAgreementManger() {

    }

    public void setUserAgreementWindow(IUserAgreement userAgreementWindow) {
        this.userAgreementWindow = userAgreementWindow;
    }

    public void showUserAgreementWindow(Activity act, View anchor, String agreementText,
                                        IUserAgreement.CallBack callBack) {
        if (userAgreementWindow == null) {
            return;
        }
        if (act != null && !act.isFinishing()) {
            userAgreementWindow.showAgreement(act, anchor, agreementText, callBack);
        }
    }

    public void dismiss() {
        if (userAgreementWindow == null) {
            return;
        }
        userAgreementWindow.dismiss();
    }
}

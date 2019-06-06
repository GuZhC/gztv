package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.view.View;
import com.dfsx.core.common.view.IUserAgreement;

/**
 * Created by liuwb on 2017/4/21.
 */
public class LiveUserAgreement implements IUserAgreement {

    private LiveCreateAlertPopupwindow createAlertPopupwindow;

    @Override
    public void showAgreement(Context context, View anchor, String agreementText, final CallBack callBack) {
        createAlertPopupwindow = new LiveCreateAlertPopupwindow(context);
        createAlertPopupwindow.setShowText(agreementText);
        createAlertPopupwindow.show(anchor);
        createAlertPopupwindow.setOnAgreeCallBackListener(new LiveCreateAlertPopupwindow.OnAgreeCallBackListener() {
            @Override
            public void agreeCallBack(boolean isAgree) {
                if (callBack != null) {
                    callBack.callback(isAgree);
                }
            }
        });
    }

    @Override
    public void dismiss() {
        if (createAlertPopupwindow != null) {
            createAlertPopupwindow.dismiss();
        }
    }
}

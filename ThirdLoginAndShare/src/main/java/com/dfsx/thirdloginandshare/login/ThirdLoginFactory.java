package com.dfsx.thirdloginandshare.login;

import android.content.Context;

/**
 * Created by Administrator on 2015/12/24.
 */
public class ThirdLoginFactory {
    public static AbsThirdLogin createThirdLogin(Context context, int oauthtype,
                                                 AbsThirdLogin.OnThirdLoginListener listener) {
        AbsThirdLogin thirdLogin = null;
        switch (oauthtype) {
            case AbsThirdLogin.Weixin:
                thirdLogin = new WXLogin(context, listener);
                break;
            case AbsThirdLogin.Qq:
                thirdLogin = new QQLogin(context, listener);
                break;
            case AbsThirdLogin.Sinaweibo:
                thirdLogin = new SinaLogin(context, listener);
                break;
        }
        return thirdLogin;
    }
}

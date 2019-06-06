package com.dfsx.ganzcms.app.business;

import android.text.TextUtils;
import com.dfsx.core.common.model.LoginParams;
import com.dfsx.ganzcms.app.App;
import com.dfsx.lzcms.liveroom.business.IApp;
import com.dfsx.lzcms.liveroom.business.IGetPlayBackInfo;

/**
 * Created by liuwb on 2016/10/24.
 */
public class LiveRoomAppImp implements IApp {
    @Override
    public boolean isLogin() {
        return App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null;
    }

    @Override
    public long getLoginUserId() {
        return App.getInstance().getUser() != null &&
                App.getInstance().getUser().getUser() != null ?
                App.getInstance().getUser().getUser().getId() :
                0;
    }

    @Override
    public String getUserName() {
        if (App.getInstance().getUser() == null ||
                App.getInstance().getUser().getUser() == null ||
                App.getInstance().getUser().loginInfo == null) {
            return "";
        }
        return App.getInstance().getUser().getUser().getUsername();
    }

    @Override
    public String getUserPassword() {
        if (App.getInstance().getUser() == null ||
                App.getInstance().getUser().loginInfo == null) {
            return "";
        }
        boolean isThird = App.getInstance().getUser().loginInfo != null &&
                App.getInstance().getUser().loginInfo.loginType != LoginParams.LOGIN_TYPE_USER;
        return isThird ? null
                : App.getInstance().getUser().loginInfo.password;
    }

    @Override
    public String getUserNickName() {
        if (App.getInstance().getUser() == null ||
                App.getInstance().getUser().getUser() == null) {
            return "test";
        }
        String nickName = App.getInstance().getUser().getUser().getNickname();
        if (TextUtils.isEmpty(nickName)) {
            nickName = App.getInstance().getUser().getUser().getUsername();
        }
        return nickName;
    }

    @Override
    public String getCurrentToken() {
        return App.getInstance().getCurrentToken();
    }

    @Override
    public String getHttpBaseUrl() {
        return App.getInstance().getmSession().getLiveServerUrl();
    }

    @Override
    public String getCommonHttpUrl() {
        return App.getInstance().getmSession().getPortalServerUrl();
    }

    @Override
    public String getLiveShareUrl() {
        return App.getInstance().getmSession().getBaseShareLiveUrl();
    }

    @Override
    public String getGuessLiveShareUrl() {
        return App.getInstance().getmSession().getBaseShareGuessLiveUrl();
    }

    @Override
    public String getLiveBackPlayShareUrl() {
        return App.getInstance().getmSession().getBaseShareBackplayUrl();
    }

    @Override
    public String getMobileWebUrl() {
        return App.getInstance().getmSession().getBaseMobileWebUrl();
    }

    @Override
    public IGetPlayBackInfo getPlayBackManager() {
        return new GetPlayBackInfoImpl();
    }
}

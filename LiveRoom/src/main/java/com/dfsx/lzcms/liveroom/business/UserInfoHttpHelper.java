package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.lzcms.liveroom.model.UserDetailsInfo;
import com.google.gson.Gson;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 获取用户信息详情功能
 * Created by liuwb on 2017/3/13.
 */
public class UserInfoHttpHelper implements IGetUserDetailsInfo {


    public UserInfoHttpHelper() {
    }

    @Override
    public UserDetailsInfo getUserDetailsSync(long userId) {
        String httpUrl = AppManager.getInstance().getIApp().getCommonHttpUrl()
                + "/public/users/" + userId;
        String resStr = HttpUtil.executeGet(httpUrl,
                new HttpParameters(),
                AppManager.getInstance().
                        getIApp().getCurrentToken());
        if (!TextUtils.isEmpty(resStr)) {
            UserDetailsInfo userDetailsInfo = new Gson().
                    fromJson(resStr, UserDetailsInfo.class);
            return userDetailsInfo;
        }
        return null;
    }

    @Override
    public void getUserDetailsAsync(long userId, final ICallBack<UserDetailsInfo> iCallBack) {
        Observable.just(userId)
                .observeOn(Schedulers.io())
                .map(new Func1<Long, UserDetailsInfo>() {
                    @Override
                    public UserDetailsInfo call(Long id) {
                        return getUserDetailsSync(id);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserDetailsInfo>() {
                    @Override
                    public void call(UserDetailsInfo userDetailsInfo) {
                        if (iCallBack != null) {
                            iCallBack.callBack(userDetailsInfo);
                        }
                    }
                });
    }
}

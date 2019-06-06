package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.logonproject.busniness.AccountApi;
import com.google.gson.Gson;
import org.json.JSONObject;

/**
 * Created by liuwb on 2016/8/30.
 */
public class UserInfoHelper {

    public static void getUserInfo(Context context, int uid, DataRequest.DataCallback callback) {
        String url = App.getInstance().getmSession().makeUrl("services/user/" + uid + ".json");
        new DataRequest<Account>(context) {

            @Override
            public Account jsonToBean(JSONObject jsonObject) {
                Account userItem = new Account();
                userItem.getUser().setId(jsonObject.optLong("uid"));
                userItem.getUser().setEmail(jsonObject.optString("mail"));
                //                userItem.getUser(). = jsonObject.optString("timezone");
                userItem.getUser().setUsername(jsonObject.optString("name"));
                long sp = jsonObject.optLong("created");
                userItem.getUser().setCreation_time(sp);
                String imgPath = "";
                if (!jsonObject.isNull("picture")) {
                    JSONObject mn = jsonObject.optJSONObject("picture");
                    if (mn != null) {
                        imgPath = mn.optString("url");
                    }
                }
                userItem.getUser().setAvatar_url(imgPath);
                return userItem;
            }
        }.getData(new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setToken(App.getInstance().getCurrentToken()).
                build(), false).
                setCallback(callback);
    }

    public static void updateCurrentUserInfo(Context context, DataRequest.DataCallback<Account> callback) {
        if (App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null) {
            String url = App.getInstance().getPotrtServerUrl() + "/public/users/current";
            new DataRequest<Account>(context) {

                @Override
                public Account jsonToBean(JSONObject jsonObject) {
                    Account account = App.getInstance().getUser();
                    Account.UserBean userBean = new Gson().fromJson(jsonObject.toString(),
                            Account.UserBean.class);
                    if (userBean != null && userBean.getId() != 0) {
                        account.setUser(userBean);
                        App.getInstance().setCurrentAccount(account);
                        AccountApi.saveAccount(account);
                    }
                    return account;
                }
            }.getData(new DataRequest.HttpParamsBuilder().
                    setUrl(url).
                    setToken(App.getInstance().getCurrentToken()).
                    build(), false).
                    setCallback(callback);
        }
    }

    public static void updateUserLogo(Context context) {

    }
}

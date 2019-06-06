package com.dfsx.logonproject.busniness;

import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.model.LoginParams;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.IHttpResponseListener;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.IGetToken;
import com.dfsx.core.network.datarequest.TokenListener;
import com.google.gson.Gson;
import org.json.JSONObject;

/**
 * Created by liuwb on 2016/9/2.
 */
public class TokenHelper implements IGetToken {

    private TokenListener tokenListener;
    AccountApi accountApi;

    public TokenHelper() {
        accountApi = new AccountApi(CoreApp.getInstance().getApplicationContext());
    }

    public String getTokenSync() {
        Account user = CoreApp.getInstance().getUser();
        if (user != null && user.loginInfo != null) {
            AccountApi accountApi = new AccountApi(CoreApp.getInstance().getApplicationContext());
            JSONObject json = null;
            if (user.loginInfo.loginType == LoginParams.LOGIN_TYPE_USER) {
                String url = CoreApp.getInstance().getPotrtServerUrl() +
                        "/public/users/current/login";
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("username", user.loginInfo.userName);
                    obj.put("password", user.loginInfo.password);
                    json = JsonHelper.httpPostJson(url, obj, null);
                } catch (Exception e) {
                }
            } else {
                String access_token = user.loginInfo.access_token;
                String web_url = CoreApp.getInstance().getPotrtServerUrl();
                JSONObject obj = new JSONObject();
                try {
                    if ("qq".equals(user.loginInfo.provider)) {
                        web_url += "/public/users/current/qq-login/app";
                        obj.put("access_token", access_token);
                    } else if ("weixin".equals(user.loginInfo.provider)) {
                        web_url += "/public/users/current/wechat-login";
                        obj.put("code", access_token);
                    } else {
                        web_url += "/public/users/current/weibo-login/app";
                        obj.put("access_token", access_token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String respose = HttpUtil.execute(web_url, new HttpParameters(obj), null);
                try {
                    json = JsonCreater.jsonParseString(respose);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
            if (json != null) {
                Account curAccount = new Gson().
                        fromJson(json.toString(), Account.class);

                curAccount.loginInfo = user.loginInfo;
                AccountApi.saveAccount(curAccount);
                CoreApp.getInstance().setCurrentAccount(curAccount);
                return curAccount.getToken();
            }
        } else {
            //heyang   2017-9-20     版本更新失败.清除过期无效token
            AccountApi.saveAccount(null);
            CoreApp.getInstance().setCurrentAccount(null);
        }
        return null;
    }

    public void getTokenAsync(TokenListener listener) {
        this.tokenListener = listener;
        Account user = CoreApp.getInstance().getUser();
        if (user != null && user.loginInfo != null) {
            AccountApi accountApi = new AccountApi(CoreApp.getInstance().getApplicationContext());
            if (user.loginInfo.loginType == LoginParams.LOGIN_TYPE_USER) {
                String url = CoreApp.getInstance().getPotrtServerUrl() +
                        "/public/users/current/login";
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("username", user.loginInfo.userName);
                    obj.put("password", user.loginInfo.password);

                    HttpUtil.doPost(url, new HttpParameters(obj), null, asyncResponseListener);
                } catch (Exception e) {
                }
            } else {
                accountApi.thirdLogin(user.loginInfo.provider,
                        user.loginInfo.client_id,
                        user.loginInfo.access_token,
                        user.loginInfo.uid, new DataRequest.DataCallback<Account>() {
                            @Override
                            public void onSuccess(boolean isAppend, Account data) {
                                if (tokenListener != null) {
                                    tokenListener.tokenCallback(data.getToken());
                                }
                            }

                            @Override
                            public void onFail(ApiException e) {
                                if (tokenListener != null) {
                                    tokenListener.tokenCallback(null);
                                }
                            }
                        });
            }

        } else {
            //heyang   2017-9-20     版本更新失败.清除过期无效token
            AccountApi.saveAccount(null);
            CoreApp.getInstance().setCurrentAccount(null);
            if (tokenListener != null) {
                tokenListener.tokenCallback(null);
            }
        }
    }


    private IHttpResponseListener asyncResponseListener = new IHttpResponseListener() {
        @Override
        public void onComplete(Object tag, String response) {
            try {
                JSONObject json = JsonCreater.jsonParseString(response);
                if (json != null) {
                    Account curAccount = new Gson().
                            fromJson(json.toString(), Account.class);
                    curAccount.loginInfo = CoreApp.getInstance().getUser().loginInfo;
                    AccountApi.saveAccount(curAccount);
                    CoreApp.getInstance().setCurrentAccount(curAccount);

                    if (tokenListener != null) {
                        tokenListener.tokenCallback(curAccount.getToken());
                    }
                } else {
                    if (tokenListener != null) {
                        tokenListener.tokenCallback(null);
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
                if (tokenListener != null) {
                    tokenListener.tokenCallback(null);
                }
            }
        }

        @Override
        public void onError(Object tag, ApiException e) {
            if (tokenListener != null) {
                tokenListener.tokenCallback(null);
            }
        }
    };
}

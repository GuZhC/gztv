package com.dfsx.logonproject.busniness;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.BuildConstants;
import com.dfsx.core.CoreApp;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.common.Util.LoginCacheFileUtil;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.model.Account;
import com.dfsx.core.common.model.LoginParams;
import com.dfsx.core.common.model.TalentRankInfo;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.*;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.network.datarequest.GetTokenManager;
import com.dfsx.core.rx.RxBus;
import com.dfsx.selectedmedia.MediaModel;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create  by    heyang   2016/10/27
 */

public class AccountApi {
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SO_TIMEOUT = 5000;
    private static final String TAG = "HTTPOP";
    private DataRequest.DataCallback callback;
    private LoginParams loginParams;

    public AccountApi(Context context) {
        mContext = context;
    }

    private Context mContext;

    private DataRequest<Account> loginRequestData = new
            DataRequest<Account>(CoreApp.getInstance().getApplicationContext()) {
                @Override
                public Account jsonToBean(JSONObject json) {
                    Account curAccount = new Gson().fromJson(String.valueOf(json), Account.class);
                    //                    Account curAccount = new Account();
                    //                    curAccount.token = json.optString("token");
                    //                    curAccount.sessionName = json.optString("session_name");
                    //                    curAccount.sessionId = json.optString("sessid");
                    //                    JSONObject user = json.optJSONObject("user");
                    //                    curAccount.id = user.optLong("uid");
                    //                    curAccount.userName = user.optString("name");
                    curAccount.loginInfo = loginParams;
                    saveAccount(curAccount);
                    return curAccount;
                }
            };

    public static void saveAccount(Account account) {
        FileUtil.saveFileByAccountId(CoreApp.getInstance().getApplicationContext(),
                LoginCacheFileUtil.ACCOUNT_FILE_NAME, LoginCacheFileUtil.ACCOUNT_DIR, account);
    }

    private DataRequest.DataCallback<Account> tempCallBack = new DataRequest.DataCallback<Account>() {
        @Override
        public void onSuccess(boolean isAppend, Account data) {
            //            App.getInstance().setCurrentAcount(data);
            CoreApp.getInstance().setCurrentAccount(data);

            if (callback != null) {
                callback.onSuccess(isAppend, data);
            }
        }

        @Override
        public void onFail(ApiException e) {
            if (callback != null) {
                callback.onFail(e);
            }
        }
    };

    public static Account getAccountFromCache() {
        Account account = (Account) FileUtil.getFileByAccountId(CoreApp.getInstance().getApplicationContext(),
                LoginCacheFileUtil.ACCOUNT_FILE_NAME, LoginCacheFileUtil.ACCOUNT_DIR);

        return account;
    }

    public static void clearAccountFromCache() {
        FileUtil.delFileByAccontId(CoreApp.getInstance().getApplicationContext(),
                LoginCacheFileUtil.ACCOUNT_FILE_NAME, LoginCacheFileUtil.ACCOUNT_DIR);
    }

    public void thirdLogin(String provider, String client_id, String access_token, String uid, DataRequest.DataCallback callback) {
        this.callback = callback;
        loginParams = createLoginParams(provider, client_id, access_token, uid);
        String web_url = CoreApp.getInstance().getPotrtServerUrl();
        JSONObject obj = new JSONObject();
        try {
            if ("qq".equals(provider)) {
                web_url += "/public/users/current/qq-login/app";
                obj.put("access_token", access_token);
            } else if ("weixin".equals(provider)) {
                web_url += "/public/users/current/wechat-login/app";
                obj.put("code", access_token);
            } else {
                web_url += "/public/users/current/weibo-login/app";
                obj.put("access_token", access_token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().setUrl(web_url).
                setJsonParams(obj).build();
        loginRequestData.getData(httpParams, false).setCallback(tempCallBack);
    }

    public void login(final String username, String password, DataRequest.DataCallback callback) throws ApiException {
        this.callback = callback;
        loginParams = createLoginParams(username, password);
        //        String url = App.getInstance().getmSession().getPortalServerUrl() +
        //                "/public/users/current/login";
        String url = CoreApp.getInstance().getPotrtServerUrl() +
                "/public/users/current/login";
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", password);
            obj.put("remember_login", true);
            DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
                    setUrl(url).setJsonParams(obj).setToken(null).build();
            loginRequestData.getData(params, false).
                    setCallback(tempCallBack);
        } catch (Exception e) {
            throw ApiException.error("登陆失败");
        }
    }

    /*
     *  同步登陆
     */
    public void loginSysnic(final String username, String password) throws ApiException {
        loginParams = createLoginParams(username, password);
        String url = CoreApp.getInstance().getPotrtServerUrl() +
                "/public/users/current/login";
        try {
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", password);
            obj.put("remember_login", true);
            String result = HttpUtil.exePost(url, new HttpParameters(obj), CoreApp.getInstance().getCurrentToken());
            if (result != null && !TextUtils.isEmpty(result)) {
                JSONObject jsonObject = JsonCreater.jsonParseString(result);
                Account curAccount = new Gson().fromJson(String.valueOf(jsonObject), Account.class);
                curAccount.loginInfo = loginParams;
//                    String token = jsonObject.optString("token");
//                    curAccount.setToken(token);
                saveAccount(curAccount);
                CoreApp.getInstance().setCurrentAccount(curAccount);
            }
        } catch (Exception e) {
            throw ApiException.error(e.toString());
        }
    }


    /**
     * 同步获取头像上传地址
     *
     * @return
     */
    public static String getAvaterUploadURL() throws ApiException {
        String urlString = CoreApp.getInstance().getPotrtServerUrl() + "/public/avatars/uploader";
        HttpURLConnection conn = null;
        String result = "";
        try {
            if (BuildConstants.DEBUG)
                Log.d(TAG, urlString);

            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(SO_TIMEOUT);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json");
            int code = conn.getResponseCode();
            InputStreamReader in = null;
            if (code == HttpStatus.SC_OK) {
                in = new InputStreamReader(conn.getInputStream());
            } else
                in = new InputStreamReader(conn.getErrorStream());
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            StringBuffer sbuff = new StringBuffer();
            while (((inputLine = buffer.readLine()) != null)) {
                sbuff.append(inputLine);
            }
            in.close();
            result = sbuff.toString();

        } catch (Exception e) {
            throw new ApiException("获取头像上传地址失败");
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        if (result.contains("http"))
            return result.substring(1, result.length() - 1);
        else
            return null;
    }

    /**
     * 同步上传头像
     *
     * @param upUrl 上传地址
     * @param path  头像路径
     * @return 服务器保存的图片相对地址
     */
    public static String uploadAvatar(String upUrl, String path) throws ApiException {
        String ralatPath = "";
        try {
            JSONObject json = JsonHelper.httpUpload(upUrl, path, null);
            if (json != null) {
                int isOk = json.optInt("isOK");
                if (isOk == 1) {
                    ralatPath = json.optString("relativepath");
                    ralatPath += json.optString("name");
                } else {
                    throw new ApiException("服务器返回出错信息");
                }
            } else {
                throw new ApiException("返回的json为null");
            }
        } catch (Exception e) {
            throw new ApiException("上传头像出错");
        }
        if (ralatPath == null)
            return null;
        return ralatPath;
    }

    public static String uploadAvatarHttpUtl(String upUrl, String path) throws ApiException {
        String servicePath = "";
        try {
            String res = HttpUtil.uploadFileSynchronized(upUrl,
                    new File(path), null);
            Log.e("TAG", "uploadAvatarHttpUtl  upfile " + path + "--- res == " + res);
            boolean isOk = false;
            JSONObject json = new JSONObject(res);
            isOk = json.optInt("isOK") > 0;
            if (!isOk) {
                if (json.has("errMessage")) {
                    String code = json.optString("errMessage");
                    String msg = decodeUnicode(code);
                    throw new ApiException(msg);
                }
            }
            String name = json.optString("name");
            String dir = json.optString("relativepath");
            servicePath = dir + name;
        } catch (Exception e) {
            throw new ApiException(e);
        }
        return servicePath;
    }

    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher(str);
        int start = 0;
        int start2 = 0;
        StringBuffer sb = new StringBuffer();
        while (m.find(start)) {
            start2 = m.start();
            if (start2 > start) {
                String seg = str.substring(start, start2);
                sb.append(seg);
            }
            String code = m.group(1);
            int i = Integer.valueOf(code, 16);
            byte[] bb = new byte[4];
            bb[0] = (byte) ((i >> 8) & 0xFF);
            bb[1] = (byte) (i & 0xFF);
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append(String.valueOf(set.decode(b)).trim());
            start = m.end();
        }
        start2 = str.length();
        if (start2 > start) {
            String seg = str.substring(start, start2);
            sb.append(seg);
        }
        return sb.toString();
    }

    private LoginParams createLoginParams(String username, String password) {
        LoginParams params = new LoginParams();
        params.loginType = LoginParams.LOGIN_TYPE_USER;
        params.userName = username;
        params.password = password;
        return params;
    }

    private LoginParams createLoginParams(String provider, String client_id, String access_token, String uid) {
        LoginParams params = new LoginParams();
        params.loginType = LoginParams.LOGIN_TYPE_THIRD_USER;
        params.provider = provider;
        params.client_id = client_id;
        params.access_token = access_token;
        params.uid = uid;
        return params;
    }

    public void logout(final DataRequest.DataCallback logoutCallback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/logout";
        //这里不采用DataRequest。因为这里不需要自动验证token失效
        Observable.just(url)
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        String token = getCurrentAccount() != null ? getCurrentAccount().getToken() : null;
                        String res = HttpUtil.execute(s, new HttpParameters(), token);
                        return res;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (logoutCallback != null) {
                            logoutCallback.onFail(new ApiException(e));
                        }
                    }

                    @Override
                    public void onNext(String res) {
                        if (TextUtils.isEmpty(res)) {
                            RxBus.getInstance().post(new Intent(IntentUtil.ACTION_LOGIN_OUT));
                            if (logoutCallback != null) {
                                logoutCallback.onSuccess(false, true);
                            }
                        } else {
                            if (logoutCallback != null) {
                                String errorMsg = "token验证失败";
                                try {
                                    JSONObject resJson = new JSONObject(res);
                                    errorMsg = resJson.optString("message");
                                    if (TextUtils.isEmpty(errorMsg)) {
                                        errorMsg = "未知错误";
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                logoutCallback.onFail(new ApiException(errorMsg));
                            }
                        }
                    }
                });
    }

    public void changePassword(String oldPassword, String newPassword, DataRequest.DataCallback callback) throws ApiException {
        if (CoreApp.getInstance().getUser() == null) throw new ApiException("no login");
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/change-password";
        //        String url = App.getInstance().getmSession().
        //                makeUrl("UpdatePassword.ashx", "user",
        //                        CoreApp.getInstance().getUser().getUser().getUsername(), "oldpasswd",
        //                        oldPassword, "newpasswd", newPassword);
        try {
            JSONObject obj = new JSONObject();
            obj.put("old_password", oldPassword);
            obj.put("new_password", newPassword);
            DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                    setUrl(url).
                    setToken(CoreApp.getInstance().getUser().getToken()).build();
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
                    return json != null && !TextUtils.isEmpty(json.toString());
                }
            }.getData(httpParams, false).setCallback(callback);
        } catch (JSONException e) {
            throw ApiException.error("修改密码失败");
        }
    }

    public void changeOtherInfo(Account user, DataRequest.DataCallback callback) throws ApiException {
        if (CoreApp.getInstance().getUser() == null) throw new ApiException("no login");
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current";
        //        String url = App.getInstance().getmSession().
        //                makeUrl("UpdatePassword.ashx", "user",
        //                        CoreApp.getInstance().getUser().getUser().getUsername(), "oldpasswd",
        //                        oldPassword, "newpasswd", newPassword);
        try {
            String objStr = new Gson().toJson((Account) user);
            JSONObject jsonObject = new JSONObject(objStr);
            DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                    setUrl(url).
                    setJsonParams(jsonObject).
                    setToken(CoreApp.getInstance().getUser().getToken()).build();
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
                    return json != null && !TextUtils.isEmpty(json.toString());
                }
            }.getData(httpParams, false).setCallback(callback);
        } catch (JSONException e) {
            throw ApiException.error("修改基本信息失败");
        }
    }

    public void register(final String username, String password, String email, String mobile, String averPath,
                         final DataRequest.DataCallback callback) throws ApiException {
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", "");
            obj.put("password", password);
            obj.put("email", email);
            obj.put("mobile", "");
            obj.put("avatar_path", averPath);
        } catch (JSONException e) {
            e.printStackTrace();
            throw ApiException.error(e.getMessage());
        }
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/register";
        HttpUtil.doPost(url, new HttpParameters(obj), null, new IHttpResponseListener() {
            @Override
            public void onComplete(Object tag, String response) {
                boolean isSuccess = TextUtils.isDigitsOnly(response) &&
                        Long.valueOf(response) > 0;
                callback.onSuccess(false, isSuccess);
            }

            @Override
            public void onError(Object tag, ApiException e) {
                callback.onFail(e);
            }
        });
        //        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
        //                setUrl(url).setJsonParams(obj).build();
        //        new DataRequest<Account>(mContext) {
        //            @Override
        //            public Account jsonToBean(JSONObject json) {
        //                if (json == null && TextUtils.isEmpty(json.toString())) return null;
        //                Account act = new Gson().fromJson(json.toString(), Account.class);
        ////                Account account = new Account();
        ////                account.getUser().setUsername(username);
        ////                account.getUser().setId(json.optLong("uid"));
        //                return act;
        //            }
        //        }.getData(httpParams, false).setCallback(callback);
    }

    public void exist() {
        CoreApp.getInstance().setCurrentAccount(null);
        clearAccountFromCache();
    }

    public Account getCurrentAccount() {
        return CoreApp.getInstance().getUser();
    }

    public void setCurrentAccount(Account account) {
        CoreApp.getInstance().setCurrentAccount(account);
    }

    /**
     * 更新我的推送设备
     *
     * @param context
     * @param deviceId 设备类型 1 – android,2 – ios
     * @param callback 设备推送id
     */
    public static void submitDeviceId(Context context, String deviceId, DataRequest.DataCallback<Void> callback) {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/user-device";
        DataRequest<Void> request = new DataRequest<Void>(context) {
            @Override
            public Void jsonToBean(JSONObject json) {
                return null;
            }
        };
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device_type", 1);
            jsonObject.put("device_id", deviceId);
        } catch (Exception e) {
        }
        String token = "";
        if (CoreApp.getInstance().getUser() != null) {
            token = CoreApp.getInstance().getUser().getToken();
        }
        request.getData(new DataRequest.HttpParamsBuilder().setUrl(url).setToken(
                token).setJsonParams(jsonObject).
                setRequestType(DataReuqestType.PUT).build(), false);
        request.setCallback(callback);
    }

    //////////////////     dazhoucms  注册  登陆     ///////////////////////////


    /**
     * 获取用户头像
     *
     * @return
     */
    public String getCurrentUserInfor() throws ApiException {
        String userImagString = "";
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current";
        String result = HttpUtil.executeGet(url, new HttpParameters(), CoreApp.getInstance().getUser().getToken());
        if (result != null && !TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = JsonCreater.jsonParseString(result);
                if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                    userImagString = jsonObject.optString("avatar_url");
                }
            } catch (ApiException e) {
                throw new ApiException(e);
            }
        }
        return userImagString;
    }

    /***
     *   获取用户的排名信息
     * @param callback
     */
    public void getMyRankInfo(DataRequest.DataCallback<TalentRankInfo> callback) {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/rank";
        new DataRequest<TalentRankInfo>(mContext) {

            @Override
            public TalentRankInfo jsonToBean(JSONObject json) {
                TalentRankInfo info = null;
                if (json != null && !TextUtils.isEmpty(json.toString()))
                    info = new Gson().fromJson(json.toString(), TalentRankInfo.class);
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setToken(CoreApp.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(callback);
    }

    public void updateTelephone(String telePhone, long verfityBNumber) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/change-phone ";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone_number", telePhone);
            jsonObject.put("verification_code", verfityBNumber);

            HttpUtil.doPut(url, new HttpParameters(jsonObject), null, new IHttpResponseListener() {
                @Override
                public void onComplete(Object tag, String response) {
                    boolean isSuccess = TextUtils.isDigitsOnly(response) &&
                            Long.valueOf(response) > 0;
                    callback.onSuccess(false, isSuccess);
                }

                @Override
                public void onError(Object tag, ApiException e) {
                    callback.onFail(e);
                }
            });
        } catch (JSONException e) {
            throw ApiException.error("修改手机号失败");
        }
    }

    /**
     * 注册
     *
     * @param usrName
     * @param pass
     * @param telePhone
     * @param verityNumber
     * @throws ApiException
     */
    public void registerDzUser(String usrName, String pass, String telePhone, String verityNumber, final DataRequest.DataCallback callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/register ";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", "");
            jsonObject.put("nickname", usrName);
            jsonObject.put("password", pass);
            jsonObject.put("email", "");
            jsonObject.put("phone_number", telePhone);
            jsonObject.put("verification_code", verityNumber);

            HttpUtil.doPost(url, new HttpParameters(jsonObject), null, new IHttpResponseListener() {
                @Override
                public void onComplete(Object tag, String response) {
//                    long  id=-1;
//                    if (response != null && !TextUtils.isEmpty(response)) {
//                        try {
//                            JSONObject json = JsonCreater.jsonParseString(response);
//                            id=json.optLong("id");
//                        } catch (ApiException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    boolean isSuccess = TextUtils.isDigitsOnly(response) &&
                            Long.valueOf(response) > 0;
                    callback.onSuccess(false, isSuccess);
                }

                @Override
                public void onError(Object tag, ApiException e) {
                    callback.onFail(e);
                }
            });

//            DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
//                    setUrl(url).
//                    setJsonParams(jsonObject).
//                    setRequestType(DataReuqestType.POST).
//                    setToken(CoreApp.getInstance().getUser().getToken()).build();
//            new DataRequest<Long>(mContext) {
//                @Override
//                public Long jsonToBean(JSONObject json) {
//                    return json.optLong("id");
//                }
//            }.getData(httpParams, false).setCallback(callback);
        } catch (JSONException e) {
            throw ApiException.error("用户注册失败");
        }
    }

    /*
    * 同步创建用户
     */
    public boolean resgisterSysnic(String usrName, String pass, String telePhone, String verityNumber) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/register ";
        boolean response = true;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", "");
            jsonObject.put("nickname", usrName);
            jsonObject.put("password", pass);
            jsonObject.put("email", "");
            jsonObject.put("phone_number", telePhone);
            jsonObject.put("verification_code", verityNumber);
            String result = HttpUtil.exePost(url, new HttpParameters(jsonObject), CoreApp.getInstance().getCurrentToken());
            JSONObject obj = JsonCreater.jsonParseString(result);
            if (obj == null || TextUtils.equals(obj.toString().trim(), "{}")) {
                response = TextUtils.isDigitsOnly(result) &&
                        Long.valueOf(result) > 0;
            } else {
                if (obj.has("error")) {
                    Log.e("TAG", "onCommit() resgisterSysnic  failed " + obj.optString("message"));
                    throw new ApiException(obj.optString("message"));
                }
            }
//            response = TextUtils.isDigitsOnly(result) &&
//                    Long.valueOf(result) > 0;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            throw new ApiException(e);
        }
        return response;
    }


//    public void logonDzUser(String usrName, String pass, String telePhone, long verityNumber, DataRequest.DataCallback callback) throws ApiException {
//        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/login ";
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("username", telePhone);
//            jsonObject.put("password", pass);
//            jsonObject.put("remember_login", false);
//            DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
//                    setUrl(url).
//                    setJsonParams(jsonObject).
//                    setRequestType(DataReuqestType.POST).
//                    setToken(CoreApp.getInstance().getUser().getToken()).build();
//            new DataRequest<Account>(mContext) {
//                @Override
//                public Account jsonToBean(JSONObject json) {
//
//                    return json.optLong("id");
//                }
//            }.getData(httpParams, false).setCallback(callback);
//        } catch (JSONException e) {
//            throw ApiException.error("用户注册失败");
//        }
//    }

    /***
     *  重置密码
     * @param telePhone
     * @param newPass
     * @param verityNumber
     * @param callback
     * @throws ApiException
     */
    public void resetPassWord(String telePhone, String newPass, String verityNumber, DataRequest.DataCallback callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/reset-password";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receiver", telePhone);
            jsonObject.put("new_password", newPass);
            jsonObject.put("verification_code", verityNumber);
            DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                    setUrl(url).
                    setJsonParams(jsonObject).
                    setRequestType(DataReuqestType.POST).
                    setToken("").build();
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
                    return json != null && !TextUtils.isEmpty(json.toString());
                }
            }.getData(httpParams, false).setCallback(callback);
        } catch (JSONException e) {
            throw ApiException.error("修改密码失败");
        }
    }

    /***
     * 手机验证码是否有效
     * @param telePhone
     * @param verityNumber
     * @param callback
     * @throws ApiException
     */
    public void checkVerifyValid(String telePhone, String verityNumber, int type, DataRequest.DataCallback<Boolean> callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/phone/verification-code/validate";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone_number", telePhone);
            jsonObject.put("verification_code", verityNumber);
            jsonObject.put("type", type);
            DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                    setUrl(url).
                    setJsonParams(jsonObject).
                    setRequestType(DataReuqestType.POST).
                    build();
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
//                    boolean isSuccess = TextUtils.isDigitsOnly(json.toString()) &&
//                            Long.valueOf(json.toString()) > 0;
//                    return json != null && !TextUtils.isEmpty(json.toString());
                    boolean isSuccess = true;
                    if (json != null) {
                        isSuccess = json.optBoolean("res");
//                        String  pp=json.optString("res");
//                        int a = 0;
//                        int aa = 0;
//                        int bb = 0;
                    }
                    return isSuccess;
                }
            }.getData(httpParams, false).setCallback(callback);
        } catch (JSONException e) {
            throw ApiException.error("手机验证码验证失败 ");
        }
    }

    /***
     *  验证手机号  /  更换手机号
     * @param telePhone
     * @param verityNumber
     * @param callback
     * @throws ApiException
     */
    public void checkTeleValid(String telePhone, String verityNumber, DataRequest.DataCallback<Boolean> callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/change-phone";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone_number", telePhone);
            jsonObject.put("verification_code", verityNumber);
            DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                    setUrl(url).
                    setJsonParams(jsonObject).
                    setRequestType(DataReuqestType.PUT).
                    setToken(CoreApp.getInstance().getUser().getToken()).
                    build();
            new DataRequest<Boolean>(mContext) {
                @Override
                public Boolean jsonToBean(JSONObject json) {
//                    boolean isSuccess = TextUtils.isDigitsOnly(json.toString()) &&
//                            Long.valueOf(json.toString()) > 0;
//                    return json != null && !TextUtils.isEmpty(json.toString());
                    boolean isSuccess = true;
//                    if (json != null) {
//                        isSuccess = json.optBoolean("res");
//                    }
                    return isSuccess;
                }
            }.getData(httpParams, false).setCallback(callback);
        } catch (JSONException e) {
            throw ApiException.error("手机验证码验证失败 ");
        }
    }

    /**
     * 验证手机号是否存在
     *
     * @param telePhone
     * @param callback
     * @throws ApiException
     */
    public void isTelePhoneExist(long telePhone, DataRequest.DataCallback callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/phone/" + telePhone + "/exist";
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.POST).
                setToken(CoreApp.getInstance().getUser().getToken()).build();
        new DataRequest<Boolean>(mContext) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                return json != null && !TextUtils.isEmpty(json.toString());
            }
        }.getData(httpParams, false).setCallback(callback);
    }

    /**
     * 发送短信验证码
     *
     * @param telePhone
     * @param callback
     * @throws ApiException
     */
    public void sendPhoneMessage(String telePhone, int type, final DataRequest.DataCallback callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/phone/send-code";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", telePhone);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.POST).
                setJsonParams(jsonObject).
                setToken("").build();
        new DataRequest<Boolean>(mContext) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                if (json != null && !TextUtils.isEmpty(json.toString())) {
                    int error = json.optInt("error");
                    if (error == 500) {
                        //   callback.onFail(ApiException.error(json.toString()));
                    } else {
                        //   callback.onSuccess(false, true);
                    }
                }
                return json != null && !TextUtils.isEmpty(json.toString());
            }
        }.getData(httpParams, false).setCallback(callback);
    }

    /*
    2.1.34	获取我的三方授权信息
     */
    public List<Account.AuthorThrid> getMyThirdInfo() throws ApiException {
        List<Account.AuthorThrid> aulist = null;
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/third-auths";
        try {
            String result = HttpUtil.executeGet(url, new HttpParameters(), CoreApp.getInstance().getCurrentToken());
            if (result != null && !TextUtils.isEmpty(result)) {
                JSONObject obj = JsonCreater.jsonParseString(result);
                if (obj != null) {
                    JsonCreater.checkThrowError(obj);
                    aulist = new ArrayList<>();
                    Iterator iterator = obj.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String values = obj.optString(key);
                        Account.AuthorThrid item = new Account.AuthorThrid();
                        try {
                            item.setType(Integer.parseInt(key));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        item.setNickName(values);
                        aulist.add(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            throw new ApiException(e);
        }
        return aulist;
    }

    /*
     * 解除第三方绑定
     */
    public void removeThirdAuthor(int type, DataRequest.DataCallback callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/third-auths/" + type + "/unbind";
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.POST).
                setToken(CoreApp.getInstance().getCurrentToken()).build();
        new DataRequest<Boolean>(mContext) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }.getData(httpParams, false).setCallback(callback);
    }

    /*
  *  绑定第三方
  */
    public void bindThirdAuthor(int type, String token, DataRequest.DataCallback callback) throws ApiException {
        String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/";
        JSONObject json = new JSONObject();
        try {
            if (type == 0) {
                url += "wechat-login";
                json.put("code", token);
            } else if (type == 1) {
                url += "weibo-login";
                json.put("access_token", token);
            } else {
                url += "qq-login";
                json.put("access_token", token);
            }
            url += "/app/bind";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataRequest.HttpParams httpParams = new DataRequest.HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.POST).
                setJsonParams(json).
                setToken(CoreApp.getInstance().getCurrentToken()).build();
        new DataRequest<String>(mContext) {
            @Override
            public String jsonToBean(JSONObject json) {
                String name = "";
                if (!(json == null || TextUtils.isEmpty(json.toString()))) {
                    name = json.optString("alias_name");
                }
                return name;
            }
        }.
                getData(httpParams, false).
                setCallback(callback);

    }

    /**
     * 意见反馈
     */
    public void publisSuggest(final String content, ArrayList<MediaModel> imgs, final ProgessCallbacface callbacface, Observer<Long> op) throws ApiException {
        final String url = CoreApp.getInstance().getPotrtServerUrl() + "/public/users/current/feedback";
        Observable.just(imgs).
                observeOn(Schedulers.io()).
                map(new Func1<ArrayList<MediaModel>, Long>() {
                    @Override
                    public Long call(ArrayList<MediaModel> list) {
                        long topicId = -1;
                        String upFIleUrl = getImaUpfileUrl();
                        if (TextUtils.isEmpty(upFIleUrl)) {
                            try {
                                throw new ApiException("获取图片上传地址失败!");
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                        JSONArray arr = new JSONArray();
                        if (list != null && !list.isEmpty()) {
                            for (int i = 0; i < list.size(); i++) {
                                MediaModel model = list.get(i);
                                if (!CoreApp.getInstance().isSuggestFlag()) break;
                                String sharPath = synchronNewUpfile(upFIleUrl, model.url);
                                if (!TextUtils.isEmpty(sharPath)) {
                                    callbacface.MyObtainProgressValues(i * 20);
                                    //                                        JSONObject ob = new JSONObject();
//                                        JSONArray yy = new JSONArray();
//                                        yy.put(sharPath);
//                                        ob.put("name", model.name);
//                                        ob.put("paths", yy);
                                    arr.put(sharPath);
                                }
                            }
                        }
                        JSONObject parmaObj = new JSONObject();
                        try {
                            parmaObj.put("content", content);
                            parmaObj.put("image_paths", arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (CoreApp.getInstance().isSuggestFlag()) {
                            JSONObject result = JsonHelper.httpPostJson(url, parmaObj, CoreApp.getInstance().getUser().getToken());
                            if (result != null && !TextUtils.isEmpty(result.toString())) {
                                topicId = checkUserVerityError(result);
                                if (topicId != -2) topicId = result.optLong("id");
                            }
                        }
                        return topicId;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(op);
    }


    //  String  pictureUrl = CoreApp.getInstance().getPotrtServerUrl() + "/public/pictures/uploader";
//    String pictureUrl = CoreApp.getInstance().getPotrtServerUrl() + "/public/avatars/uploader";

    /**
     * 获取图片的上传地址
     */
    public String getImaUpfileUrl() {
        String result = "";
        String pictureUrl = CoreApp.getInstance().getPotrtServerUrl() + "/public/avatars/uploader";
        result = HttpUtil.executeGet(pictureUrl, new HttpParameters(), CoreApp.getInstance().getUser().getToken());
        if (result != null && !TextUtils.isEmpty(result)) {
            try {
                JSONObject obj = JsonCreater.jsonParseString(result);
                if (obj != null) {
                    String token = checkTokenError(obj);
                    if (token != null)
                        result = HttpUtil.executeGet(pictureUrl, new HttpParameters(), token);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    /**
     * 检查token 是否有效
     */
    public String checkTokenError(JSONObject obj) {
        String token = null;
        int code = obj.optInt("code");
        if (code == 401) {
            token = GetTokenManager.getInstance().getIGetToken().getTokenSync();
        }
        return token;
    }

    /**
     * 检查token 是否有效
     */
    public long checkUserVerityError(JSONObject obj) {
        long id = 0;
        if (obj != null) {
            id = obj.optLong("error");
            if (id == 1100) {
                id = -2;
            }
        }
        return id;
    }

    public String synchronNewUpfile(String upUrl, String path) {
        String ralatPath = "";
        try {
            upUrl = checkUrl(upUrl);
            JSONObject json = JsonHelper.httpUpload(upUrl, path, null);
            if (json != null) {
                int isOk = json.optInt("isOK");
                if (isOk == 1) {
                    ralatPath = json.optString("relativepath");
                    ralatPath += json.optString("name");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ralatPath;
    }

    public static String checkUrl(String urlStr) {
        int index = urlStr.indexOf("\"");
        if (index != -1)
            urlStr = urlStr.substring(index + 1);
        index = urlStr.lastIndexOf("\"");
        if (index != -1)
            urlStr = urlStr.substring(0, index);
        return urlStr;
    }

}

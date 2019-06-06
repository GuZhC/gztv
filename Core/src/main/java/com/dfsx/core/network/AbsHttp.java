package com.dfsx.core.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * edited by yanyi on 15/8/20.
 */
public abstract class AbsHttp {
    public static String TAG = "http";
    // 用户唯一token
    public static String PARAM_TOKEN = "apiCode";
    // 用户唯一账户ID
    public static String PARAM_ACCOUNTID = "accountId";
    // 是否是release版本
    public static String PARAM_ISRELEASE = "isRelease";
    // 当前版本名称
    public static String PARAM_VERSIONNAME = "vn";
    // 当前版本code
    public static String PARAM_VERSIONCODE = "clientVersion";   // 将vc 修改为 clientVersion
    // 渠道
    public static String PARAM_APPKEY = "ak";
    // 客户端类型
    public static String PARAM_CLIENTTYPE = "clientType";
    public static long NET_TOKEN_TIMEOUT_ERROR = -2004;
    public static String TOKEN_TIMEOUT_INTENT = "com.tixa.action.token.timeout";

    public abstract void doPost(@NonNull String httpUrl, @NonNull IHttpParameters params, String token, IHttpResponseListener requestListener);

    public abstract void doPut(@NonNull String httpUrl, @NonNull IHttpParameters params, String token, IHttpResponseListener requestListener);

    public abstract void doDel(@NonNull String httpUrl, @NonNull IHttpParameters params, String token, IHttpResponseListener requestListener);

    public abstract void doGet(@NonNull String httpUrl, @NonNull IHttpParameters params, String token, IHttpResponseListener requestListener);

    public abstract void doGet(@NonNull String httpUrl, String token, IHttpResponseListener requestListener);

    //同步上传的接口
    public abstract String uploadSync(@NonNull String httpUrl, File file, Object... objs) throws IOException;

    public abstract String uploadSync(@NonNull String httpUrl, String filePath, Object... objs) throws IOException;

    //异步上传的接口
    public abstract void uploadAsync(@NonNull String httpUrl, File file, IHttpResponseListener requestListener, Object... objs) throws IOException;

    public abstract void uploadAsync(@NonNull String httpUrl, String filePath, IHttpResponseListener requestListener, Object... objs) throws IOException;

    public abstract void cancel(@NonNull Object tag);

    public abstract String execute(@NonNull String httpUrl, @NonNull IHttpParameters params, String token);

    public abstract String executeGet(@NonNull String httpUrl, @NonNull IHttpParameters params, String token);

    /**
     * * by  create heyang 2018/1/19
     * 增加同步post请求
     * @param httpUrl
     * @param params
     * @param token
     * @return
     */
    public abstract String exePost(String httpUrl, IHttpParameters params, String token);

    /*
    * token过期发送广播 if result = TOKEN_TIMEOUT_INTENT then reLogin
    */
    public static final void sendTokenTimeOutBroad(String httpResult) {
        try {
            if (!TextUtils.isEmpty(httpResult)) {
                httpResult = httpResult.trim();
                if (httpResult.startsWith("{")) {
                    try {
                        JSONObject tempJson = new JSONObject(httpResult);

                        if (tempJson.has("errorCode")) {
                            long result = tempJson.optLong("errorCode");
                            sendTokenTimeOutBroad(result);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else if (httpResult.startsWith("[")) {
                    //TODO
                } else {
                    try {
                        long result = Long.parseLong(httpResult);
                        sendTokenTimeOutBroad(result);
                    } catch (NumberFormatException e) {
                        //UNDO
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final void sendTokenTimeOutBroad(long errorCode) {
        if (errorCode == NET_TOKEN_TIMEOUT_ERROR) {
//            LXUtil.sendLXBroadCast(LXApplication.getInstance(), new Intent(TOKEN_TIMEOUT_INTENT));
        }
    }
}

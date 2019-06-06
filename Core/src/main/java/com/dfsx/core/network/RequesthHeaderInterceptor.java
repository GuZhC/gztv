package com.dfsx.core.network;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.AppApiManager;
import com.dfsx.core.log.LogUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liuwb on 2016/9/5.
 */
public class RequesthHeaderInterceptor implements Interceptor {

    private String token;
    private String cookie;
    private HashMap<String, String> header;

    public RequesthHeaderInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request newRequest;

        try {
            LogUtils.d("addHeader", "Before");
            Request.Builder builder = request.newBuilder();
            if (token != null) {
                builder.addHeader("X-CSRF-Token", token);
            }
            if (!TextUtils.isEmpty(getCookie())) {
                builder.addHeader("Cookie", getCookie());
            }
            builder.addHeader("User-Agent", getUserAgent());
            builder.addHeader("X-User-Agent", getUserClientAPIUserAgent());
            builder.addHeader("Client", getClientHeader());
            if (header != null) {
                Iterator iter = header.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    String val = (String) entry.getValue();
                    builder.addHeader(key, val);
                }
            }
            newRequest = builder.build();
        } catch (Exception e) {
            LogUtils.d("addHeader", "Error");
            e.printStackTrace();
            return chain.proceed(request);
        }

        LogUtils.d("addHeader", "after");
        return chain.proceed(newRequest);
    }

    public String getUserAgent() {
        String androidVersion = "Android " + Build.VERSION.RELEASE;
        String phoneId = "" + Build.MODEL;
        String text = "Mozilla/5.0" + "(" + androidVersion + "; " + phoneId + ") app";
        return text;
    }

    /**
     * 设置客户请求后天的API版本
     *
     * @return
     */
    protected String getUserClientAPIUserAgent() {
        return "nmip-android-app/1.0";
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;

    }

    /**
     * 需要写上手机型号、系统、版本号等内容。格式如下：
     * HUAWEI P10; Android; 8.0.0
     *
     * @return
     */
    protected String getClientHeader() {
        String model = Build.MODEL + ";";
        String nameVersion = " Android; " + Build.VERSION.RELEASE;

        return model + nameVersion;
    }

    private String getCookie() {
        if (TextUtils.isEmpty(cookie) && AppApiManager.getInstance().getAppApi() != null &&
                !TextUtils.isEmpty(AppApiManager.getInstance().getAppApi().getSession())) {
            String sessionName = AppApiManager.getInstance().
                    getAppApi().getSessionName();
            String sessionId = AppApiManager.getInstance().
                    getAppApi().getSession();
            //            conn.setRequestProperty("Cookie", sessionName + "=" + sessionId);
            return sessionName + "=" + sessionId;
        }
        return cookie;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }
}

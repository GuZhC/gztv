package com.dfsx.core.network;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cn.edu.zafu.coreprogress.helper.ProgressHelper;
import cn.edu.zafu.coreprogress.listener.impl.UIProgressRequestListener;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.squareup.okhttp.*;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * edited by yanyi on 15/8/20.
 */
public class OkHttpUtil extends AbsHttp {
    public static String TAG = "HTTP";
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    private static OkHttpUtil instance;
    private static RequesthHeaderInterceptor headerInterceptor;
    private static CommonHttpErrorCodeHelper errorParser;

    static {
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        errorParser = new CommonHttpErrorCodeHelper();
        //网络监控
        headerInterceptor = new RequesthHeaderInterceptor();
        mOkHttpClient.networkInterceptors().add(headerInterceptor);
    }

    private OkHttpUtil() {
    }

    public static synchronized OkHttpUtil getInstance() {
        if (instance == null) {
            instance = new OkHttpUtil();
        }
        return instance;
    }

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 不会开启异步线程。
     *
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        setHeaderInterceptor();
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     */
    public static void enqueue(final Request request, final Object tag, final IHttpResponseListener listener) {
        final Handler handler = new Handler(Looper.getMainLooper());
        setHeaderInterceptor();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) listener.onError(tag, new ApiException(e));
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    //解析response的body的时候不能在ui线程上面
                    final String result = response.body().string().trim();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            String msg = decodeUnicode(result);
                            String msg =result;
                            if (response.isSuccessful()/* && !TextUtils.isEmpty(msg)*/) {//现在允许返回值为null的类型
                                //打印结果
                                printFormattedHttpResult(request, msg);
                                sendTokenTimeOutBroad(msg);
                                if (listener != null) {
                                    listener.onComplete(tag, msg);
                                }
                            } else {
                                if (errorParser != null) {
                                    errorParser.parser(getRequestErrorCode(msg));
                                }
                                if (listener != null) {
                                    listener.onError(tag, new ApiException(response.code() + ":" + msg));
                                }
                            }

                        }

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(tag, new ApiException(e));
                    }
                }
            }
        });

    }

    public static int getRequestErrorCode(String responseBody) {
        try {
            String errorRes = responseBody;
            JSONObject errorJson = new JSONObject(errorRes);
            if (errorJson != null) {
                if (errorJson.has("error")) {
                    return errorJson.optInt("error");
                }
                return errorJson.optInt("code");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void setHeaderInterceptor() {
        if (!mOkHttpClient.networkInterceptors().contains(headerInterceptor)) {
            mOkHttpClient.networkInterceptors().add(headerInterceptor);
        }

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

    @Override
    public void doPost(String httpUrl, IHttpParameters params, String token, IHttpResponseListener requestListener) {
        LogUtils.d("http", "pos url == " + httpUrl + params.fromJSON());
        headerInterceptor.setToken(token);
        headerInterceptor.setHeader(params.getHeader());
        //中文不能Encode，不用OKHttp里的方法了
        RequestBody formBody = toHttpParamsNoEncode(params);
        //        RequestBody formBody = toOkHttpParam(params).build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .post(formBody)
                .tag(params.getTag())
                .build();
        enqueue(request, params.getTag(), requestListener);
    }

    @Override
    public void doPut(@NonNull String httpUrl, @NonNull IHttpParameters params, String token, IHttpResponseListener requestListener) {
        LogUtils.d("http", "put url == " + httpUrl);
        headerInterceptor.setToken(token);
        headerInterceptor.setHeader(params.getHeader());
        RequestBody formBody = toHttpParamsNoEncode(params);
        Request request = new Request.Builder()
                .url(httpUrl)
                .put(formBody)
                .tag(params.getTag())
                .build();
        enqueue(request, params.getTag(), requestListener);
    }

    @Override
    public void doDel(@NonNull String httpUrl, @NonNull IHttpParameters params, String token, IHttpResponseListener requestListener) {
        LogUtils.d("http", "del url == " + httpUrl);
        headerInterceptor.setToken(token);
        headerInterceptor.setHeader(params.getHeader());
        RequestBody formBody = toHttpParamsNoEncode(params);
        Request request = new Request.Builder()
                .url(httpUrl)
                .delete(formBody)
                .tag(params.getTag())
                .build();
        enqueue(request, params.getTag(), requestListener);
    }

    @Override
    public void doGet(String httpUrl, IHttpParameters params, String token, IHttpResponseListener requestListener) {
        LogUtils.d("http", "get url == " + httpUrl);
        headerInterceptor.setToken(token);
        headerInterceptor.setHeader(params.getHeader());
        boolean isAdd = !(TextUtils.isEmpty(params.fromGet()) || httpUrl.endsWith("?"));
        String url = httpUrl + (isAdd ? "?" : "") + params.fromGet();
        Request request = new Request.Builder()
                .url(url)
                .tag(params.getTag())
                .build();
        enqueue(request, params.getTag(), requestListener);
    }

    @Override
    public void doGet(@NonNull String httpUrl, String token, IHttpResponseListener requestListener) {
        headerInterceptor.setToken(token);
        String url = httpUrl;
        Request request = new Request.Builder()
                .url(url)
                .build();
        enqueue(request, null, requestListener);
    }

    @Override
    public String executeGet(String httpUrl, IHttpParameters params, String token) {
        headerInterceptor.setToken(token);
        headerInterceptor.setHeader(params.getHeader());
        boolean isAdd = !(TextUtils.isEmpty(params.fromGet()) || httpUrl.endsWith("?"));
        String url = httpUrl + (isAdd ? "?" : "") + params.fromGet();
        LogUtils.d("http", "get url == " + url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .tag(params.getTag())
                .build();
        try {
            Response response = execute(request);
            String responseBody = response.body().string().trim();
            if (response != null && !response.isSuccessful()) {
                errorParser.parser(getRequestErrorCode(responseBody));
            }
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String execute(String httpUrl, IHttpParameters params, String token) {
        headerInterceptor.setToken(token);
        headerInterceptor.setHeader(params.getHeader());
        LogUtils.d("http", "post url == " + httpUrl);
        RequestBody formBody = toHttpParamsNoEncode(params);
        Request request = new Request.Builder()
                .url(httpUrl)
                .post(formBody)
                .tag(params.getTag())
                .build();
        try {
            Response response = execute(request);
            String responseBody = response.body().string().trim();
            if (response != null && !response.isSuccessful()) {
                errorParser.parser(getRequestErrorCode(responseBody));
            }
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void cancel(Object object) {
        mOkHttpClient.cancel(object);
    }

    public FormEncodingBuilder toOkHttpParam(IHttpParameters parameters) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        HashMap<String, String> map = parameters.toMap();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (key != null && value != null && !TextUtils.isEmpty(value)) {
                builder.add(key, value);
            }
        }
        return builder;
    }

    private static final MediaType CONTENT_TYPE =
            MediaType.parse("application/json; charset=utf-8");

    /**
     * 中文不能Encode，不用OKHttp里的方法了
     */
    public RequestBody toHttpParamsNoEncode(IHttpParameters parameters) {
        return RequestBody.create(CONTENT_TYPE, parameters.fromJSON());
    }

    @Override
    public String uploadSync(String httpUrl, String fileName, Object... objs) throws IOException {
        File file = new File(fileName);
        return uploadSync(httpUrl, file, objs);
    }

    @Override
    public String uploadSync(String httpUrl, File file, Object... objs) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }
        UIProgressRequestListener uiProgressRequestListener = null;
        if (objs != null && objs.length == 1) {
            uiProgressRequestListener = (UIProgressRequestListener) objs[0];
        }

        RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("hello", "android")
                .addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\""),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        final Request.Builder builder = new Request.Builder();
        builder.url(httpUrl);
        if (uiProgressRequestListener != null) {
            builder.post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener));
        } else {
            builder.post(requestBody);
        }
        final Request request = builder.build();
        Response ret = mOkHttpClient.newCall(request).execute();
        String strRet = ret.body().string().trim();
        return strRet;
    }

    @Override
    public void uploadAsync(String httpUrl, String fileName, IHttpResponseListener requestListener, Object... objs) throws IOException {
        File file = new File(fileName.replace("file://", ""));
        uploadAsync(httpUrl, file, requestListener, objs);
    }

    @Override
    public void uploadAsync(String httpUrl, File file, final IHttpResponseListener requestListener, Object... objs) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        UIProgressRequestListener uiProgressRequestListener = null;
        if (objs != null && objs.length == 1) {
            uiProgressRequestListener = (UIProgressRequestListener) objs[0];
        }

        RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("hello", "android")
                .addFormDataPart("photo", file.getName(), RequestBody.create(null, file))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\""),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        final Request.Builder builder = new Request.Builder();
        builder.url(httpUrl);
        if (uiProgressRequestListener != null) {
            builder.post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener));
        } else {
            builder.post(requestBody);
        }
        final Request request = builder.build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (requestListener != null)
                            requestListener.onError(null, new ApiException(e));
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    //解析response的body的时候不能在ui线程上面
                    final String result = response.body().string().trim();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful() && !TextUtils.isEmpty(result)) {
                                //打印结果
                                printFormattedHttpResult(request, result);
                                sendTokenTimeOutBroad(result);
                                if (requestListener != null) {
                                    requestListener.onComplete(null, result);
                                }
                            } else {
                                if (requestListener != null) {
                                    requestListener.onError(null, new ApiException(result));
                                }
                            }
                        }

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    if (requestListener != null) {
                        requestListener.onError(null, new ApiException(e));
                    }
                }
            }
        });
    }


    /**
     * by  create heyang 2018/1/19
     * 增加同步post请求
     *
     * @param httpUrl
     * @param params
     * @param token
     */
    @Override
    public String exePost(String httpUrl, IHttpParameters params, String token) {
        LogUtils.d("http", "pos url == " + httpUrl + params.fromJSON());
        headerInterceptor.setToken(token);
        headerInterceptor.setHeader(params.getHeader());
        //中文不能Encode，不用OKHttp里的方法了
        RequestBody formBody = toHttpParamsNoEncode(params);
        //        RequestBody formBody = toOkHttpParam(params).build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .post(formBody)
                .tag(params.getTag())
                .build();
        return execute(httpUrl, params, token);
    }

    /**
     * 打印结果
     *
     * @param req
     * @param ret
     */

    private static void printFormattedHttpResult(Request req, String ret) {
        try {
            LogUtils.d(TAG, "请求 -- " + req + "\n" + "返回 -- " + ret);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "打印Http结果出错");
        }
    }
}

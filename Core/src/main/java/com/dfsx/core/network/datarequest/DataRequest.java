package com.dfsx.core.network.datarequest;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.IHttpResponseListener;
import com.dfsx.core.network.JsonHelper;
import com.dfsx.core.network.datarequest.GetTokenManager;
import com.dfsx.core.network.datarequest.IGetToken;
import com.dfsx.core.network.datarequest.TokenListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络获取数据. 网络获取流程已经开启子线程了。数据的回调已经在主线程了。直接刷新UI即可
 * 网络参数由HttpParamsBuilder创建。
 * 使用HttpParams。 如果jsonParams有值，就采用post的方式请求,反之就用get的方式
 * 调用getData方法启动流程
 * /////////////////////////////////////////////////一个用法的样例
 * String url = mSession.getBaseUrl() + "Doc/" + docId + "/Review";
 * DataRequest.HttpParams params = new DataRequest.HttpParamsBuilder().
 * setUrl(url).setToken(mSession.getAccount().token).build();
 * new DataRequest<ArrayList<ReviewItem>>(context) {
 *
 * @Override public ArrayList<ReviewItem> jsonToBean(JSONObject jsonObject) {
 * ArrayList<ReviewItem> dlist = null;
 * if (jsonObject != null) {
 * JSONArray result = jsonObject.optJSONArray("list");
 * if (result != null && result.length() > 0) {
 * dlist = new ArrayList<ReviewItem>();
 * for (int i = 0; i < result.length(); i++) {
 * JSONObject item = (JSONObject) result.optJSONObject(i);
 * ReviewItem doc = new ReviewItem();
 * doc.version = item.optString("version");
 * doc.description = item.optString("description");
 * doc.remark = item.optString("remark");
 * doc.add_user = item.optString("add_user");
 * doc.add_time = item.optString("add_time");
 * dlist.add(doc);
 * }
 * }
 * }
 * return dlist;
 * }
 * }.getData(params, false).
 * setCallback(new DataRequest.DataCallback<ArrayList<ReviewItem>>() {
 * @Override public void onSuccess(boolean isAppend, ArrayList<ReviewItem> data) {
 * //直接刷新UI
 * }
 * @Override public void onFail(ApiException e) {
 * //获取数据失败的回调
 * }
 * });
 * /////////////////////////////////////////////////
 * Created by liuwb on 2016/5/31.
 */
public abstract class DataRequest<T> {

    public static final int MSG_GET_JSON_DATA_SUCCESS = 1;
    public static final int MSG_GET_JSON_DATA_FAIL = 2;

    /**
     * 网络获取数据的回调
     *
     * @param <D>
     */
    public interface DataCallback<D> {
        void onSuccess(boolean isAppend, D data);

        void onFail(ApiException e);
    }

    public interface DataCallbackTag<D> extends DataCallback<D> {
        void onSuccess(Object object, boolean isAppend, D data);
    }

    //    public interface DataCallbackView<D> {
    //        void onSuccess(View  view,boolean isAppend, D data);
    //
    //        void onFail(ApiException e);
    //    }

    protected Context context;

    private DataCallback callback;

    private IGetToken tokenHelper;

    private HttpParams httpParams;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_JSON_DATA_FAIL:
                    if (callback != null) {
                        callback.onFail((ApiException) msg.obj);
                    }
                    break;
                case MSG_GET_JSON_DATA_SUCCESS:
                    if (callback != null) {
                        if (callback instanceof DataCallbackTag) {
                            ((DataCallbackTag) callback).onSuccess(httpParams.tagView, msg.arg1 > 0, (T) msg.obj);
                        }
                        callback.onSuccess(msg.arg1 > 0, (T) msg.obj);
                    }
                    break;
            }
        }
    };

    /**
     * @param context
     */
    public DataRequest(Context context) {
        this.context = context;
    }

    public void setCallback(DataCallback<T> callback) {
        this.callback = callback;
    }

    /**
     * @return
     */
    public JSONObject getNetWorkData(HttpParams params) throws ApiException {
        if (params != null) {
            if (params.jsonParams != null) {
                return JsonHelper.httpPostJson(params.url, params.jsonParams, params.token);
            } else {
                return JsonHelper.httpGetJson(params.url, params.token);
            }
        }
        return null;
    }

    public IGetToken getTokenHelper() {
        tokenHelper = GetTokenManager.getInstance().getIGetToken();
        return tokenHelper;
    }

    /**
     * 把网络请求得到的数据转换成JavaBean数据对象
     *
     * @param json 这个json有可能为null
     * @return
     */
    public abstract T jsonToBean(JSONObject json);

    /**
     * @param params
     * @param isAppend 设置当前请求是否是加载更多事件.
     *                 默认下拉刷新读取缓存操作
     * @return
     */
    public DataRequest getData(HttpParams params, boolean isAppend) {
        return getData(params, isAppend, true);
    }

    /**
     * @param params
     * @param isAppend                    主要用来标识这个请求是刷新还是加载更多
     * @param isReadCacheFirstWhenRefresh 主要用来设置此次请求是否读取缓存.
     *                                    当且仅当isAppend为false，isReadCacheFirstWhenRefresh为TRUE，才获取缓存
     */
    public DataRequest getData(HttpParams params, boolean isAppend, boolean isReadCacheFirstWhenRefresh) {
        //        new GetNetDataThread(params, isAppend).start();
        httpParams = params;
        new OkHttpGetData(params, isAppend, isReadCacheFirstWhenRefresh).start();
        return this;
    }

    class OkHttpGetData {

        private HttpParams params;
        private boolean isAppend;

        private boolean isNeedConfirmTokenTimeOut;

        private boolean isReadCacheWhenRefresh;

        public OkHttpGetData(HttpParams params, boolean isAppend) {
            this.isAppend = isAppend;
            this.params = params;
            this.isReadCacheWhenRefresh = true;
            isNeedConfirmTokenTimeOut = !TextUtils.isEmpty(params.token);
        }

        public OkHttpGetData(HttpParams params, boolean isAppend, boolean isReadCacheWhenRefresh) {
            this.isAppend = isAppend;
            this.params = params;
            this.isReadCacheWhenRefresh = isReadCacheWhenRefresh;
            isNeedConfirmTokenTimeOut = !TextUtils.isEmpty(params.token);
        }

        public void start() {
            if (isReadCacheWhenRefresh) {
                readCacheData(isAppend);
            }
            getDataFromnet();
        }

        private void getDataFromnet() {
            if (params != null) {
                HttpParameters parameters = params.toHttpParameters();
                parameters.setTag(isAppend);
                if (params.reuqestType == DataReuqestType.POST) {
                    HttpUtil.doPost(params.url, parameters, params.token, okhttpResponseListener);
                } else if (params.reuqestType == DataReuqestType.PUT) {
                    HttpUtil.doPut(params.url, parameters, params.token, okhttpResponseListener);
                } else if (params.reuqestType == DataReuqestType.DEL) {
                    HttpUtil.doDel(params.url, parameters, params.token, okhttpResponseListener);
                } else {
                    HttpUtil.doGet(params.url, params.token, parameters, okhttpResponseListener);
                }
            }
        }

        private IHttpResponseListener okhttpResponseListener = new IHttpResponseListener() {
            @Override
            public void onComplete(Object tag, String response) {
                new ParseDataThread(tag, response).start();
            }

            @Override
            public void onError(Object tag, final ApiException e) {
                if (isNeedConfirmTokenTimeOut) {
                    isNeedConfirmTokenTimeOut = false;

                    String error = e.getMessage();
                    if (error.contains("401") ||
                            error.contains("TOKEN")) {
                        if (getTokenHelper() == null) {
                            return;
                        }
                        //判定当前token 过期
                        getTokenHelper().getTokenAsync(new TokenListener() {
                            @Override
                            public void tokenCallback(String token) {
                                if (!TextUtils.isEmpty(token)) {
                                    params.token = token;
                                    getDataFromnet();
                                } else {
                                    sendFailMsg(e);
                                }
                            }
                        });
                        return;
                    }
                }
                sendFailMsg(e);
            }
        };
    }


    private void saveCache(final T data, final boolean isAppend) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                dispatchNetData(data, isAppend);
            }
        }.start();
    }

    class ParseDataThread extends Thread {

        private Object tag;
        private String response;

        public ParseDataThread(Object tag, String response) {
            this.tag = tag;
            this.response = response;
        }

        @Override
        public void run() {
            super.run();
            boolean booleanTag = (Boolean) tag;
            JSONObject json = null;
            try {
                json = jsonParseString(response);
                checkError(json);
                T data = jsonToBean(json);
                saveCache(data, booleanTag);
                sendSuccessMsg(data, booleanTag);
            } catch (ApiException e) {
                e.printStackTrace();
                sendFailMsg(new ApiException(e));
            }
        }
    }

    class GetNetDataThread extends Thread {

        private boolean isAppend;
        private HttpParams params;

        public GetNetDataThread(HttpParams params, boolean isAppend) {
            this.isAppend = isAppend;
            this.params = params;
        }

        @Override
        public void run() {
            super.run();
            readCacheData(isAppend);
            JSONObject json = null;
            try {
                json = getNetWorkData(params);
            } catch (ApiException e) {
                e.printStackTrace();
                sendFailMsg(e);
            }
            if (json != null) {
                if (isTokenError(json)) {//重新获取token的逻辑
                    params.token = getToken();
                    try {
                        json = getNetWorkData(params);
                    } catch (ApiException e) {
                        e.printStackTrace();
                        sendFailMsg(e);
                    }
                }
                try {
                    checkError(json);
                    T data = jsonToBean(json);
                    dispatchNetData(data, isAppend);
                    sendSuccessMsg(data, isAppend);
                } catch (ApiException e) {
                    e.printStackTrace();
                    sendFailMsg(e);
                }
            }
        }

        private boolean isTokenError(JSONObject json) {
            if (params.token == null || TextUtils.isEmpty(params.token)) {
                return false;
            }
            try {
                checkError(json);
            } catch (ApiException e) {
                e.printStackTrace();
                return true;
            }
            return false;
        }

        protected String getToken() {
            return getTokenHelper() == null ? null : getTokenHelper().getTokenSync();
        }
    }


    // 网络连接失败返回错误 json
    public void checkError(JSONObject object) throws ApiException {
        if (object == null)
            throw new ApiException("连接服务器失败");
        String eCode = object.optString("error");
        if (!eCode.isEmpty()) {
            String eMsg = object.optString("message");
            String value = eMsg;
            value = decodeUnicode(eMsg);
            throw new ApiException(eCode + ":" + value);
        }
    }

    //   * 解码 Unicode \\uXXXX
    public String decodeUnicode(String str) {
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

    public static JSONObject StringArraryToJson(String... params) throws JSONException {
        JSONObject object = new JSONObject();

        String[] jsonParams = params;
        if (jsonParams != null) {
            for (int i = 0; i < jsonParams.length - 1; i += 2) {
                object.put(jsonParams[i], jsonParams[i + 1]);
            }
        }
        return object;
    }

    public static JSONObject jsonParseString(String response) throws ApiException {

        JSONObject jsonObject = null;
        try {
            response = response.toString().trim();
            if (response.startsWith("[")) {
                jsonObject = new JSONObject();
                jsonObject.put("result", new JSONArray(response));
            } else if (response.startsWith("{")) {
                jsonObject = new JSONObject(response);
            } else {
                int index = response.indexOf("[");
                if (index == -1) {
                    index = response.indexOf("{");
                }
                if (index != 0) {
                    response = response.substring(index);
                }
                jsonObject = new JSONObject(response);
            }
        } catch (Exception e) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put("res", response);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //throw ApiException.exception(e);
        }
        return jsonObject;
    }

    protected void sendSuccessMsg(T data, boolean isAppend) {
        int appendCount = isAppend ? 1 : 0;
        handler.obtainMessage(MSG_GET_JSON_DATA_SUCCESS, appendCount, appendCount, data).sendToTarget();
    }

    protected void sendFailMsg(ApiException e) {
        handler.obtainMessage(MSG_GET_JSON_DATA_FAIL, e).sendToTarget();
    }

    /**
     * 对数据 进行再次处理. 注意此方法运行在子线程中
     *
     * @param data
     */
    protected void dispatchNetData(T data, boolean isAppend) {

    }

    /**
     * 读取缓存. 当前方法运行在子线程中
     *
     * @param isAppend
     */
    protected void readCacheData(boolean isAppend) {

    }

    /**
     * 使用HttpParams。 如果jsonParams有值，就采用post的方式请求,反之就用get的方式
     */
    public static class HttpParams {
        String url;
        JSONObject jsonParams;
        int intParams;
        long longParams;
        String sParams;
        boolean booleanParams;
        int booleanInit;
        double doubleParams;
        String token;
        Object tagView;
        HashMap<String, String> httpHeader;
        //默认为GET
        DataReuqestType reuqestType = DataReuqestType.GET;

        public HttpParameters toHttpParameters() {
            HttpParameters parameters = null;
            if (jsonParams != null) {
                parameters = new HttpParameters(jsonParams);
            } else if (intParams != 0) {
                parameters = new HttpParameters(intParams);
            } else if (longParams != 0) {
                parameters = new HttpParameters(longParams);
            } else if (!TextUtils.isEmpty(sParams)) {
                parameters = new HttpParameters(sParams);
            } else if (doubleParams != 0) {
                parameters = new HttpParameters(doubleParams);
            } else if (booleanInit != 0) {
                parameters = new HttpParameters(booleanParams);
            } else {
                parameters = new HttpParameters();
            }
            if (httpHeader != null) {
                parameters.setHeader(httpHeader);
            }
            return parameters;
        }
    }

    public static class HttpParamsBuilder {
        private HttpParams params;

        public HttpParamsBuilder() {
            params = new HttpParams();
        }

        public HttpParamsBuilder setUrl(String url) {
            params.url = url;
            return this;
        }

        public HttpParamsBuilder setIntParams(int intParams) {
            params.intParams = intParams;
            params.reuqestType = DataReuqestType.POST;
            return this;
        }

        public HttpParamsBuilder setLongParams(long longParams) {
            params.longParams = longParams;
            params.reuqestType = DataReuqestType.POST;
            return this;
        }

        public HttpParamsBuilder setDoubleParams(double dParams) {
            params.doubleParams = dParams;
            params.reuqestType = DataReuqestType.POST;
            return this;
        }

        public HttpParamsBuilder setStringParams(String sParams) {
            params.sParams = sParams;
            params.reuqestType = DataReuqestType.POST;
            return this;
        }

        public HttpParamsBuilder setBooleanParams(boolean booleanParams) {
            params.booleanParams = booleanParams;
            params.booleanInit = 1;
            params.reuqestType = DataReuqestType.POST;
            return this;
        }

        public HttpParamsBuilder setRequestType(DataReuqestType type) {
            params.reuqestType = type;
            return this;
        }

        public HttpParamsBuilder setTagView(Object view) {
            params.tagView = view;
            return this;
        }

        public HttpParamsBuilder setJsonParams(JSONObject jsonParams) {
            params.jsonParams = jsonParams;
            params.reuqestType = DataReuqestType.POST;
            return this;
        }

        public HttpParamsBuilder setHttpHeader(HashMap<String, String> header) {
            params.httpHeader = header;
            return this;
        }

        public HttpParamsBuilder setToken(String token) {
            params.token = token;
            return this;
        }

        public HttpParams build() {
            return params;
        }
    }

}

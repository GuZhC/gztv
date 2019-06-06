package com.dfsx.core.network;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

public class HttpParameters implements IHttpParameters {
    private HashMap<String, String> map;
    private Object tag;
    private JSONObject jsonParams;
    private String jsonBodyString;
    private HashMap<String, String> header;

    public HttpParameters() {
        map = new HashMap<String, String>();
        jsonBodyString = new JSONObject().toString();
    }

    public HttpParameters(JSONObject jsonObject) {
        map = new HashMap<String, String>();
        this.jsonParams = jsonObject;
        jsonBodyString = jsonParams.toString();
    }

    public HttpParameters(long body) {
        this(Long.toString(body));
    }

    public HttpParameters(int body) {
        this(Integer.toString(body));
    }

    public HttpParameters(boolean body) {
        this(Boolean.toString(body));
    }

    public HttpParameters(double body) {
        this(Double.toString(body));
    }

    public HttpParameters(String body) {
        jsonBodyString = "\"" + body + "\"";
        map = new HashMap<String, String>();
    }

    public static HttpParameters build() {
        return new HttpParameters();
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public HttpParameters setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public String fromMap() {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            try {
                sb.append(key).append("=").append(URLEncoder.encode(map.get(key), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                Log.e("encodeUrl", "encode to GBK is error!!!");
                e.printStackTrace();
                sb.append(key).append("=").append(map.get(key)).append("&");
            }
        }
        //        sb.append(AbsHttp.PARAM_VERSIONNAME).append("=").append(VersionConfig.getVersionName()).append("&");
        //        sb.append(AbsHttp.PARAM_VERSIONCODE).append("=").append(VersionConfig.getVersionCode()).append("&");
        //        sb.append(AbsHttp.PARAM_ISRELEASE).append("=").append(VersionConfig.isRelease()).append("&");
        //        sb.append(AbsHttp.PARAM_CLIENTTYPE).append("=").append(Constants.CLIENTTYPE);
        return sb.toString();
    }

    public void clear() {
        map.clear();
    }

    @Override
    public String fromJSON() {
        return jsonBodyString;
    }

    @Override
    public String fromGet() {
        StringBuilder sb = new StringBuilder();
        if (jsonParams != null) {
            Iterator iterator = jsonParams.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jsonParams.optString(key, "");
                try {
                    sb.append(key).append("=").append(URLEncoder.encode(value, "utf-8")).append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    sb.append(key).append("=").append(value).append("&");
                }
            }
        }
        if (map != null && map.size() > 0) {
            sb.append(fromMap());
        }
        String getString = sb.toString();
        if (getString.endsWith("&")) {
            getString = getString.substring(0, getString.length() - 1);
        }
        return getString;
    }

    @Override
    public HashMap<String, String> toMap() {
        //        map.put(AbsHttp.PARAM_VERSIONNAME, VersionConfig.getVersionName());
        //        map.put(AbsHttp.PARAM_VERSIONCODE, String.valueOf(VersionConfig.getVersionCode()));
        //        map.put(AbsHttp.PARAM_ISRELEASE, String.valueOf(VersionConfig.isRelease()));
        //        map.put(AbsHttp.PARAM_CLIENTTYPE, String.valueOf(Constants.CLIENTTYPE));
        return map;
    }

    public HttpParameters append(String key, String value) {
        put(key, value);
        return this;
    }

    public HttpParameters append(String key, int value) {
        put(key, value);
        return this;
    }

    public HttpParameters append(String key, long value) {
        put(key, value);
        return this;
    }

    public HttpParameters append(String key, double value) {
        put(key, value);
        return this;
    }

    public void put(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            map.put(key, value);
        }

    }

    public void put(String key, int value) {
        map.put(key, String.valueOf(value));
    }

    public void put(String key, long value) {
        map.put(key, String.valueOf(value));
    }

    public void put(String key, double value) {
        map.put(key, String.valueOf(value));
    }

    @Deprecated
    public void add(String key, String value) {
        append(key, value);
    }

    @Deprecated
    public void add(String key, int value) {
        append(key, value);
    }

    @Deprecated
    public void add(String key, long value) {
        append(key, value);
    }

    @Deprecated
    public void add(String key, double value) {
        append(key, value);
    }

    public void remove(String key) {
        map.remove(key);
    }


    public void setJsonParams(JSONObject jsonParams) {
        this.jsonParams = jsonParams;
        jsonBodyString = jsonParams.toString();
    }

    public HashMap<String, String> getHeader() {
        return header;
    }

    /**
     * 为调用设置请求的header
     *
     * @param header
     */
    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }
}

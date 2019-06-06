package com.dfsx.core.common.Util;

import android.text.TextUtils;
import com.dfsx.core.exception.ApiException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuwb on 2016/8/30.
 */
public class JsonCreater {

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

    public static String getErrorMsgFromApi(String error) {
        if (error != null && !TextUtils.isEmpty(error)) {
            int last = error.lastIndexOf("{");
            if (last != -1) {
                error = error.substring(last);
                try {
                    JSONObject ob = jsonParseString(error);
                    if (ob != null)
                        error = ob.optString("message");
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
        return error;
    }

//    public static String getErrorMsg(String error) {
//        if (error != null && !TextUtils.isEmpty(error)) {
//            int last = error.indexOf(":");
//            if (last != -1) {
//                error = error.substring(last);
//            }
//        }
//        return error;
//    }


    /**
     * HttpUtils.exeget  返回
     *
     * @param error
     * @return heyang
     */
    public static String getErrorMsg(String error) {
        String result = error;
        if (error != null && !TextUtils.isEmpty(error)) {
            int last = error.indexOf(":");
            if (last != -1) {
                result = error.substring(last + 1);
                String reg1 = ".*failed to connect.*";       //判断字符串中是否含有特定字符串ll
                String reg2 = ".*Unable to resolve host.*";  //判断字符串中是否含有特定字符串ll
                if (result.matches(reg1) || result.matches(reg2)) {
                    result = "连接超时";
                }
            }
        } else {
            result = "连接超时";
        }
        return result;
    }

    /**
     *  create  by heyang   检查error 并抛出异常
     * @param result
     * @throws ApiException
     */
    public static void checkThrowError(JSONObject result) throws ApiException {
        if (result != null) {
            if (result.has("error")) {
                throw new ApiException(result.optString("message"));
            }
        }
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
            //throw ApiException.exception(e);
        }
        return jsonObject;
    }
}

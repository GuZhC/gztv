package com.dfsx.core.network;

import android.text.TextUtils;
import android.util.Log;

import com.dfsx.core.AppApiManager;
import com.dfsx.core.BuildConstants;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by halim on 2015/4/3.
 */
public class JsonHelper {
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SO_TIMEOUT = 5000;
    private static final String TAG = "HTTPOP";


    public static JSONObject httpPostJson(String urlString, JSONObject jsonParams, String token) {
        return httpSendJson("POST", urlString, jsonParams, token);
    }

    public static JSONObject httpPostString(String urlString, String params, String token) throws ApiException {
        return httpSendStringWithException("POST", urlString, params, token);
    }

    public static JSONObject httpPutJson(String urlString, JSONObject jsonParams, String token) {
        return httpSendJson("PUT", urlString, jsonParams, token);
    }

    /**
     * httpPutJson()的不处理异常的版本
     *
     * @param urlString
     * @param jsonParams
     * @param token
     * @return
     * @throws ApiException
     */
    public static JSONObject httpPutJsonWithException(String urlString, JSONObject jsonParams, String token) throws ApiException {
        return httpSendJsonWithException("PUT", urlString, jsonParams, token);
    }

    //heyang   2015-4-14
    public static JSONObject httpDelJson(String urlString, String token) {
        return httpSendJson("DELETE", urlString, null, token);
    }

    public static JSONObject getError(int code, String error) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", code);
        jsonObject.put("message", error);
        return jsonObject;
    }

    public static JSONObject httpSendJson(String method, String urlString, JSONObject jsonParams, String token) {
        Log.e("http", "http url = " + getGetUrl(urlString, jsonParams));
        HttpURLConnection urlConn = null;
        JSONObject jsonObject = null;
        try {
            if (BuildConstants.DEBUG)
                Log.d(TAG, method + " " + urlString);
            URL url = new URL(urlString);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            if (!("DELETE").equals(method))
                urlConn.setDoOutput(true);
            urlConn.setRequestMethod(method);
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConn.setReadTimeout(SO_TIMEOUT);
//            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            //urlConn.setRequestProperty("Accept","*/*");
            urlConn.setRequestProperty("Content-Type", "application/json");
            if (token != null) {
                urlConn.setRequestProperty("X-CSRF-Token", token);
            }
            if (AppApiManager.getInstance().getAppApi() != null &&
                    !TextUtils.isEmpty(AppApiManager.getInstance().getAppApi().getSession())) {
                String sessionName = AppApiManager.getInstance().
                        getAppApi().getSessionName();
                String sessionId = AppApiManager.getInstance().
                        getAppApi().getSession();
                urlConn.setRequestProperty("Cookie", sessionName + "=" + sessionId);

            }

            if (jsonParams != null) {
                DataOutputStream printout;
                printout = new DataOutputStream(urlConn.getOutputStream());
                //printout.writeBytes(URLEncoder.encode(jsonParams.toString(), "UTF-8"));
                byte[] bytes = jsonParams.toString().getBytes("UTF-8");
                printout.write(bytes);
                //printout.writeBytes(jsonParams.toString());
                printout.flush();
                printout.close();
            }

            int code = urlConn.getResponseCode();
            InputStreamReader in = null;
            if (code == HttpStatus.SC_OK) {
                in = new InputStreamReader(urlConn.getInputStream());
            } else
                in = new InputStreamReader(urlConn.getErrorStream());

            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            StringBuffer sbuff = new StringBuffer();
            while (((inputLine = buffer.readLine()) != null)) {
                sbuff.append(inputLine);
            }
            in.close();
            if (code == HttpStatus.SC_OK ||
                    code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                jsonObject = jsonParseString(sbuff.toString());
            } else {
                jsonObject = getError(code, sbuff.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return jsonObject;
    }

    /**
     * httpSendJson()的抛出异常的版本
     *
     * @param method
     * @param urlString
     * @param jsonParams
     * @param token
     * @return
     * @throws ApiException
     */
    public static JSONObject httpSendJsonWithException(String method, String urlString,
                                                       JSONObject jsonParams, String token) throws ApiException {
        Log.e("http", "http url = " + getGetUrl(urlString, jsonParams));
        HttpURLConnection urlConn = null;
        JSONObject jsonObject = null;
        try {
            if (BuildConstants.DEBUG)
                Log.d(TAG, method + " " + urlString);
            URL url = new URL(urlString);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            if (!("DELETE").equals(method))
                urlConn.setDoOutput(true);
            urlConn.setRequestMethod(method);
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConn.setReadTimeout(SO_TIMEOUT);
//            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            //urlConn.setRequestProperty("Accept","*/*");
            urlConn.setRequestProperty("Content-Type", "application/json");
            if (token != null) {
                urlConn.setRequestProperty("X-CSRF-Token", token);
            }
            if (AppApiManager.getInstance().getAppApi() != null &&
                    !TextUtils.isEmpty(AppApiManager.getInstance().getAppApi().getSession())) {
                String sessionName = AppApiManager.getInstance().
                        getAppApi().getSessionName();
                String sessionId = AppApiManager.getInstance().
                        getAppApi().getSession();
                urlConn.setRequestProperty("Cookie", sessionName + "=" + sessionId);

            }

            if (jsonParams != null) {
                DataOutputStream printout;
                printout = new DataOutputStream(urlConn.getOutputStream());
                //printout.writeBytes(URLEncoder.encode(jsonParams.toString(), "UTF-8"));
                byte[] bytes = jsonParams.toString().getBytes("UTF-8");
                printout.write(bytes);
                //printout.writeBytes(jsonParams.toString());
                printout.flush();
                printout.close();
            }

            int code = urlConn.getResponseCode();
            InputStreamReader in = null;
            if (code == HttpStatus.SC_OK) {
                in = new InputStreamReader(urlConn.getInputStream());
            } else
                in = new InputStreamReader(urlConn.getErrorStream());

            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            StringBuffer sbuff = new StringBuffer();
            while (((inputLine = buffer.readLine()) != null)) {
                sbuff.append(inputLine);
            }
            in.close();
            if (code == HttpStatus.SC_OK ||
                    code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                jsonObject = jsonParseString(sbuff.toString());
            } else {
                jsonObject = getError(code, sbuff.toString());
            }


        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return jsonObject;
    }


    /**
     * 发出的是string 而不是 json
     *
     * @param method
     * @param urlString
     * @param params
     * @param token
     * @return
     * @throws ApiException
     */
    public static JSONObject httpSendStringWithException(String method, String urlString,
                                                         String params, String token) throws ApiException {

        HttpURLConnection urlConn = null;
        JSONObject jsonObject = null;
        try {
            if (BuildConstants.DEBUG)
                Log.d(TAG, method + " " + urlString);
            URL url = new URL(urlString);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            if (!("DELETE").equals(method))
                urlConn.setDoOutput(true);
            urlConn.setRequestMethod(method);
            urlConn.setUseCaches(false);
            urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConn.setReadTimeout(SO_TIMEOUT);
//            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            //urlConn.setRequestProperty("Accept","*/*");
            urlConn.setRequestProperty("Content-Type", "application/json");
            if (token != null) {
                urlConn.setRequestProperty("X-CSRF-Token", token);
            }
            if (AppApiManager.getInstance().getAppApi() != null &&
                    !TextUtils.isEmpty(AppApiManager.getInstance().getAppApi().getSession())) {
                String sessionName = AppApiManager.getInstance().
                        getAppApi().getSessionName();
                String sessionId = AppApiManager.getInstance().
                        getAppApi().getSession();
                urlConn.setRequestProperty("Cookie", sessionName + "=" + sessionId);

            }

            if (params != null) {
                DataOutputStream printout;
                printout = new DataOutputStream(urlConn.getOutputStream());
                //printout.writeBytes(URLEncoder.encode(jsonParams.toString(), "UTF-8"));
                byte[] bytes = params.getBytes("UTF-8");
                printout.write(bytes);
                //printout.writeBytes(jsonParams.toString());
                printout.flush();
                printout.close();
            }

            int code = urlConn.getResponseCode();
            InputStreamReader in = null;
            if (code == HttpStatus.SC_OK) {
                in = new InputStreamReader(urlConn.getInputStream());
            } else
                in = new InputStreamReader(urlConn.getErrorStream());

            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            StringBuffer sbuff = new StringBuffer();
            while (((inputLine = buffer.readLine()) != null)) {
                sbuff.append(inputLine);
            }
            in.close();
            if (code == HttpStatus.SC_OK ||
                    code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                jsonObject = jsonParseString(sbuff.toString());
            } else {
                jsonObject = getError(code, sbuff.toString());
            }


        } catch (Exception e) {
            Log.d("---------------------", e.getMessage());
            throw new ApiException(e.getMessage());
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return jsonObject;
    }


    public static JSONObject httpGetJson(String urlString, String token) throws ApiException {
        Log.e("http", "http url = " + urlString);
        JSONObject jsonObject = null;
        HttpURLConnection conn = null;
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
            if (token != null) {
                conn.setRequestProperty("X-CSRF-Token", token);
            }
            if (AppApiManager.getInstance().getAppApi() != null &&
                    !TextUtils.isEmpty(AppApiManager.getInstance().getAppApi().getSession())) {
                String sessionName = AppApiManager.getInstance().
                        getAppApi().getSessionName();
                String sessionId = AppApiManager.getInstance().
                        getAppApi().getSession();
                conn.setRequestProperty("Cookie", sessionName + "=" + sessionId);

            }
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

            if (code == HttpStatus.SC_OK ||
                    code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                jsonObject = jsonParseString(sbuff.toString());
            } else {
                jsonObject = getError(code, sbuff.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return jsonObject;
    }

    /*****************
     * 上传视频
     ******/
    public static JSONObject httpUpload(String urlServer, String pathFile, ProgessCallbacface cb) throws Exception {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        String urlpath = "";
        final int TIME_OUT = 10 * 1000;
        JSONObject result = null;
        // start
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 256 * 1024;// 256KB
        try {
            File uploadFile = new File(pathFile);
            FileInputStream fileInputStream = new FileInputStream(uploadFile);

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);

            // Set size of every block for post
            connection.setChunkedStreamingMode(256 * 1024);// 256KB

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"onefile\";filename=\""
                    + pathFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            int nCount = bytesAvailable / bufferSize + 1;

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            Log.e("JsonHelp", "upload-----------");
            int step = 0;
            int npre = -1;
            while (bytesRead > 0) {
                double pos = step * 90 / nCount;
                step++;
                String format = Double.toString(pos);
                int val = stringToInt(format);
                boolean ret = true;
                if (npre != val) {
                    if (cb != null)
                        ret = cb.MyObtainProgressValues(val == 0 ? 1 : val);
                    npre = val;
                }
                outputStream.write(buffer, 0, bufferSize);
//                Log.e("JsonHelp  httpUpload==============","step=="+step);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

//            Log.e("JsonHelp  httpUpload==============","2");

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            InputStream inputStream = connection.getInputStream();

//            Log.e("JsonHelp  httpUpload==============","5");

            result = JsonHelper.jsonParse(inputStream);
            // urlpath=result.getString("sharepath")+result.getString("name");

//            Log.e("JsonHelp  httpUpload==============","6");

            fileInputStream.close();

            outputStream.flush();

            outputStream.close();
            if (cb != null)
                cb.MyObtainProgressValues(90);
//            Log.d("JsonHelp  httpUpViewo======", serverResponseMessage + " code=" + serverResponseCode);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return result;
    }

    static public JSONObject jsonParse(InputStream inputStream) throws ApiException {

        StringBuffer responseBuffer = new StringBuffer();

        try {
            InputStreamReader reader = new InputStreamReader(inputStream, "utf-8");
            char[] buffer = new char[4096];

            while (true) {
                int count = reader.read(buffer);
                if (count < 0) break;
                responseBuffer.append(buffer, 0, count);
            }
        } catch (Exception e) {
            throw ApiException.exception(e);
        }

        return jsonParseString(responseBuffer.toString());
    }

//    static public JSONObject jsonParseString(String response) {
//
//        JSONObject jsonObject = null;
//        try {
//            response = response.trim();
//            if(response.startsWith("[")) {
//                jsonObject = new JSONObject();
//                jsonObject.put("result", new JSONArray(response));
//            }
//            else {
//                jsonObject = new JSONObject(response);
//            }
//        }
//        catch (JSONException e) {
//            //@ return empty.
//            jsonObject = new JSONObject();
//        }
//
//        return jsonObject;
//    }

    //heyang  2015-4/14
    static public JSONObject jsonParseString(String response) throws ApiException {

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
                    if (index == -1) {
                        int number = Integer.parseInt(response);
                        jsonObject = new JSONObject();
                        jsonObject.put("id", response);  //针对圈子返回id
                        return jsonObject;
                    }
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

    public static String getUndValue(JSONObject jsonObject) throws JSONException {

        return getUndString(jsonObject, "value");
    }

    public static String getUndString(JSONObject jsonObject, String key) throws JSONException {
        String result = "";
        JSONArray undArry = jsonObject.optJSONArray("und");
        if (undArry != null
                && undArry.length() > 0) {
            JSONObject item = (JSONObject) undArry.get(0);
            result = item.optString(key);
        }
        return result;
    }

    public static int stringToInt(String string) {
        String str = string.substring(0, string.indexOf("."));
        int intgeo = Integer.parseInt(str);
        return intgeo;
    }

    public static String getGetUrl(String urlString, JSONObject jsonParams) {
        String getUrl = urlString;
        if (jsonParams != null) {
            if (!urlString.endsWith("?")) {
                getUrl += "?";
            }
            Iterator iterator = jsonParams.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jsonParams.optString(key, "");
                getUrl += key + "=" + value + "&";
            }

            getUrl = getUrl.substring(0, getUrl.length() - 1);
        }

        return getUrl;
    }
}

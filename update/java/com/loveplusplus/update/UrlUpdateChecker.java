package com.loveplusplus.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class UrlUpdateChecker implements IApkUpdateChecker {
    private static final String TAG = "UrlUpdateChecker";

    private String url;

    private ApkCheckResult checkResult;

    private Context mContext;

    public UrlUpdateChecker(Context context, String url) {
        this.mContext = context;
        this.url = url;
    }

    public static AppVersion getAppVersionByurl(String apkUrl) {
        Log.d("TAG", "download app url == " + apkUrl);
        Pattern pattern = Pattern.compile("d=.*-([\\d\\.]+\\.apk)");
        Matcher matcher = pattern.matcher(apkUrl);
        AppVersion appVersion = new AppVersion("", 0);
        if (matcher.find()) {
            String versionStr = matcher.group(1);
            Log.e("TAG", "v apk == " + versionStr);
            if (!TextUtils.isEmpty(versionStr)) {
                String[] arr = versionStr.split("\\.");
                int count = arr.length - 1;
                String codeStr = "";
                while (count < arr.length && count >= 0) {
                    String num = arr[count];
                    try {
                        Integer code = Integer.valueOf(num);
                        codeStr = num;
                        appVersion.setVersionCode(code);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count--;
                }
                String versionName = versionStr.replaceAll("\\." + codeStr + "\\.", "");
                versionName = versionName.replace("apk", "");
                appVersion.setVersionName(versionName);
            }
        }
        return appVersion;
    }

    public static AppVersion getCurrentApkVersion(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        int versionCode = packageInfo.versionCode;
        String versionName = packageInfo.versionName;
        AppVersion curVersion = new AppVersion(versionName, versionCode);
        return curVersion;
    }

    private void parseJson(String json) {
        try {

            //            JSONArray result = new JSONArray(json);
            //            JSONObject obj = result.getJSONObject(0);
            if (json == null){
                return;
            }
            JSONObject obj = new JSONObject(json);
            String updateMessage = obj.optString(Constants.APK_UPDATE_CONTENT);
            String apkUrl = obj.optString(Constants.APK_DOWNLOAD_URL);
            int apkCode = obj.optInt(Constants.APK_VERSION_CODE);
            AppVersion netAppVersion = null;
            if (apkUrl != null && !apkUrl.isEmpty()) {
                netAppVersion = getAppVersionByurl(apkUrl);
            }
            String netVersion = "1.0.0";
            if (netAppVersion != null)
                netVersion = netAppVersion.getVersionName() + "-" + netAppVersion.getVersionCode();
            AppVersion curVersion = getCurrentApkVersion(mContext);
            if (netAppVersion != null && netAppVersion.isNewerVersion(curVersion)) {
                checkResult = new ApkCheckResult(true, apkUrl, updateMessage, netVersion);
            } else {
                checkResult = new ApkCheckResult(false, apkUrl, updateMessage, netVersion);
                //Toast.makeText(mContext, mContext.getString(R.string.app_no_new_update), Toast.LENGTH_SHORT).show();
            }

        } catch (PackageManager.NameNotFoundException ignored) {
        } catch (JSONException e) {
            Log.e(TAG, "parse json error", e);
        } catch (IllegalStateException e) {
            //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            Log.e(TAG, e.toString());
        }
    }

    protected String sendPost(String urlStr) {
        HttpURLConnection uRLConnection = null;
        InputStream is = null;
        BufferedReader buffer = null;
        String result = null;
        try {
            URL url = new URL(urlStr);
            uRLConnection = (HttpURLConnection) url.openConnection();
            //uRLConnection.setDoInput(true);
            //uRLConnection.setDoOutput(true);
            uRLConnection.setRequestMethod("GET");
            uRLConnection.setUseCaches(false);
            uRLConnection.setConnectTimeout(10 * 1000);
            uRLConnection.setReadTimeout(10 * 1000);
            //uRLConnection.setInstanceFollowRedirects(false);
            //uRLConnection.setRequestProperty("Connection", "Keep-Alive");
            //uRLConnection.setRequestProperty("Charset", "UTF-8");
            //uRLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            //uRLConnection.setRequestProperty("Content-Type", "application/json");

            uRLConnection.connect();

            if (uRLConnection.getResponseCode() == 200) {

                is = uRLConnection.getInputStream();

                String content_encode = uRLConnection.getContentEncoding();

                if (null != content_encode && !"".equals(content_encode) && content_encode.equals("gzip")) {
                    is = new GZIPInputStream(is);
                }

                buffer = new BufferedReader(new InputStreamReader(is));
                StringBuilder strBuilder = new StringBuilder();
                String line;
                while ((line = buffer.readLine()) != null) {
                    strBuilder.append(line);
                }
                result = strBuilder.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "http post error", e);
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }
        return result;
    }

    @Override
    public ApkCheckResult checkUpdate() {
        checkResult = new ApkCheckResult(false, null, null, "");
        String res = sendPost(url);
        parseJson(res);
        return checkResult;
    }
}

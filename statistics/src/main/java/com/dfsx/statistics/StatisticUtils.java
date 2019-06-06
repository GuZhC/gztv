package com.dfsx.statistics;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.tencent.stat.StatService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

public class StatisticUtils {

    public static boolean initAndStart(Context context) {
        try {
            // 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
            String appKey = context.getResources().getString(com.dfsx.statistics.R.string.app_key);
            StatService.startStatService(context, appKey,
                    com.tencent.stat.common.StatConstants.VERSION);

            return true;
        } catch (Exception e) {
            // MTA初始化失败
            e.printStackTrace();
            Log.e("StatService", "StatService init error ----- ");
        }

        return false;
    }

    /**
     * 用户IP统计
     */
    public static void onUserIPStatistic(Context context) {
        new StatisticIP(context).execute();
    }

    protected static class StatisticIP extends AsyncTask<Void, Void, String> {
        private Context context;

        public StatisticIP(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String ipAdrr = null;
            try {
                String[] ipInfo = getNetIpCityInfo("");
                ipAdrr = ipInfo[0];
                String city = ipInfo[1];
                Log.e("StatService", "city ---" + city);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ipAdrr;
        }

        private String[] getNetIpCityInfo(String foreignIPString) {
            try {
                String sGetAddrUrl = "http://ip-api.com/json/";
                String requestStr = sGetAddrUrl + foreignIPString;
                String res = getNetString(requestStr);
                JSONObject jsonObject = new JSONObject(res);
                String ip = jsonObject.optString("query");
                String city = jsonObject.optString("city");
                String[] strings = new String[2];
                strings[0] = ip;
                strings[1] = city;
                return strings;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getNetString(String requestStr) throws IOException {
            HttpURLConnection uRLConnection = null;
            InputStream is = null;
            BufferedReader buffer = null;
            String result = null;
            URL url = new URL(requestStr);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("GET");
            uRLConnection.setUseCaches(false);
            uRLConnection.setConnectTimeout(10 * 1000);
            uRLConnection.setReadTimeout(10 * 1000);
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
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s)) {
                Properties prop = new Properties();
                Log.e("StatService", "user ip == " + s);
                prop.setProperty("IP", s);
                StatService.trackCustomKVEvent(context, "UserIP", prop);
            }
        }
    }
}

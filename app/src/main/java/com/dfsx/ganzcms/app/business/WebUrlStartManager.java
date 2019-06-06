package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.push.IPushMessageClickEvent;
import com.dfsx.ganzcms.app.push.NotificationMessageStartManager;
import com.dfsx.ganzcms.app.push.OnMessageClickEvent;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Body;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Extension;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by liuwb on 2017/6/5.
 */
public class WebUrlStartManager {

    private static WebUrlStartManager instance = new WebUrlStartManager();

    private OnMessageClickEvent pageStart;
    private Message webStartMessage;

    private WebUrlStartManager() {

    }

    public static WebUrlStartManager getInstance() {
        return instance;
    }

    public void onReceiveIntent(Context context, Intent intent) {
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                startPageByUri(context, uri);
            }
        }
    }

    /**
     * 这是通过把链接转换成消息的方式来启动页面
     *
     * @param context
     * @param uri
     */
    private void startPageByUri(Context context, Uri uri) {
        Extension extension = createExtensionDataByUrl(uri);
        if (extension != null) {
            webStartMessage = new Message();
            webStartMessage.setExt(extension);
            startAct(context);
        }
    }

    private void startAct(Context context) {
        if (NotificationMessageStartManager.getInstance().isMainTabLive()) {
            startWebUrlAct(context);
        } else {
            Intent intent = new Intent(context, MainTabActivity.class);
            context.startActivity(intent);
        }
    }

    public void startWebUrlAct(Context context) {
        if (webStartMessage != null) {
            if (pageStart == null) {
                pageStart = new OnMessageClickEvent(context, new IPushMessageClickEvent
                        .DefaultPushMessageClickEvent());
            }
            pageStart.onMessageClick(webStartMessage);
            webStartMessage = null;
        }
    }

    private String getExtensionSource(Uri uri) {
        String source = "cms";
        if (uri != null) {
            String path = uri.toString();
            if (path.contains("portal/node?")) {
                source = "cms";
            } else if (path.contains("sns/node?")) {
                source = "community";
            } else if (path.contains("live/")) {
                source = "live";
            } else if (path.contains("i/vcard?")) {
                source = "user";
            }
            Log.e("TAG", "path == " + path);
            Log.e("TAG", "source == " + source);
        }
        return source;
    }

    private Extension createExtensionDataByUrl(Uri uri) {
        Set<String> parameterSet = uri.getQueryParameterNames();
        Iterator<String> setIt = parameterSet.iterator();
        try {
            JSONObject parameterJson = new JSONObject();
            while (setIt.hasNext()) {
                String key = setIt.next();
                String value = uri.getQueryParameter(key);
                if (!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value)) {
                    parameterJson.put(key, Long.parseLong(value));
                } else {
                    parameterJson.put(key, value);
                }
            }
            Log.e("TAG", "parameterJson == " + parameterJson.toString());
            Body body = new Gson().fromJson(parameterJson.toString(), Body.class);
            if (body == null) {
                return null;
            }
            String type = parameterJson.optString("type");
            String source = getExtensionSource(uri);
            if (TextUtils.equals("live", source)) {
                type = "show";
            }
            Extension ex = new Extension(source, type, body);
            return ex;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

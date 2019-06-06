package com.dfsx.push.aliyunpush;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.alibaba.sdk.android.push.AndroidPopupActivity;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public abstract class BasePopupPushActivity extends AndroidPopupActivity {

    static final String TAG = "BasePopupPushActivity";
    private Context context;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);

        /**
         * 如果两秒钟之类还没回调就自动关闭页面
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    /**
     * 实现通知打开回调方法，获取通知相关信息
     *
     * @param title   标题
     * @param summary 内容
     * @param extMap  额外参数
     */
    @Override
    protected void onSysNoticeOpened(String title, String summary, Map<String, String> extMap) {
        Log.d(TAG, "OnMiPushSysNoticeOpened, title: " + title + ", content: " + summary + ", extMap: " + extMap);
        try {
            if (AliyunPushManager.getInstance().getListener() != null) {
                AliyunPushManager.getInstance().getListener().onNotification(context, title, summary, extMap);
                try {
                    JSONObject json = new JSONObject(extMap);
                    AliyunPushManager.getInstance().getListener().onNotificationOpened(context, title, summary, json.toString());
                    if (!isAppRun()) {
                        startApp();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    /**
     * 当前APP是否打开
     * @return
     */
    public abstract boolean isAppAlive();

    public boolean isAppRun() {
        return isAppAlive();
    }

    private boolean startApp() {
        try {
            PackageManager packageManager = getPackageManager();
            String packname = getApplicationInfo().processName;
            Intent intent = packageManager.getLaunchIntentForPackage(packname);
            startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

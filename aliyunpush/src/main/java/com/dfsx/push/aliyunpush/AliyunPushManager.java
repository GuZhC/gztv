package com.dfsx.push.aliyunpush;

import android.content.Context;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;

/**
 * Created by wen on 2017/3/27.
 */

public class AliyunPushManager {

    private OnAliyunPushReceiveListener listener;

    private static AliyunPushManager instance = new AliyunPushManager();

    private CloudPushService pushService;

    public static AliyunPushManager getInstance() {
        return instance;
    }

    public OnAliyunPushReceiveListener getListener() {
        return listener;
    }

    public void setListener(OnAliyunPushReceiveListener listener) {
        this.listener = listener;
    }

    /**
     * run application
     */
    public void initAndRegister(Context context, CommonCallback callback) {
        PushServiceFactory.init(context);
        pushService = PushServiceFactory.getCloudPushService();
        pushService.register(context, new DefaultCommonCallback(callback));
    }

    public void regsiterThirdPush(Context context) {
        // 注册方法会自动判断是否支持小米系统推送，如不支持会跳过注册。
        MiPushRegister.register(context, context.getResources().getString(R.string.xiaomi_appid),
                context.getResources().getString(R.string.xiaomi_appkey));
        // 注册方法会自动判断是否支持华为系统推送，如不支持会跳过注册。
        HuaWeiRegister.register(context);
    }

    public String getDeviceId() {
        return pushService != null ? pushService.getDeviceId() : null;
    }

    public CloudPushService getPushService() {
        return pushService;
    }

    class DefaultCommonCallback implements CommonCallback {

        private CommonCallback callback;

        public DefaultCommonCallback(CommonCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(String s) {
            if (callback != null) {
                callback.onSuccess(s);
            }
        }

        @Override
        public void onFailed(String s, String s1) {
            if (callback != null) {
                callback.onFailed(s, s1);
            }
        }
    }
}

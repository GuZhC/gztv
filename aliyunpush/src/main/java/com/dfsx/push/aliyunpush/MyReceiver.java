package com.dfsx.push.aliyunpush;

import android.content.Context;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

/**
 * Created by wen on 2017/3/15.
 */

public class MyReceiver extends com.alibaba.sdk.android.push.MessageReceiver {

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        super.onMessage(context, cPushMessage);
        if (AliyunPushManager.getInstance().getListener() != null)
            AliyunPushManager.getInstance().getListener().onMessage(context, cPushMessage);
    }

    @Override
    protected void onNotification(Context context, String s, String s1, Map<String, String> map) {
        super.onNotification(context, s, s1, map);
        if (AliyunPushManager.getInstance().getListener() != null)
            AliyunPushManager.getInstance().getListener().onNotification(context, s, s1, map);
    }

    @Override
    protected void onNotificationOpened(Context context, String s, String s1, String s2) {
        super.onNotificationOpened(context, s, s1, s2);
        if (AliyunPushManager.getInstance().getListener() != null)
            AliyunPushManager.getInstance().getListener().onNotificationOpened(context, s, s1, s2);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String s, String s1, String s2) {
        super.onNotificationClickedWithNoAction(context, s, s1, s2);
        if (AliyunPushManager.getInstance().getListener() != null)
            AliyunPushManager.getInstance().getListener().onNotificationClickedWithNoAction(context, s, s1, s2);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String s, String s1, Map<String, String> map, int i, String s2, String s3) {
        super.onNotificationReceivedInApp(context, s, s1, map, i, s2, s3);
        if (AliyunPushManager.getInstance().getListener() != null)
            AliyunPushManager.getInstance().getListener().onNotificationReceivedInApp(context, s, s1, map, i, s2, s3);
    }

    @Override
    protected void onNotificationRemoved(Context context, String s) {
        super.onNotificationRemoved(context, s);
        if (AliyunPushManager.getInstance().getListener() != null)
            AliyunPushManager.getInstance().getListener().onNotificationRemoved(context, s);
    }
}

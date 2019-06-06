package com.dfsx.push.aliyunpush;


import android.content.Context;

import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

/**
 * Created by wen on 2017/3/23.
 */

public interface OnAliyunPushReceiveListener {

    /**
     * 用于接收服务端推送的消息。
     * 消息不会弹窗，而是回调该方法。
     *
     * @param message CPushMessage类型，可以获取消息Id、消息标题和内容。
     */
    void onMessage(Context context, CPushMessage message);

    /**
     * 客户端接收到通知后，回调该方法。
     * 可获取到并处理通知相关的参数。
     *
     * @param title    通知标题
     * @param summary  通知内容
     * @param extraMap 通知额外参数，包括部分系统自带参数：
     *                 _ALIYUN_NOTIFICATION_ID_(V2.3.5及以上):创建通知对应id
     *                 _ALIYUN_NOTIFICATION_PRIORITY_(V2.3.5及以上):创建通知对应id。默认不带，需要通过OpenApi设置
     */
    void onNotification(Context context, String title, String summary, Map<String, String> extraMap);


    /**
     * 打开通知时会回调该方法，通知打开上报由SDK自动完成。
     *
     * @param title    通知标题
     * @param summary  通知内容
     * @param extraMap 通知额外参数，包括部分系统自带参数：
     *                 _ALIYUN_NOTIFICATION_ID_(V2.3.5及以上):创建通知对应id
     *                 _ALIYUN_NOTIFICATION_PRIORITY_(V2.3.5及以上):创建通知对应id。默认不带，需要通过OpenApi设置
     */
    void onNotificationOpened(Context context, String title, String summary, String extraMap);

    /**
     * 打开无跳转逻辑(open=4)通知时回调该方法(v2.3.2及以上版本支持)，通知打开上报由SDK自动完成。
     *
     * @param title    通知标题
     * @param summary  通知内容
     * @param extraMap 通知额外参数，包括部分系统自带参数：
     *                 _ALIYUN_NOTIFICATION_ID_(V2.3.5及以上):创建通知对应id
     *                 _ALIYUN_NOTIFICATION_PRIORITY_(V2.3.5及以上):创建通知对应id。默认不带，需要通过OpenApi设置
     */
    void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap);


    /**
     * 当用户创建自定义通知样式，并且设置推送应用内到达不创建通知弹窗时调用该回调，且此时不调用onNotification回调(v2.3.3及以上版本支持)
     *
     * @param title        通知标题
     * @param summary      通知内容
     * @param extraMap     通知额外参数
     * @param openType     原本通知打开方式，1：打开APP；2：打开activity；3：打开URL；4：无跳转逻辑
     * @param openActivity 所要打开的activity的名称，仅当openType=2时有效，其余情况为null
     * @param openUrl      所要打开的URL，仅当openType=3时有效，其余情况为null
     */
    void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl);

    /**
     * 删除通知时回调该方法，通知删除上报由SDK自动完成。
     *
     * @param messageId 删除通知的Id
     */
    void onNotificationRemoved(Context context, String messageId);

}

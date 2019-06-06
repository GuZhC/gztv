package com.dfsx.ganzcms.app.push;

import com.dfsx.ganzcms.app.push.PushMeesageModel.Extension;

/**
 * Created by wen on 2017/3/27.
 */

public interface OnPushListener {
    /**
     * 当消息到来时回调此方法
     *
     * @param extension 传递过来的消息
     */
    void onMessageReceive(Extension extension);

    /**
     * 如果想要收到指定类型的消息，则在此设置过滤器.
     * 不设置过滤器时会收到所有类型的消息
     *
     * @return
     * @ref MessagePushManager.MessageFilter
     */
    MessagePushManager.MessageFilter getFilter();
}

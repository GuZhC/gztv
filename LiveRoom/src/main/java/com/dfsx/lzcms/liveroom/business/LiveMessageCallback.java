package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.LiveMessage;

/**
 * Created by liuwb on 2017/7/7.
 */
public interface LiveMessageCallback extends IRoomMessageType {

    /**
     * 收到直播消息
     * <p>
     * 这里是所有的消息类型
     *
     * @param liveMessage
     */
    void onLiveMessage(LiveMessage liveMessage);
}

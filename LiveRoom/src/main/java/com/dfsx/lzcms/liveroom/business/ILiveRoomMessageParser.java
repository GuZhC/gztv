package com.dfsx.lzcms.liveroom.business;

/**
 * Created by liuwb on 2017/7/4.
 */
public interface ILiveRoomMessageParser<M> {

    void parserMessage(String tag, M message);

    void setMessageTypeCallBack(IRoomMessageType roomMessageType);
}

package com.dfsx.lzcms.liveroom.util;

import com.dfsx.core.rx.RxBus;

/**
 * Created by liuwb on 2017/3/6.
 */
public class RXBusUtil {

    /**
     * 在关注信息发生变化的时候调用
     *
     * @param chanegeInfo
     */
    public static void sendConcernChangeMessage(ConcernChanegeInfo chanegeInfo) {
        RxBus.getInstance().post(chanegeInfo);
    }

    /**
     * 在关注信息发生变化的时候调用
     *
     * @param isAdd     是添加关注么
     * @param changeNum 改变的数量
     */
    public static void sendConcernChangeMessage(boolean isAdd, int changeNum) {
        sendConcernChangeMessage(new ConcernChanegeInfo(isAdd, changeNum));
    }
}

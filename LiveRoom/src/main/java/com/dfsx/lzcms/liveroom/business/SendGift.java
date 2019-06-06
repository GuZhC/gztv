package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.model.GiftResponseInfo;

/**
 * Created by liuwb on 2016/10/10.
 */
public interface SendGift {
    void sendGift(GiftModel gift, int num, String roomName, ICallBack<GiftResponseInfo> callBack);
}

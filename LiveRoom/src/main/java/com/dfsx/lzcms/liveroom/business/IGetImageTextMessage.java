package com.dfsx.lzcms.liveroom.business;

import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.model.ImageTextMessage;

import java.util.List;

/**
 * Created by liuwb on 2017/7/6.
 */
public interface IGetImageTextMessage {

    void getImageTextMessageList(long roomId, long beforeMessageId, int max, DataRequest.DataCallback<List<ImageTextMessage>> callback);
}

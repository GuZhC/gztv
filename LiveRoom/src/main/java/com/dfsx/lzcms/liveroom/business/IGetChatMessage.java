package com.dfsx.lzcms.liveroom.business;

import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.model.IChatData;

import java.util.List;

/**
 * Created by liuwb on 2017/7/7.
 */
public interface IGetChatMessage {

    void getLiveChatMessageList(long roomId, long beforeMessageId, int pageSize,
                                DataRequest.DataCallback<List<IChatData>> callback);
}

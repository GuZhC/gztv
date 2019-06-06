package com.dfsx.lzcms.liveroom.business;

import com.dfsx.core.network.datarequest.DataRequest;

public interface IGetLiveServiceIntroduce {
    void getLiveServiceIntroduce(long showId, DataRequest.DataCallback<String> callBack);
}

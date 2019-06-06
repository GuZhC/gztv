package com.dfsx.ganzcms.app.business;

import com.dfsx.ganzcms.app.model.IHostDetails;
import com.dfsx.lzcms.liveroom.business.ICallBack;

public interface IGetHostDetails {

    void getHostDetails(long contentId, ICallBack<IHostDetails> callBack);
}

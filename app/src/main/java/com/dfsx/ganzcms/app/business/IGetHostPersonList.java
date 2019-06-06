package com.dfsx.ganzcms.app.business;

import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.model.HostInfo;

import java.util.ArrayList;

public interface IGetHostPersonList {

    void getHostList(long hostColumnId, int page, int pageSize, DataRequest.DataCallback<ArrayList<HostInfo>> callback);
}

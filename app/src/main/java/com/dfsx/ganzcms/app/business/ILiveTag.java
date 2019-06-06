package com.dfsx.ganzcms.app.business;

import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.model.LiveTagInfo;

import java.util.ArrayList;

public interface ILiveTag {

    void getAllLiveTag(DataRequest.DataCallback<ArrayList<LiveTagInfo>> callback);
}

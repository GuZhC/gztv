package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.BackPlayIntentData;

/**
 * Created by liuwb on 2017/3/27.
 */
public interface IGetPlayBackInfo {

    String getPlayBackInfo(long playBackId);

    /**
     * 设置跳转的数据
     *
     * @param backPlayIntentData
     */
    void initBackPlayIntentData(long playBackId,
                                BackPlayIntentData backPlayIntentData,
                                ICallBack<BackPlayIntentData> callBack);
}

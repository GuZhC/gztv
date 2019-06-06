package com.dfsx.lzcms.liveroom.view;

import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.model.GiftModel;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/7/5.
 */
public interface GetGift {

    void getGifts(ICallBack<ArrayList<GiftModel>> giftList);
}

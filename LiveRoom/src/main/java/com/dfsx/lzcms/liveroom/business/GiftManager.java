package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import com.dfsx.lzcms.liveroom.model.GiftModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liuwb on 2016/11/4.
 */
public class GiftManager {

    private static GiftManager instance = new GiftManager();


    private HashMap<Long, GiftModel> giftHashMap = new HashMap<>();

    private GiftManager() {
    }

    public static GiftManager getInstance() {
        return instance;
    }

    public void getGiftById(Context context, final long giftId, final ICallBack<GiftModel> giftCallback) {
        if (giftHashMap.get(giftId) != null) {
            giftCallback.callBack(giftHashMap.get(giftId));
        } else {
            new GiftGetter(context)
                    .getAllGiftList(false, new ICallBack<ArrayList<GiftModel>>() {
                        @Override
                        public void callBack(ArrayList<GiftModel> data) {
                            toMap(data);
                            giftCallback.callBack(giftHashMap.get(giftId));
                        }
                    });
        }
    }

    public GiftModel getGiftByIdSync(Context context, long giftId) {
        GiftModel gm = giftHashMap.get(giftId);
        if (gm != null) {
            return gm;
        }
        ArrayList<GiftModel> giftModels = new GiftGetter(context).getAllGiftListSync();
        if (giftModels != null) {
            toMap(giftModels);
            return giftHashMap.get(giftId);
        }
        return null;
    }

    private void toMap(ArrayList<GiftModel> data) {
        if (data != null && !data.isEmpty()) {
            giftHashMap.clear();
            for (GiftModel gift : data) {
                giftHashMap.put(gift.getId(), gift);
            }
        }
    }
}

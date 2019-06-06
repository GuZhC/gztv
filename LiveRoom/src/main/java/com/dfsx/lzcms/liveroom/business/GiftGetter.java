package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.model.GiftCategory;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.GetGift;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/11/3.
 */
public class GiftGetter implements GetGift {

    public static final String GIFT_URL = AppManager.getInstance().getIApp().getHttpBaseUrl() +
            "/public/gifts";

    public static final String DIR = "all";
    public static final String GIFT_FILE_NAME = "com.dfsx.lscms_gift_list";
    private Context context;

    public GiftGetter(Context context) {
        this.context = context;
    }


    @Override
    public void getGifts(ICallBack<ArrayList<GiftModel>> giftCallback) {
        getAllGiftList(true, giftCallback);
    }

    public void getAllGiftList(boolean isCacheGift, final ICallBack<ArrayList<GiftModel>> giftCallback) {
        String url = GIFT_URL;
        DataRequest<ArrayList<GiftModel>> request = isCacheGift ? new DataFileCacheManager<ArrayList<GiftModel>>(context, DIR, GIFT_FILE_NAME) {

            @Override
            public ArrayList<GiftModel> jsonToBean(JSONObject json) {
                return parseJson(json);
            }
        }
                :
                new DataRequest<ArrayList<GiftModel>>(context) {
                    @Override
                    public ArrayList<GiftModel> jsonToBean(JSONObject json) {
                        return parseJson(json);
                    }
                };
        request.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .build(), false)
                .setCallback(new DataRequest.DataCallback<ArrayList<GiftModel>>() {
                    @Override
                    public void onSuccess(boolean isAppend, ArrayList<GiftModel> data) {
                        giftCallback.callBack(data);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        LogUtils.e("TAG", "get gift error == " + e.getMessage());
                        giftCallback.callBack(null);
                    }
                });
    }

    public ArrayList<GiftModel> getAllGiftListSync() {
        String res = HttpUtil.executeGet(GIFT_URL, new HttpParameters(), null);
        try {
            StringUtil.checkHttpResponseError(res);
            JSONObject json = JsonCreater.jsonParseString(res);

            return parseJson(json);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<GiftModel> parseJson(JSONObject json) {
        ArrayList<GiftModel> giftModelArrayList = new ArrayList<GiftModel>();
        Gson gson = new Gson();
        JSONArray array = json.optJSONArray("result");
        for (int i = 0; i < array.length(); i++) {
            JSONObject giftJson = array.optJSONObject(i);
            GiftModel gift = gson.fromJson(giftJson.toString(), GiftModel.class);
            giftModelArrayList.add(gift);
        }
        return giftModelArrayList;
    }

    public void getGiftCategories(final ICallBack<ArrayList<GiftCategory>> callBack) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/gift-categories";
        new DataRequest<ArrayList<GiftCategory>>(context) {

            @Override
            public ArrayList<GiftCategory> jsonToBean(JSONObject json) {
                ArrayList<GiftCategory> list = new ArrayList<GiftCategory>();
                JSONArray array = json.optJSONArray("result");
                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    GiftCategory gc = gson.fromJson(item.toString(), GiftCategory.class);
                    list.add(gc);
                }
                return list;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url).build(), false)
                .setCallback(new DataRequest.DataCallback<ArrayList<GiftCategory>>() {
                    @Override
                    public void onSuccess(boolean isAppend, ArrayList<GiftCategory> data) {
                        callBack.callBack(data);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        callBack.callBack(null);
                    }
                });
    }
}

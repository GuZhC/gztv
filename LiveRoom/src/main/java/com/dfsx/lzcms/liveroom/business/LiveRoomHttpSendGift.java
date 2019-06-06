package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.model.GiftResponseInfo;
import com.dfsx.lzcms.liveroom.util.AndroidUtil;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liuwb on 2017/7/4.
 */
public class LiveRoomHttpSendGift implements SendGift {
    private Context context;
    private long roomId;
    private String roomEnterId;

    public LiveRoomHttpSendGift(Context context, long roomId, String roomEnterId) {
        this.context = context;
        this.roomId = roomId;
        this.roomEnterId = roomEnterId;
    }

    @Override
    public void sendGift(final GiftModel gift, final int num, String roomName, final ICallBack<GiftResponseInfo> callBack) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + roomId + "/room/gift/send-gift";
        Observable.just(url)
                .observeOn(Schedulers.io())
                .map(new Func1<String, GiftResponseInfo>() {
                    @Override
                    public GiftResponseInfo call(String s) {
                        String phoneIp = AndroidUtil.getPhoneIp();
                        HttpParameters parameters = LSLiveUtils.getLiveHttpHeaderParam(roomEnterId);
                        JSONObject postJson = new JSONObject();
                        try {
                            postJson.put("from_ip", phoneIp);
                            postJson.put("gift_id", gift.getId());
                            postJson.put("gift_count", num);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        parameters.setJsonParams(postJson);
                        String res = HttpUtil.execute(s, parameters,
                                AppManager.getInstance().getIApp().getCurrentToken());
                        GiftResponseInfo responseInfo = new GiftResponseInfo();
                        try {
                            JSONObject resJson = new JSONObject(res);
                            if (resJson.has("total_coins")) {
                                double totalCoins = resJson.optDouble("total_coins");
                                responseInfo.setRoomOwerTotalCoin(totalCoins);
                                responseInfo.setSuccess(true);
                            } else {
                                int error = resJson.optInt("error");
                                String errorText = resJson.optString("message");
                                responseInfo.setSuccess(false);
                                responseInfo.setErrorCode(error);
                                responseInfo.setErrorMsg(errorText);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            responseInfo.setSuccess(false);
                            responseInfo.setErrorMsg("JSONException");
                        }
                        return responseInfo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GiftResponseInfo>() {
                    @Override
                    public void call(GiftResponseInfo responseInfo) {
                        if (callBack != null) {
                            callBack.callBack(responseInfo);
                        }
                    }
                });
    }
}

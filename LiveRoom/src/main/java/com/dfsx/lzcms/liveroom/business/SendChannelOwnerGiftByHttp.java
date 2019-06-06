package com.dfsx.lzcms.liveroom.business;

import android.text.TextUtils;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.IHttpResponseListener;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.model.GiftResponseInfo;
import com.dfsx.lzcms.liveroom.util.AndroidUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuwb on 2016/12/20.
 */
public class SendChannelOwnerGiftByHttp implements SendGift {
    private static final String baseLiveUrl = AppManager.getInstance().getIApp().getHttpBaseUrl();

    private String httpUrl;

    public SendChannelOwnerGiftByHttp(long channelId) {
        httpUrl = baseLiveUrl + "/public/channels/" + channelId + "/send-gifts";
    }

    @Override
    public void sendGift(GiftModel gift, int num, String roomName, final ICallBack<GiftResponseInfo> callBack) {
        JSONObject reqJson = new JSONObject();
        try {
            reqJson.put("from_ip", AndroidUtil.getPhoneIp());
            reqJson.put("gift_id", gift.getId());
            reqJson.put("gift_count", num);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtil.doPost(httpUrl, new HttpParameters(reqJson),
                AppManager.getInstance().getIApp().getCurrentToken(),
                new IHttpResponseListener() {
                    @Override
                    public void onComplete(Object tag, String response) {
                        if (callBack != null) {
                            callBack.callBack(new GiftResponseInfo(true));
                        }
                    }

                    @Override
                    public void onError(Object tag, ApiException e) {
                        if (callBack != null) {
                            GiftResponseInfo res = new GiftResponseInfo(false);
                            String errorStr = e.getMessage();
                            String[] errorArr = errorStr.split(":");
                            int errorCode = -1;
                            if (errorArr != null && errorArr.length > 0
                                    && TextUtils.isDigitsOnly(errorArr[0])) {
                                errorCode = Integer.valueOf(errorArr[0]);
                            }
                            res.setErrorCode(errorCode);
                            res.setErrorMsg(errorStr);
                            callBack.callBack(res);
                        }
                    }
                }
        );
    }
}

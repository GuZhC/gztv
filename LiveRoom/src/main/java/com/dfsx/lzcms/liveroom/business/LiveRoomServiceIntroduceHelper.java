package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import org.json.JSONObject;

public class LiveRoomServiceIntroduceHelper implements IGetLiveServiceIntroduce {

    private Context context;

    private String roomEnterId;

    public LiveRoomServiceIntroduceHelper(Context context, String roomEnterId) {
        this.context = context;
        this.roomEnterId = roomEnterId;
    }

    @Override
    public void getLiveServiceIntroduce(long showId, DataRequest.DataCallback<String> callBack) {
        String httpUrl = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/shows/" + showId + "/room/intro";

        new DataRequest<String>(context) {

            @Override
            public String jsonToBean(JSONObject json) {
                if (json != null) {
                    return json.optString("text");
                }
                return null;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(httpUrl)
                .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                .setRequestType(DataReuqestType.GET)
                .setHttpHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId))
                .build(), false)
                .setCallback(callBack);
    }
}

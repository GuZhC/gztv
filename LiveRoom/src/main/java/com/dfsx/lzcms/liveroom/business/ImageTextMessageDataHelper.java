package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.lzcms.liveroom.model.ImageTextMessage;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2017/7/6.
 */
public class ImageTextMessageDataHelper implements IGetImageTextMessage {

    private Context context;
    private String roomEnterId;

    public ImageTextMessageDataHelper(Context context, String roomEnterId) {
        this.context = context;
        this.roomEnterId = roomEnterId;
    }

    @Override
    public void getImageTextMessageList(long roomId,
                                        long beforeMessageId, int max, DataRequest.DataCallback<List<ImageTextMessage>> callback) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl()
                + "/public/shows/" + roomId + "/room/image-text/messages?\n" +
                "max=" + max + "&before=" + beforeMessageId;
        new DataRequest<List<ImageTextMessage>>(context) {

            @Override
            public List<ImageTextMessage> jsonToBean(JSONObject json) {
                ArrayList<ImageTextMessage> list = new ArrayList<ImageTextMessage>();
                if (json != null) {
                    JSONArray array = json.optJSONArray("result");
                    if (array != null) {
                        Gson g = new Gson();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.optJSONObject(i);
                            ImageTextMessage message = g.fromJson(item.toString(), ImageTextMessage.class);
                            list.add(message);
                        }
                    }

                }
                return list;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setHttpHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId))
                .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                .setRequestType(DataReuqestType.GET)
                .build(), beforeMessageId > 0)
                .setCallback(callback);
    }
}

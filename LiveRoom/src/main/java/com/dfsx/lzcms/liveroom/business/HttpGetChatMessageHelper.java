package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.lzcms.liveroom.model.GiftMessage;
import com.dfsx.lzcms.liveroom.model.IChatData;
import com.dfsx.lzcms.liveroom.model.LiveMessage;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.*;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.*;

/**
 * Created by liuwb on 2017/7/7.
 */
public class HttpGetChatMessageHelper implements IGetChatMessage {

    private Context context;
    private boolean isSortList;
    private String roomEnterId;

    public HttpGetChatMessageHelper(Context context, String roomEnterId) {
        this.context = context;
        isSortList = true;
        this.roomEnterId = roomEnterId;
    }

    public HttpGetChatMessageHelper(Context context, boolean isSort, String roomEnterId) {
        this.context = context;
        isSortList = isSort;
        this.roomEnterId = roomEnterId;
    }


    @Override
    public void getLiveChatMessageList(final long roomId, final long beforeMessageId, final int pageSize,
                                       final DataRequest.DataCallback<List<IChatData>> callback) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl()
                + "/public/shows/"
                + roomId +
                "/room/chat/messages?max="
                + pageSize +
                "&before=" + beforeMessageId;

        rx.Observable.just(url)
                .observeOn(Schedulers.io())
                .map(new Func1<String, List<IChatData>>() {
                    @Override
                    public List<IChatData> call(String httpUrl) {
                        HttpParameters parameters = new HttpParameters();
                        parameters.setHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId));
                        parameters.setTag(beforeMessageId > 0);
                        String res = HttpUtil.executeGet(httpUrl, parameters,
                                AppManager.getInstance().getIApp().getCurrentToken());
                        try {
                            JSONObject json = JsonCreater.jsonParseString(res);
                            LiveRoomJSONStringMessageParser parser = new LiveRoomJSONStringMessageParser();
                            if (json != null) {
                                JSONArray arr = json.optJSONArray("result");
                                if (arr != null && arr.length() > 0) {
                                    ArrayList<IChatData> list = new ArrayList<IChatData>();
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject item = arr.optJSONObject(i);
                                        LiveMessage liveMessage = parser.parserMessageData("", item.toString());
                                        if (liveMessage instanceof GiftMessage) {//礼物消息特殊处理。设置文本显示的文本
                                            boolean isOk = ((GiftMessage) liveMessage).initGiftContentTextSync(context);
                                            if (isOk) {
                                                list.add(liveMessage);
                                            }
                                        } else {
                                            list.add(liveMessage);
                                        }
                                    }
                                    if (isSortList) {
                                        sortChatList(list);
                                    }
                                    return (list);
                                }
                            }
                            return null;
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<IChatData>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null) {
                            callback.onFail(new ApiException(e));
                        }
                    }

                    @Override
                    public void onNext(List<IChatData> datas) {
                        if (callback != null) {
                            callback.onSuccess(beforeMessageId > 0, datas);
                        }
                    }
                });

    }

    private void sortChatList(ArrayList<IChatData> list) {
        Collections.sort(list, new Comparator<IChatData>() {
            @Override
            public int compare(IChatData lhs, IChatData rhs) {
                return (int) (lhs.getChatTime() - rhs.getChatTime());
            }
        });
    }
}

package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.network.datarequest.GetTokenManager;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.*;

/**
 * Created by liuwb on 2016/10/24.
 */
public class LiveChannelManager {

    protected Context context;

    private String baseLiveUrl;

    public LiveChannelManager(Context context) {
        this.context = context;
        baseLiveUrl = AppManager.getInstance().getIApp().getHttpBaseUrl();
    }

    public void getLiveServicePlayBackInfo(long roomId,
                                           DataRequest.DataCallback<LiveServicePlayBackInfo> callback) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/activity-shows/" + roomId + "/playback";
        new DataRequest<LiveServicePlayBackInfo>(context) {

            @Override
            public LiveServicePlayBackInfo jsonToBean(JSONObject json) {
                LiveServicePlayBackInfo info = new LiveServicePlayBackInfo();
                if (json != null) {
                    JSONArray array = json.optJSONArray("result");
                    if (array != null && array.length() > 0) {
                        ArrayList<LiveServiceMultilineVideoInfo> list = new ArrayList<LiveServiceMultilineVideoInfo>();
                        info.setMultilineVideoInfoList(list);
                        Gson gson = new Gson();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject item = array.optJSONObject(i);
                            LiveServiceMultilineVideoInfo videoInfo = gson.fromJson(item.toString(), LiveServiceMultilineVideoInfo.class);
                            list.add(videoInfo);
                        }
                    }
                }
                return info;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .build(), false)
                .setCallback(callback);
    }

    public void getLiveServiceDetailsInfo(long roomId,
                                          DataRequest.DataCallback<LiveServiceDetailsInfo> callback) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/activity-shows/" + roomId;

        new DataRequest<LiveServiceDetailsInfo>(context) {

            @Override
            public LiveServiceDetailsInfo jsonToBean(JSONObject json) {
                LiveServiceDetailsInfo info = new Gson().
                        fromJson(json.toString(), LiveServiceDetailsInfo.class);
                return info;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .build(), false)
                .setCallback(callback);
    }

    /**
     * 发送Http请求进入房间
     *
     * @param id
     * @param callback
     */
    public void enterRoom(String id, String password, final ICallBack<EnterRoomInfo> callback) {

        String url = AppManager.getInstance().getIApp().getHttpBaseUrl();

        url += "/public/shows/" + id +
                "/enter-room";
        final JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(password)) {
                jsonObject.put("password", password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Observable.just(url)
                .observeOn(Schedulers.io())
                .map(new Func1<String, EnterRoomInfo>() {
                    @Override
                    public EnterRoomInfo call(String httpUrl) {
                        String json = HttpUtil.execute(httpUrl, new HttpParameters(jsonObject),
                                AppManager.getInstance().getIApp().getCurrentToken());
                        LogUtils.d("http", "res == " + json);
                        String errorMessage = "";
                        int errorCode = 0;
                        try {
                            StringUtil.checkHttpResponseError(json);
                        } catch (ApiException e) {
                            e.printStackTrace();
                            errorCode = StringUtil.getHttpResponseErrorCode(json);
                            errorMessage = e.getMessage();
                        }
                        EnterRoomInfo serviceInfo = new Gson().fromJson(json, EnterRoomInfo.class);
                        if (AppManager.getInstance().getIApp().isLogin() && errorCode == 401) {
                            //说明当前登录状态失效,需要重新获取TOKEN
                            String token = GetTokenManager.getInstance().getIGetToken().getTokenSync();
                            String json1 = HttpUtil.execute(httpUrl, new HttpParameters(new JSONObject()),
                                    token);
                            try {
                                StringUtil.checkHttpResponseError(json1);
                            } catch (ApiException e) {
                                e.printStackTrace();
                                EnterRoomInfo enterRoomInfoException = new EnterRoomInfo();
                                enterRoomInfoException.setError(true);
                                enterRoomInfoException.setErrorMessage(e.getMessage());
                                return enterRoomInfoException;
                            }
                            EnterRoomInfo serviceInfo1 = new Gson().fromJson(json1, EnterRoomInfo.class);

                            return serviceInfo1;
                        } else if (errorCode != 0) {
                            serviceInfo = new EnterRoomInfo();
                            serviceInfo.setError(true);
                            serviceInfo.setErrorMessage(errorMessage);
                        }
                        return serviceInfo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EnterRoomInfo>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(EnterRoomInfo enterRoomInfo) {
                        if (callback != null) {
                            callback.callBack(enterRoomInfo);
                        }
                    }
                });

    }

    /**
     * 批量处理添加关注或取消关注
     *
     * @param isConcern
     * @param callback
     * @param userIds
     */
    public void handleConcernList(boolean isConcern, DataRequest.DataCallback<Boolean> callback,
                                  long... userIds) {

        String url = AppManager.getInstance().getIApp().getCommonHttpUrl() +
                "/public/users/current/follow/";
        for (long id : userIds) {
            url += id + ",";
        }
        if (url.endsWith(",")) {
            url = url.substring(0, url.length() - 1);
        }

        DataRequest<Boolean> request = new DataRequest<Boolean>(context) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        };
        request.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                .setBooleanParams(isConcern)
                .build(), false);
        request.setCallback(callback);

    }

    /**
     * 指定关注某个人
     *
     * @param userId
     * @param callback
     */
    public void addConcern(long userId, DataRequest.DataCallback<Boolean> callback) {
        handleConcernList(true, callback, userId);
    }

    /**
     * 取消关注某人
     *
     * @param userId
     * @param callback
     */
    public void removeConcern(long userId, DataRequest.DataCallback<Boolean> callback) {
        handleConcernList(false, callback, userId);
    }

    public void isConcern(final long userId, final DataRequest.DataCallback<Boolean> callback) {
        handleIsConcernList(new DataRequest.DataCallback<HashMap<Long, Boolean>>() {
                                @Override
                                public void onSuccess(boolean isAppend, HashMap<Long, Boolean> data) {
                                    boolean isConcern = data.get(userId) != null && data.get(userId);
                                    if (callback != null) {
                                        callback.onSuccess(isAppend, isConcern);
                                    }
                                }

                                @Override
                                public void onFail(ApiException e) {
                                    if (callback != null) {
                                        callback.onFail(e);
                                    }
                                }
                            }
                , userId);
    }

    public void handleIsConcernList(DataRequest.DataCallback<HashMap<Long, Boolean>> callback,
                                    long... userIds) {
        String url = AppManager.getInstance().getIApp().getCommonHttpUrl() +
                "/public/users/current/followed/";
        for (long id : userIds) {
            url += id + ",";
        }
        if (url.endsWith(",")) {
            url = url.substring(0, url.length() - 1);
        }

        DataRequest<HashMap<Long, Boolean>> request = new DataRequest<HashMap<Long, Boolean>>(context) {
            @Override
            public HashMap<Long, Boolean> jsonToBean(JSONObject json) {
                HashMap<Long, Boolean> map = new HashMap<>();
                Iterator<?> it = json.keys();
                while (it.hasNext()) {//遍历JSONObject
                    String key = (String) it.next();
                    JSONObject vJson = json.optJSONObject(key);
                    boolean value = vJson.optBoolean("followed");
                    map.put(Long.parseLong(key), value);
                }
                return map;
            }
        };
        request.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                .build(), false);
        request.setCallback(callback);
    }

    /**
     * 获取当前登录者的乐币信息
     *
     * @param callback
     */
    public void getMyMoneyInfo(DataRequest.DataCallback<UserMoneyInfo> callback) {
        String url = AppManager.getInstance().getIApp().getCommonHttpUrl() +
                "/public/users/current/account";

        new DataRequest<UserMoneyInfo>(context) {
            @Override
            public UserMoneyInfo jsonToBean(JSONObject json) {
                UserMoneyInfo info = new Gson().
                        fromJson(json.toString(), UserMoneyInfo.class);
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }

    public void getLiveRtmpUrl(long roomId, DataRequest.DataCallback<String> callback) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/users/current/channels/" + roomId + "/room-stream";
        new DataRequest<String>(context) {

            @Override
            public String jsonToBean(JSONObject json) {
                String rtmp = "";
                if (json != null) {
                    rtmp = json.optString("rtmp_url");
                }
                return rtmp;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                .build(), false)
                .setCallback(callback);

    }

    public void getBackPlayVideoInfo(long backPlayId,
                                     DataRequest.DataCallback<List<BackPlayVideoInfo>> callback) {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() +
                "/public/personal-shows/" + backPlayId + "/playback";
        new DataRequest<List<BackPlayVideoInfo>>(context) {

            @Override
            public List<BackPlayVideoInfo> jsonToBean(JSONObject json) {
                if (json == null) {
                    return null;
                }
                JSONArray arr = json.optJSONArray("result");
                if (arr == null) {
                    return null;
                }
                List<BackPlayVideoInfo> list = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.optJSONObject(i);
                    if (item != null) {
                        BackPlayVideoInfo videoInfo = gson.fromJson(item.toString(),
                                BackPlayVideoInfo.class);
                        list.add(videoInfo);
                    }
                }
                return list;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(callback);
    }

    public void getGuessRoomInfo(long showId, String roomEnterId,
                                 DataRequest.DataCallback<GuessRoomInfo> callback) {
        String url = baseLiveUrl + "/public/shows/" + showId + "/room/quiz";

        new DataRequest<GuessRoomInfo>(context) {

            @Override
            public GuessRoomInfo jsonToBean(JSONObject json) {
                GuessRoomInfo roomInfo = new Gson().fromJson(json.toString(), GuessRoomInfo.class);
                return roomInfo;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setHttpHeader(LSLiveUtils.getLiveHttpHeader(roomEnterId))
                        .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }

    /**
     * 获取竞猜的人列表
     *
     * @param showId
     * @param callback
     */
    public void getGuessRoomBetPersonList(long showId,
                                          DataRequest.DataCallback<List<BetPersonInfo>> callback) {
        String url = baseLiveUrl + "/public/quiz-shows/" + showId + "/bets";

        new DataRequest<List<BetPersonInfo>>(context) {

            @Override
            public List<BetPersonInfo> jsonToBean(JSONObject json) {
                List<BetPersonInfo> list = null;
                JSONArray arr = json.optJSONArray("result");
                if (arr != null) {
                    list = new ArrayList<>();
                    Gson gson = new Gson();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject item = arr.optJSONObject(i);
                        BetPersonInfo personInfo = gson.fromJson(item.toString(), BetPersonInfo.class);
                        list.add(personInfo);
                    }
                    Collections.sort(list);
                }
                return list;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .build(), false)
                .setCallback(callback);
    }

    public void getRoomContributionList(int max, long showId, DataRequest.DataCallback<ArrayList<ContributionInfo>> callback) {
        getRoomContributionList(max, showId, 0, 0, callback);
    }

    /**
     * @param max
     * @param showId
     * @param startTimeStamp 开始时间 单位是S 是linux 的timeStamp
     * @param stopTimeStamp  结束时间
     * @param callback
     */
    public void getRoomContributionList(int max, long showId,
                                        long startTimeStamp,
                                        long stopTimeStamp,
                                        DataRequest.DataCallback<ArrayList<ContributionInfo>> callback) {
        String url = baseLiveUrl + "/public/top-senders?max=" + max +
                "&show_id=" + showId;
        if (startTimeStamp != 0) {
            url += "&start=" + startTimeStamp;
        }
        if (stopTimeStamp != 0) {
            url += "&stop=" + stopTimeStamp;
        }
        new DataRequest<ArrayList<ContributionInfo>>(context) {

            @Override
            public ArrayList<ContributionInfo> jsonToBean(JSONObject json) {
                JSONArray array = json.optJSONArray("result");
                ArrayList<ContributionInfo> list = new ArrayList<>();
                if (array != null) {
                    Gson gson = new Gson();
                    for (int i = 0; i < array.length(); i++) {
                        ContributionInfo info = gson.fromJson(array.optJSONObject(i).toString(),
                                ContributionInfo.class);
                        list.add(info);
                    }
                }

                return list;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .build(), false)
                .setCallback(callback);
    }

    public void gerPersonalRoomInfo(boolean isCurrentuser, long roomId, DataRequest.DataCallback<LivePersonalRoomDetailsInfo> callback) {
        String apiStr = isCurrentuser ? "/public/users/current/personal-shows/" :
                "/public/personal-shows/";
        String url = baseLiveUrl + apiStr + roomId;
        new DataRequest<LivePersonalRoomDetailsInfo>(context) {

            @Override
            public LivePersonalRoomDetailsInfo jsonToBean(JSONObject json) {
                LivePersonalRoomDetailsInfo info = new Gson().fromJson(json.toString(), LivePersonalRoomDetailsInfo.class);
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(AppManager.getInstance().getIApp().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }

    public void getUserChannelInfo(long userId, DataRequest.DataCallback<ArrayList<ChannelInfo>> callback) {
        String url = baseLiveUrl + "/public/users/" + userId + "/channels";
        new DataRequest<ArrayList<ChannelInfo>>(context) {
            @Override
            public ArrayList<ChannelInfo> jsonToBean(JSONObject json) {
                ArrayList<ChannelInfo> list = null;
                if (json != null) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        list = new ArrayList<ChannelInfo>();
                        Gson gson = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject item = arr.optJSONObject(i);
                            ChannelInfo info = gson.fromJson(item.toString(), ChannelInfo.class);
                            list.add(info);
                        }
                    }
                }
                return list;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .build(), false)
                .setCallback(callback);
    }

}

package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.IHttpResponseListener;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 与我的数据相关的请求  跟直播频道相关方法
 * Created by liuwb on 2016/10/25.
 */
public class MyDataManager extends LiveChannelManager {

    private String baseLiveUrl;
    private String basePlatformUrl;
    private String baseCmsUrl;

    public MyDataManager(Context context) {
        super(context);
        baseLiveUrl = App.getInstance().getmSession().getLiveServerUrl();
        basePlatformUrl = App.getInstance().getmSession().getPortalServerUrl();
        baseCmsUrl = App.getInstance().getmSession().getContentcmsServerUrl();
    }

    public void getMyCMSPlatformInfo(DataRequest.DataCallback<UserCMSPlatformInfo> callback) {
        String url = baseCmsUrl + "/public/users/current";
        new DataRequest<UserCMSPlatformInfo>(context) {
            @Override
            public UserCMSPlatformInfo jsonToBean(JSONObject json) {
                if (json != null) {
                    UserCMSPlatformInfo info = new Gson().fromJson(json.toString(), UserCMSPlatformInfo.class);
                    return info;
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setRequestType(DataReuqestType.GET)
                        .setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }

    public void getMyDayTask(DataRequest.DataCallback<List<DayTask>> callback) {
        String url = basePlatformUrl + "/public/users/current/day-tasks";
        new DataRequest<List<DayTask>>(context) {

            @Override
            public List<DayTask> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray jsonArray = json.optJSONArray("result");
                    if (jsonArray != null) {
                        List<DayTask> list = new ArrayList<>();
                        Gson g = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            DayTask dayTask = g.fromJson(jsonArray.optJSONObject(i).toString(),
                                    DayTask.class);
                            list.add(dayTask);
                        }
                        return list;
                    }
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setRequestType(DataReuqestType.GET)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }

    public void qianDao(DataRequest.DataCallback<Boolean> callback) {
        String url = basePlatformUrl + "/public/users/current/sign-in";
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                RxBus.getInstance().post(new Intent(IntentUtil.ACTION_QIAN_DAO_SUCCESS));
                return true;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.POST)
                        .build(), false)
                .setCallback(callback);
    }

    public void isQianDao(DataRequest.DataCallback<Boolean> callback) {
        String url = basePlatformUrl + "/public/users/current/sign-in";
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                if (json != null) {
                    String res = json.optString("res");
                    try {
                        boolean is = Boolean.valueOf(res);
                        return is;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder().setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(callback);

    }

    /**
     * 获取我的收藏的数量
     *
     * @param callback
     */
    public void getMyStoreNum(DataRequest.DataCallback<Integer> callback) {
        String url = basePlatformUrl + "/public/users/current/favorites?page=1&size=1";
        getHttpResultTotalNum(callback, url);

    }

    public void getHttpResultTotalNum(DataRequest.DataCallback<Integer> callback, String url) {
        new DataRequest<Integer>(context) {

            @Override
            public Integer jsonToBean(JSONObject json) {
                int total = 0;
                if (json != null) {
                    total = json.optInt("total");
                }
                return total;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(App.getInstance().getCurrentToken())
                .build(), false)
                .setCallback(callback);
    }

    /**
     * 获取我关注的数量
     *
     * @param callback
     */
    public void getMyFollowNum(DataRequest.DataCallback<Integer> callback) {
        String url = basePlatformUrl + "/public/users/current/follows?page=1&size=1";
        getHttpResultTotalNum(callback, url);
    }

    /**
     * 获取我粉丝的数量
     *
     * @param callback
     */
    public void getMyFansNum(DataRequest.DataCallback<Integer> callback) {
        String url = basePlatformUrl + "/public/users/current/fans?page=1&size=1";
        getHttpResultTotalNum(callback, url);
    }

    /**
     * 创建个人的直播
     *
     * @param title           直播主题
     * @param category_key    分类key
     * @param cover_path      直播封面上传路径
     * @param introduction    直播简介
     * @param password        直播密码
     * @param isYugao         是否是发布直播预告
     * @param plan_start_time 计划开始时间
     * @param screen_mode     屏幕显示模式 1-横屏 2-竖屏
     * @param privacy         是否分享回放
     * @param callback
     */
    public void createPersonalShow(String title,
                                   String category_key,
                                   String cover_path,
                                   String introduction,
                                   String password,
                                   boolean isYugao,
                                   long plan_start_time,
                                   int screen_mode,
                                   boolean privacy,
                                   long[] tagIds,
                                   DataRequest.DataCallback<ShowRoomInfo> callback) {

        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/current/personal-shows";
        JSONObject object = new JSONObject();
        try {
            object.put("title", title);
            object.put("category_key", category_key);
            object.put("cover_path", cover_path);
            object.put("introduction", introduction);
            object.put("password", password);
            object.put("forenotice", isYugao);
            if (isYugao) {//是预告就设置开始时间
                object.put("plan_start_time", plan_start_time);
            }
            object.put("screen_mode", screen_mode);
            object.put("privacy", privacy);
            if (tagIds != null && tagIds.length > 0) {
                JSONArray tagArr = new JSONArray();
                object.put("tags", tagArr);
                for (long id : tagIds) {
                    tagArr.put(id);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new DataRequest<ShowRoomInfo>(context) {
            @Override
            public ShowRoomInfo jsonToBean(JSONObject json) {
                if (json != null) {
                    ShowRoomInfo info = new Gson().fromJson(json.toString(), ShowRoomInfo.class);
                    if (info.getId() >= 0 && !TextUtils.isEmpty(info.getRtmpUrl())) {
                        return info;
                    }
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setJsonParams(object)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }

    /**
     * 创建show的直播类型
     *
     * @param channnelId
     * @param subject
     * @param cover_path
     * @param introduction
     * @param password
     * @param callback
     */
    public void createShowChannel(long channnelId,
                                  String subject,
                                  String cover_path,
                                  String introduction,
                                  String password,
                                  DataRequest.DataCallback<ShowRoomInfo> callback) {

        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/current/channels/" + channnelId + "/shows";

        JSONObject object = new JSONObject();
        try {
            object.put("subject", subject);
            object.put("cover_path", cover_path);
            object.put("introduction", introduction);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new DataRequest<ShowRoomInfo>(context) {
            @Override
            public ShowRoomInfo jsonToBean(JSONObject json) {
                if (json != null) {
                    ShowRoomInfo info = new Gson().fromJson(json.toString(), ShowRoomInfo.class);
                    if (info.getId() >= 0 && !TextUtils.isEmpty(info.getRtmpUrl())) {
                        return info;
                    }
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setJsonParams(object)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);

    }

    public void getShowInfo(final long showId, DataRequest.DataCallback<ShowRoomInfo> callback) {
        String url = baseLiveUrl + "/public/users/current/shows/" + showId;
        new DataRequest<ShowRoomInfo>(context) {

            @Override
            public ShowRoomInfo jsonToBean(JSONObject json) {
                ShowRoomInfo info = new Gson().fromJson(json.toString(), ShowRoomInfo.class);
                info.setId(showId);
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);

    }

    public void updateShowInfo(long showId, String subject,
                               long cover_id, String cover_path,
                               String introduction, String password,
                               DataRequest.DataCallback<Boolean> callback) {
        String url = baseLiveUrl + "/public/users/current/shows/" + showId;
        JSONObject object = new JSONObject();
        try {
            object.put("subject", subject);
            object.put("cover_id", cover_id);
            if (!TextUtils.isEmpty(cover_path)) {
                object.put("cover_path", cover_path);
            }
            object.put("introduction", introduction);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setJsonParams(object)
                        .setToken(App.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.PUT)
                        .build(), false)
                .setCallback(callback);
    }

    /**
     * 获取我的直播
     *
     * @param callback
     */
    public void getMyLive(DataRequest.DataCallback<ArrayList<LiveRoomInfo>> callback) {
        String url = baseLiveUrl + "/public/users/current/lives";
        new DataRequest<ArrayList<LiveRoomInfo>>(context) {

            @Override
            public ArrayList<LiveRoomInfo> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray arr = json.optJSONArray("result");
                    if (arr != null) {
                        ArrayList<LiveRoomInfo> list = new ArrayList<LiveRoomInfo>();
                        Gson g = new Gson();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject item = arr.optJSONObject(i);
                            LiveRoomInfo roomInfo = g.fromJson(item.toString(), LiveRoomInfo.class);
                            list.add(roomInfo);
                        }
                        return list;
                    }
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);

    }

    /**
     * heyang  创建直播频道
     *
     * @param title
     * @param introduction
     * @param category_key
     * @param cover_path
     * @param callback
     */
    public void createMyChannel(String title, String introduction,
                                String category_key, String cover_path,
                                DataRequest.DataCallback<String> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/users/current/channels";
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("title", title);
            requestJson.put("introduction", introduction);
            requestJson.put("category_key", category_key);
            requestJson.put("cover_path", cover_path);
            new DataRequest<String>(context) {

                @Override
                public String jsonToBean(JSONObject json) {
                    return json.optString("id");
                }
            }.getData(new DataRequest.HttpParamsBuilder()
                    .setUrl(url)
                    .setJsonParams(requestJson)
                    .setToken(App.getInstance().getCurrentToken())
                    .build(), false)
                    .setCallback(callback);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFail(new ApiException(e));
        }
    }

    public void getMyLivePlatformInfo(DataRequest.DataCallback<UserLivePlatformInfo> callback) {
        String url = baseLiveUrl + "/public/users/current";
        new DataRequest<UserLivePlatformInfo>(context) {

            @Override
            public UserLivePlatformInfo jsonToBean(JSONObject json) {
                if (json != null) {
                    UserLivePlatformInfo info = new Gson().
                            fromJson(json.toString(), UserLivePlatformInfo.class);
                    return info;
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .setRequestType(DataReuqestType.GET)
                        .build(), false)
                .setCallback(callback);

    }

    public void getMyChannel(DataRequest.DataCallback<ArrayList<LiveRoomInfo>> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/users/current/channels";
        new DataRequest<ArrayList<LiveRoomInfo>>(context) {
            @Override
            public ArrayList<LiveRoomInfo> jsonToBean(JSONObject json) {
                JSONArray array = json.optJSONArray("result");
                ArrayList<LiveRoomInfo> list = new ArrayList<LiveRoomInfo>();
                if (array == null) {
                    return list;
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject j = array.optJSONObject(i);

                    LiveRoomInfo roomInfo = new Gson().
                            fromJson(j.toString(), LiveRoomInfo.class);
                    list.add(roomInfo);
                }
                return list;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(App.getInstance().getCurrentToken())
                .build(), false)
                .setCallback(callback);
    }

    public void deleteMyChannel(long channelId,
                                final DataRequest.DataCallback<Boolean> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl();
        url += "/public/users/current/channels/" + channelId;
        HttpUtil.doDel(url, new HttpParameters(new JSONObject()),
                App.getInstance().getCurrentToken(),
                new IHttpResponseListener() {
                    @Override
                    public void onComplete(Object tag, String response) {
                        Log.e("http", "delete channle == " + response);
                        if (callback != null) {
                            callback.onSuccess(false, true);
                        }
                    }

                    @Override
                    public void onError(Object tag, ApiException e) {
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }
                });
    }

    public void modifyMyChannel(long id,
                                String title, String introduction,
                                String category_key,
                                long cover_id,
                                String cover_path,
                                DataRequest.DataCallback<Boolean> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl();
        url += "/public/users/current/channels/" + id;
        JSONObject rJson = new JSONObject();
        try {
            rJson.put("title", title);
            rJson.put("introduction", introduction);
            rJson.put("category_key", category_key);
            rJson.put("cover_path", cover_path);
            new DataRequest<Boolean>(context) {

                @Override
                public Boolean jsonToBean(JSONObject json) {

                    return true;
                }
            }.getData(new DataRequest.HttpParamsBuilder()
                    .setUrl(url)
                    .setJsonParams(rJson)
                    .setToken(App.getInstance().getCurrentToken())
                    .build(), false)
                    .setCallback(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取我的回放
     *
     * @param page
     * @param size
     * @param callback
     */
    public void getMyBackPlay(int page, int size, DataRequest.DataCallback<PlayBackListInfo> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/current/playbacks?page=" + page + "&size=" + size;

        new DataRequest<PlayBackListInfo>(context) {

            @Override
            public PlayBackListInfo jsonToBean(JSONObject json) {
                PlayBackListInfo playModel = new Gson().
                        fromJson(json.toString(), PlayBackListInfo.class);
                return playModel;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(App.getInstance().getCurrentToken())
                .build(), page > 1)
                .setCallback(callback);
    }

    public void deleteMyBackPlay(long playback_id,
                                 final DataRequest.DataCallback<Boolean> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/current/shows/" + playback_id;

        HttpUtil.doDel(url, new HttpParameters(),
                App.getInstance().getCurrentToken(),
                new IHttpResponseListener() {
                    @Override
                    public void onComplete(Object tag, String response) {
                        if (callback != null) {
                            callback.onSuccess(false, true);
                        }
                    }

                    @Override
                    public void onError(Object tag, ApiException e) {
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }
                });
    }


    /**
     * @param pager    从1开始
     * @param size
     * @param type     <交易类型：0 – 不限, 1 – 收入, 2 – 支出, 3 – 支付>
     * @param callback
     */
    public void getMyTradeRecordList(int pager,
                                     int size, int type,
                                     DataRequest.DataCallback<TradeRecords> callback) {
        String url = App.getInstance().getmSession().getPortalServerUrl() +
                "/public/users/current/trade-records?page=" +
                pager + "&size=" + size + "&type=" + type;
        new DataRequest<TradeRecords>(context) {

            @Override
            public TradeRecords jsonToBean(JSONObject json) {
                TradeRecords records = new Gson().
                        fromJson(json.toString(), TradeRecords.class);
                return records;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setTagView(type)
                .setToken(App.getInstance().getCurrentToken())
                .build(), pager > 1)
                .setCallback(callback);

    }

    /**
     * 获取我的交易记录详情
     *
     * @param record_sn 交易流水号
     * @param callback
     */
    public void getMyTradeDetails(String record_sn,
                                  DataRequest.DataCallback<TradeDetails> callback) {
        String url = App.getInstance().getmSession().getPortalServerUrl() +
                "/public/users/current/trade-records/" + record_sn;

        new DataRequest<TradeDetails>(context) {

            @Override
            public TradeDetails jsonToBean(JSONObject json) {
                TradeDetails details = new Gson().
                        fromJson(json.toString(), TradeDetails.class);
                return details;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setToken(App.getInstance().getCurrentToken())
                .build(), false)
                .setCallback(callback);
    }

    public void updateLiveRoomSubjectInfo(long roomId, String newCoverImage,
                                          LiveSettingInfo liveSettings,
                                          DataRequest.DataCallback<Boolean> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/current/channels/" + roomId + "/room";
        JSONObject json = new JSONObject();
        try {
            json.put("subject", liveSettings.getSubject());
            if (TextUtils.isEmpty(newCoverImage)) {
                json.put("cover_id", liveSettings.getCoverId());
            } else {
                json.put("cover_path", newCoverImage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setJsonParams(json)
                        .setRequestType(DataReuqestType.PUT)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }


    /**
     * 设置我的回放是否是私有的
     *
     * @param backPlayId
     * @param isPrivacy  true私有，不公开
     * @param callback
     */
    public void setMyBackPlayPrivacy(long showId, boolean isPrivacy,
                                     DataRequest.DataCallback<Boolean> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/current/shows/" + showId + "/privacy";
        new DataRequest<Boolean>(context) {
            @Override
            public Boolean jsonToBean(JSONObject json) {
                return true;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setBooleanParams(isPrivacy)
                        .setRequestType(DataReuqestType.POST)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), false)
                .setCallback(callback);
    }
}

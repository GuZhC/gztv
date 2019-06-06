package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.common.Util.JsonCreater;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2016/10/24.
 */
public class ChannelManager extends LiveChannelManager {

    public static final int MAX_COUNT = 1000;
    public static final String HOTEST = "/public/lives/hotest?";

    public static final String ACCOUNT_DIR = "all";
    public static final String FILE_LIVE_CHANNEL = "com.dfsx.lscms.app_main_FILE_LIVE_CHANNEL.txt";

    private CategoryManager categoryManager;

    public ChannelManager(Context context) {
        super(context);
        categoryManager = new CategoryManager(context);
    }

    public void livePasswordNoNeed(long showId, DataRequest.DataCallback<Boolean> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/shows/" + showId + "/can-enter-privacy-room";
        new DataRequest<Boolean>(context) {

            @Override
            public Boolean jsonToBean(JSONObject json) {
                if (json != null) {
                    String res = json.optString("res");
                    return TextUtils.equals("true", res);
                }
                return false;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setRequestType(DataReuqestType.POST)
                .setToken(App.getInstance().getCurrentToken())
                .build(), false)
                .setCallback(callback);
    }

    public PlayBackListInfo searchPlayBackListSync(String keywords, int page, int size) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/playbacks/search?keywords=" + keywords +
                "&page=" + page +
                "&size=" + size;

        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
        PlayBackListInfo info = null;
        try {
            StringUtil.checkHttpResponseError(res);
            info = new Gson().fromJson(res, PlayBackListInfo.class);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return info;
    }

    /**
     * @param keywords
     * @param page
     * @param size
     * @param type     直播类型：0 - 不限，1 – 个人，2 – 活动
     * @param state    0 - 不限，1 - 未开始，2 – 正在直播，3 – 直播结束
     */
    public void searchLiveList(String keywords, int page, int size, int type,
                               int state, DataRequest.DataCallback<SearchLiveData> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/shows/search?keywords=" + keywords +
                "&page=" + page +
                "&size=" + size +
                "&type=" + type +
                "&state=" + state;
        new DataRequest<SearchLiveData>(context) {

            @Override
            public SearchLiveData jsonToBean(JSONObject json) {
                SearchLiveData info = new Gson().fromJson(json.toString(), SearchLiveData.class);
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .build(), page > 1)
                .setCallback(callback);
    }

    /**
     * @param keywords
     * @param page
     * @param size
     * @param type     直播类型：0 - 不限，1 – 个人，2 – 活动
     * @param state    0 - 不限，1 - 未开始，2 – 正在直播，3 – 直播结束
     */
    public SearchLiveData searchLiveListSync(String keywords, int page, int size, int type,
                                             int state) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/shows/search?keywords=" + keywords +
                "&page=" + page +
                "&size=" + size +
                "&type=" + type +
                "&state=" + state;
        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
        SearchLiveData info = new Gson().fromJson(res, SearchLiveData.class);
        return info;
    }

    /**
     * @param keywords
     * @param page     从1开始
     * @param size
     * @param callback
     */
    public void searchPlayBackList(String keywords, int page, int size,
                                   DataRequest.DataCallback<PlayBackListInfo> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/playbacks/search?keywords=" + keywords +
                "&page=" + page +
                "&size=" + size;
        new DataRequest<PlayBackListInfo>(context) {

            @Override
            public PlayBackListInfo jsonToBean(JSONObject json) {
                PlayBackListInfo info = new Gson().fromJson(json.toString(), PlayBackListInfo.class);
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .build(), page > 1)
                .setCallback(callback);
    }

    public void getPlayBackInfo(long playBackId, DataRequest.DataCallback<PlayBackInfo> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/playbacks/" + playBackId;
        new DataRequest<PlayBackInfo>(context) {

            @Override
            public PlayBackInfo jsonToBean(JSONObject json) {
                if (json != null) {
                    PlayBackInfo playBackInfo = new Gson().fromJson(json.toString(), PlayBackInfo.class);
                    return playBackInfo;
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                                .setUrl(url)
                                .setRequestType(DataReuqestType.GET)
                                .build()
                        , false)
                .setCallback(callback);
    }

    /**
     * 获取热门频道Id
     *
     * @param max
     * @param isAdd
     * @param callback
     */
    public void getHotestChannelIds(int max, boolean isAdd, DataRequest.DataCallback<ArrayList<String>> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + HOTEST + "max=" + max;
        new DataRequest<ArrayList<String>>(context) {

            @Override
            public ArrayList<String> jsonToBean(JSONObject json) {
                JSONArray array = json.optJSONArray("result");
                ArrayList<String> ids = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    ids.add(array.optString(i));
                }
                return ids;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .build(),
                isAdd)
                .setCallback(callback);
    }

    public void getCategoriesChannelIds(String categoryKey, int max, boolean isAdd,
                                        DataRequest.DataCallback<ArrayList<String>> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/categories/" + categoryKey +
                "/lives?" + "max=" + max;

        new DataRequest<ArrayList<String>>(context) {

            @Override
            public ArrayList<String> jsonToBean(JSONObject json) {
                JSONArray array = json.optJSONArray("result");
                ArrayList<String> ids = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    ids.add(array.optString(i));
                }
                return ids;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .build(),
                isAdd)
                .setCallback(callback);
    }

    public ArrayList<String> getCategoriesChannelIdsSync(String categoryKey, int max) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/categories/" + categoryKey +
                "/shows?" + "max=" + max;
        String response = HttpUtil.executeGet(url, new HttpParameters(), null);
        JSONObject json = null;
        try {
            json = JsonCreater.jsonParseString(response);
            StringUtil.checkHttpResponseError(response);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        JSONArray array = json.optJSONArray("result");
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            ids.add(array.optString(i));
        }
        return ids;
    }

    public void searchChannel(String keywords, int page, int pageSize, boolean isAdd,
                              DataRequest.DataCallback<SearchChannelData> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/lives/search?" +
                "keywords=" + keywords +
                "&page=" + page +
                "&size=" + pageSize;

        ChannelNoCacheRequest request = new ChannelNoCacheRequest(context);
        request.getData(new DataRequest.HttpParamsBuilder().setUrl(url).build(), isAdd)
                .setCallback(callback);
    }

    public void getChannelDetails(String id, DataRequest.DataCallback<LiveRoomInfo> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/lives/" + id;

        new DataRequest<LiveRoomInfo>(context) {

            @Override
            public LiveRoomInfo jsonToBean(JSONObject json) {
                return null;
            }
        }.getData(new DataRequest.HttpParamsBuilder().setUrl(url).build(), false)
                .setCallback(callback);
    }

    public String getChannelDetailsSync(String id) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/channels/" + id;
        return HttpUtil.executeGet(url, new HttpParameters(), null);
    }

    public void getChannelDetailsList(DataRequest.DataCallback<ArrayList<LiveRoomInfo>> callback,
                                      String... ids) {
        String url = App.getInstance().getmSession().getLiveServerUrl() + "/public/lives/";

        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            if (i == 0) {
                url += id;
            } else {
                url += "," + id;
            }
        }

        new ChannelNoCacheRequest(context).
                getData(new DataRequest.HttpParamsBuilder().
                        setUrl(url).build(), false).
                setCallback(callback);
    }

    /**
     * 获取所有的直播房间
     *
     * @param observer
     */
    public void getAllLiveChannelList(final Observer<List<LiveRoomInfo>> observer) {
        categoryManager.getAllCategoryPermissions(new Observer<List<Category>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onNext(List<Category> categories) {
                Observable.from(categories)
                        .subscribeOn(Schedulers.io())
                        .flatMap(new Func1<Category, Observable<CategoryMap>>() {
                            @Override
                            public Observable<CategoryMap> call(Category category) {
                                ArrayList<String> channelIds = getCategoriesChannelIdsSync(category.getKey(),
                                        MAX_COUNT);
                                ArrayList<CategoryMap> cmids = new ArrayList<CategoryMap>();
                                for (String s : channelIds) {
                                    cmids.add(new CategoryMap(category, s));
                                }
                                return Observable.from(cmids);
                            }
                        })
                        .observeOn(Schedulers.io())
                        .flatMap(new Func1<CategoryMap, Observable<LiveRoomInfo>>() {
                            @Override
                            public Observable<LiveRoomInfo> call(CategoryMap cm) {
                                String channelStr = getChannelDetailsSync(cm.channelId);
                                try {
                                    StringUtil.checkHttpResponseError(channelStr);
                                    JSONObject json = JsonCreater.jsonParseString(channelStr);
                                    Gson gson = new Gson();
                                    LiveRoomInfo roomInfo = gson.fromJson(json.toString(),
                                            LiveRoomInfo.class);
                                    roomInfo.setCategory(cm.category);
                                    return Observable.just(roomInfo);
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                }

                                return Observable.just(null);
                            }
                        })
                        .observeOn(Schedulers.io())
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
            }
        });

    }


    public void cacheRequest(String type, DataRequest.HttpParams httpParams,
                             boolean isAdd, DataRequest.DataCallback callback) {
        ChannelCacheRequest cacheRequest =
                new ChannelCacheRequest(context, type);
        cacheRequest.getData(httpParams,
                isAdd);

        cacheRequest.setCallback(callback);
    }

    public class ChannelCacheRequest extends DataFileCacheManager<ArrayList<LiveRoomInfo>> {

        public static final String DIR = "all";

        public static final String NAME = "Channel_list_";

        public ChannelCacheRequest(Context context, String type) {
            super(context, DIR, NAME + type);
        }

        @Override
        public ArrayList<LiveRoomInfo> jsonToBean(JSONObject json) {
            return null;
        }
    }

    public class ChannelNoCacheRequest extends DataRequest<SearchChannelData> {

        /**
         * @param context
         */
        public ChannelNoCacheRequest(Context context) {
            super(context);
        }

        @Override
        public SearchChannelData jsonToBean(JSONObject json) {
            SearchChannelData data = new Gson().fromJson(json.toString(), SearchChannelData.class);
            return data;
        }
    }

    /**
     * 分类和房间的Id号对应起来
     */
    protected class CategoryMap {
        Category category;
        String channelId;

        public CategoryMap(Category c, String id) {
            this.category = c;
            this.channelId = id;
        }
    }


    /**
     * 获取用户的频道
     * 包含我的
     *
     * @param userId
     */
    public void getUserLiveRoom(long userId, DataRequest.DataCallback<ArrayList<LiveRoomInfo>> callback) {
        boolean isMe = App.getInstance().getUser() != null &&
                App.getInstance().getUser().getUser() != null &&
                App.getInstance().getUser().getUser().getId() == userId;
        String urlKey = isMe ? "current" : userId + "";
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/" + urlKey +
                "/lives";
        new DataRequest<ArrayList<LiveRoomInfo>>(context) {

            @Override
            public ArrayList<LiveRoomInfo> jsonToBean(JSONObject json) {
                JSONArray array = json.optJSONArray("result");
                Gson g = new Gson();
                ArrayList<LiveRoomInfo> list = new ArrayList<LiveRoomInfo>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    LiveRoomInfo roomInfo = g.fromJson(item.toString(),
                            LiveRoomInfo.class);
                    list.add(roomInfo);
                }
                return list;
            }
        }.getData(new DataRequest.
                HttpParamsBuilder()
                .setUrl(url)
                .setToken(App.getInstance().getCurrentToken())
                .setRequestType(DataReuqestType.GET)
                .build(), false)
                .setCallback(callback);
    }

    /**
     * 用户的回放列表数据
     * 包含了当前登录的人（我的）
     *
     * @param userId
     * @param page
     * @param size
     * @param callback
     */
    public void getUserBackPlay(long userId, int page, int size,
                                DataRequest.DataCallback<PlayBackListInfo> callback) {
        boolean isMe = App.getInstance().getUser() != null &&
                App.getInstance().getUser().getUser() != null &&
                App.getInstance().getUser().getUser().getId() == userId;
        String urlKey = isMe ? "current" : userId + "";
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/" + urlKey +
                "/playbacks?page=" + page + "&size=" + size;

        new DataRequest<PlayBackListInfo>(context) {

            @Override
            public PlayBackListInfo jsonToBean(JSONObject json) {
                PlayBackListInfo info = new Gson().fromJson(json.toString(), PlayBackListInfo.class);
                return info;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setRequestType(DataReuqestType.GET)
                .setToken(App.getInstance().getCurrentToken())
                .build(), page > 1)
                .setCallback(callback);

    }

    /**
     * 用户的回放列表数据
     * 包含了当前登录的人（我的）
     *
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public PlayBackListInfo getUserBackPlaySync(long userId, int page, int size) {
        boolean isMe = App.getInstance().getUser() != null &&
                App.getInstance().getUser().getUser() != null &&
                App.getInstance().getUser().getUser().getId() == userId;
        String urlKey = isMe ? "current" : userId + "";
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/users/" + urlKey +
                "/playbacks?page=" + page + "&size=" + size;
        String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        if (!TextUtils.isEmpty(res)) {
            PlayBackListInfo info = new Gson().fromJson(res, PlayBackListInfo.class);
            return info;
        }

        return null;
    }

    /**
     * 获取直播页面的直播列表
     *
     * @param keywords
     * @param page
     * @param pageSize
     * @param isAdd
     * @param callback
     */
    public void getLiveChannelDetailsList(String keywords, int page,
                                          int pageSize, boolean isAdd,
                                          final DataRequest.DataCallback<List<LiveRoomInfo>> callback) {
        if (!isAdd) {
            //读缓存
            Observable.just(null)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<Object, List<LiveRoomInfo>>() {
                        @Override
                        public List<LiveRoomInfo> call(Object o) {
                            List<LiveRoomInfo> channelList = (List<LiveRoomInfo>) FileUtil.
                                    getFileByAccountId(context, FILE_LIVE_CHANNEL, ACCOUNT_DIR);
                            return channelList;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<LiveRoomInfo>>() {
                        @Override
                        public void call(List<LiveRoomInfo> liveRoomInfos) {
                            if (liveRoomInfos != null) {
                                callback.onSuccess(false, liveRoomInfos);
                            }
                        }
                    });
        }
        searchChannel(keywords, page, pageSize, isAdd,
                new DataRequest.DataCallback<SearchChannelData>() {
                    @Override
                    public void onSuccess(final boolean isAppend, SearchChannelData searchChannelData) {
                        Observable.just(searchChannelData)
                                .subscribeOn(Schedulers.io())
                                .flatMap(new Func1<SearchChannelData, Observable<LiveRoomInfo>>() {
                                    @Override
                                    public Observable<LiveRoomInfo> call(SearchChannelData searchChannelData) {
                                        if (searchChannelData != null) {
                                            return Observable.from(searchChannelData.getRoomInfoList());
                                        }
                                        return Observable.just(null);
                                    }
                                })
                                .observeOn(Schedulers.io())
                                .map(new Func1<LiveRoomInfo, LiveRoomInfo>() {
                                    @Override
                                    public LiveRoomInfo call(LiveRoomInfo liveRoomInfo) {
                                        //请求权限
                                        if (liveRoomInfo != null) {
                                            CategoryPermission cp = CategoryQuery.getQuery(context).
                                                    queryPermissionSync(liveRoomInfo.getCategoryKey());
                                            liveRoomInfo.setCategoryPermission(cp);
                                        }
                                        return liveRoomInfo;
                                    }
                                })
                                .toList()
                                .map(new Func1<List<LiveRoomInfo>, List<LiveRoomInfo>>() {
                                    @Override
                                    public List<LiveRoomInfo> call(List<LiveRoomInfo> liveRoomInfos) {
                                        //缓存数据
                                        if (!isAppend && liveRoomInfos != null) {
                                            FileUtil.saveFileByAccountId(context, FILE_LIVE_CHANNEL,
                                                    ACCOUNT_DIR, liveRoomInfos);
                                        }
                                        return liveRoomInfos;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<List<LiveRoomInfo>>() {
                                    @Override
                                    public void call(List<LiveRoomInfo> liveRoomInfos) {
                                        if (callback != null) {
                                            callback.onSuccess(isAppend, liveRoomInfos);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFail(ApiException e) {
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }
                });
    }
}

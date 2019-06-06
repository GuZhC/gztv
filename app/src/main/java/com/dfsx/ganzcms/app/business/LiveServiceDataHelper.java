package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.ganzcms.app.model.SearchLiveData;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by liuwb on 2017/7/5.
 */
public class LiveServiceDataHelper {

    public static final String CACHE_DIR = "all";
    public static final String CACHE_FILE_NAME = "com_dfsx_lscms_app_business_LiveServiceData.txt";
    public static final String CACHE_BANNER_FILE_NAME = "com_dfsx_lscms_app_business_banner_LiveServiceData.txt";

    private Context context;

    public LiveServiceDataHelper(Context context) {
        this.context = context;
    }

    public void getData(boolean isReadCache, int page, int pageSize,
                        final DataRequest.DataCallback<List<LiveInfo>> callback) {
        searchData(isReadCache, page, pageSize, new DataRequest.DataCallback<SearchLiveData>() {
            @Override
            public void onSuccess(boolean isAppend, SearchLiveData data) {
                if (callback != null) {
                    List<LiveInfo> liveInfos = data != null ? data.getLiveInfoList() : null;
                    callback.onSuccess(isAppend, liveInfos);
                }
            }

            @Override
            public void onFail(ApiException e) {
                callback.onFail(e);
            }
        });
    }

    public void searchData(boolean isReadCache, int page, int pageSize,
                           DataRequest.DataCallback<SearchLiveData> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/shows/search?keywords=" + "" +
                "&page=" + page +
                "&size=" + pageSize +
                "&type=" + 2 +
                "&state=" + 0;
        new DataFileCacheManager<SearchLiveData>(context, CACHE_DIR, CACHE_FILE_NAME) {

            @Override
            public SearchLiveData jsonToBean(JSONObject json) {
                SearchLiveData info = new Gson().fromJson(json.toString(), SearchLiveData.class);
                return info;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .build(), page > 1, isReadCache)
                .setCallback(callback);
    }

    /**
     * 获取活动直播里面的Banner数据
     *
     * @param isReadCache
     * @param callback
     */
    public void getBannerLoopData(boolean isReadCache, DataRequest.DataCallback<SearchLiveData> callback) {
        getRecommandedData(isReadCache, 1, 4, callback);
    }

    /**
     * 获取活动推荐的数据
     *
     * @param isReadCache
     * @param page
     * @param pageSize
     * @param callback
     */
    public void getRecommandedData(boolean isReadCache, int page, int pageSize,
                                   DataRequest.DataCallback<SearchLiveData> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/shows/recommanded/?page=" + page +
                "&size=" + pageSize +
                "&type=" + 2 +
                "&state=" + 0;
        new DataFileCacheManager<SearchLiveData>(context, CACHE_DIR, CACHE_BANNER_FILE_NAME) {

            @Override
            public SearchLiveData jsonToBean(JSONObject json) {
                SearchLiveData info = new Gson().fromJson(json.toString(), SearchLiveData.class);
                return info;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(App.getInstance().getCurrentToken())
                        .build(), page > 1, isReadCache)
                .setCallback(callback);
    }
}

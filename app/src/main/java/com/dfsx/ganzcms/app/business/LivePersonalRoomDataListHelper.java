package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.*;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播页面的数据获取
 * Created by liuwb on 2016/12/13.
 */
public class LivePersonalRoomDataListHelper {

    private Context context;

    public static final String ACCOUNT_DIR = "all";
    public static final String FILE_LIVE_CHANNEL = "com.dfsx.lscms.app_main_FILE_LIVE_CHANNEL.txt";

    public LivePersonalRoomDataListHelper(Context context) {
        this.context = context;
    }

    public void getData(int page, int size,
                        DataRequest.DataCallback<List<Room>> callback) {
        getData(true, page, size, callback);
    }

    /**
     * @param page     从1开始
     * @param size
     * @param callback
     */
    public void getData(boolean isReadCache, int page, int size,
                        DataRequest.DataCallback<List<Room>> callback) {
        searchLiveData(isReadCache, "", page, size, callback);
    }

    public void searchLiveData(boolean isReadCache, String keyWords, int page, int size,
                               DataRequest.DataCallback<List<Room>> callback) {
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/shows/search?keywords=" + keyWords +
                "&page=" + page +
                "&size=" + size +
                "&type=" + 1 +
                "&state=" + 0;
        new DataFileCacheManager<ArrayList<Room>>(context, ACCOUNT_DIR, FILE_LIVE_CHANNEL) {

            @Override
            public ArrayList<Room> jsonToBean(JSONObject json) {
                SearchLiveData info = new Gson().fromJson(json.toString(), SearchLiveData.class);
                if (info != null && info.getLiveInfoList() != null) {
                    ArrayList<Room> list = new ArrayList<Room>();
                    for (LiveInfo info1 : info.getLiveInfoList()) {
                        list.add(info1);
                    }
                    return list;
                }
                return null;
            }
        }.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .build(), page > 1, isReadCache)
                .setCallback(callback);
    }


}

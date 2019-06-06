package com.dfsx.ganzcms.app.business;

import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.LiveTagInfo;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 直播标签
 */
public class LiveTAGHelper implements ILiveTag {


    private static LiveTAGHelper instance = new LiveTAGHelper();

    private ArrayList<LiveTagInfo> liveTagInfoArrayList;

    private HashMap<String, Long> liveTagNameMaps;
    private HashMap<Long, String> liveTagIDMaps;
    private BlockingQueue<ArrayList<LiveTagInfo>> arrayListBlockingQueue;

    private LiveTAGHelper() {
    }

    public static LiveTAGHelper getInstance() {
        return instance;
    }

    @Override
    public void getAllLiveTag(DataRequest.DataCallback<ArrayList<LiveTagInfo>> callback) {
        if (liveTagInfoArrayList != null) {
            if (callback != null) {
                callback.onSuccess(false, liveTagInfoArrayList);
            }
        } else {
            getLiveTagByNet(callback);
        }

    }

    private void getLiveTagByNet(DataRequest.DataCallback<ArrayList<LiveTagInfo>> callback) {
        DataRequest<ArrayList<LiveTagInfo>> request = new DataRequest<ArrayList<LiveTagInfo>>(App.getInstance()
                .getApplicationContext()) {
            @Override
            public ArrayList<LiveTagInfo> jsonToBean(JSONObject json) {
                if (json != null) {
                    JSONArray array = json.optJSONArray("result");
                    if (array != null && array.length() > 0) {
                        ArrayList<LiveTagInfo> list = new ArrayList<>();
                        liveTagNameMaps = new HashMap<>();
                        liveTagIDMaps = new HashMap<>();
                        Gson g = new Gson();
                        for (int i = 0; i < array.length(); i++) {
                            LiveTagInfo info = g.fromJson(array.optJSONObject(i).toString(), LiveTagInfo.class);
                            if (info != null && info.getId() != 0) {
                                list.add(info);
                                liveTagNameMaps.put(info.getName(), info.getId());
                                liveTagIDMaps.put(info.getId(), info.getName());
                            }
                        }
                        liveTagInfoArrayList = list;
                        if (arrayListBlockingQueue != null) {
                            arrayListBlockingQueue.add(list);
                        }
                        return list;
                    }
                }
                return null;
            }
        };
        String url = App.getInstance().getmSession().getLiveServerUrl() +
                "/public/shows/tags";
        request.getData(new DataRequest.HttpParamsBuilder()
                .setUrl(url)
                .setRequestType(DataReuqestType.GET)
                .setToken(App.getInstance().getCurrentToken())
                .build(), false)
                .setCallback(callback);
    }

    /**
     * run thread, may be blocked.
     *
     * @param id
     * @return
     */
    public String getLiveTagNameById(long id) {
        if (liveTagIDMaps == null) {
            syncGetData();
        }
        if (liveTagIDMaps != null) {
            return liveTagIDMaps.get(id);
        }
        return "";
    }

    /**
     * run thread, may be blocked
     *
     * @param name
     * @return
     */
    public long getLiveTagIdByName(String name) {
        if (liveTagNameMaps == null) {
            syncGetData();
        }
        if (liveTagNameMaps != null) {
            return liveTagNameMaps.get(name);
        }
        return 0;
    }

    private boolean syncGetData() {
        arrayListBlockingQueue = new LinkedBlockingQueue<>();
        getLiveTagByNet(null);
        try {
            ArrayList<LiveTagInfo> list = arrayListBlockingQueue.take();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}

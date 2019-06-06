package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.model.LiveEntity;
import com.dfsx.ganzcms.app.model.LiveProgramEntity;
import com.dfsx.ganzcms.app.App;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by heyang on  2018/1/19
 * 电视  广播 回看节目单
 */
public class PlayBackListManager extends DataFileCacheManager<HashMap<String, ArrayList<LiveEntity.LiveChannel>>> {

    private ContentCmsApi contentCmsApi;

    /**
     * 此文件的缓存文件名 SELECTTOPITEMLISTMANAGER
     */
    public static final String PLAYBACKLISTMANAGER = "PlayBackListManager.tx";
    private AppSession mSession;
    private int mColumnType;
    private long liveid=-1;


    public PlayBackListManager(Context context, String accountId, int columnType) {
        super(context, accountId, PLAYBACKLISTMANAGER + "_" + columnType);
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
        contentCmsApi = new ContentCmsApi(context);
    }

    public PlayBackListManager(Context context, String accountId, int columnType, boolean isLooper) {
        super(context, accountId, PLAYBACKLISTMANAGER + "_" + columnType + (isLooper ? "_LooperImg" : ""));
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public void getData(long id, String startDate, DataCallback<HashMap<String, ArrayList<LiveEntity.LiveChannel>>> callback) {
        liveid=id;
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/lives/" + id + "/playlists?";
        url += "start=" + startDate + "&limit=7";
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), false)
                .setCallback(callback);
    }

    @Override
    public HashMap<String, ArrayList<LiveEntity.LiveChannel>> jsonToBean(JSONObject json) {
        HashMap<String, ArrayList<LiveEntity.LiveChannel>> findMap = new HashMap<>();
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            Iterator iterator = json.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                JSONArray arr = json.optJSONArray(key);
                if (arr != null && arr.length() > 0) {
                    ArrayList<LiveEntity.LiveChannel> dlist = new ArrayList<LiveEntity.LiveChannel>();
                    boolean isflag = false;
                    try {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            LiveProgramEntity.LiveProgram entity = new Gson().fromJson(obj.toString(), LiveProgramEntity.LiveProgram.class);
                            // 获取回放信息
                            List<LiveProgramEntity.LiveProgram> list = contentCmsApi.getProgramBackById(liveid, key);
                            if (list != null && list.size() > 0) {
                                for (LiveProgramEntity.LiveProgram tag : list) {
                                    if (entity.getStart_time() == tag.getStart_time() &&
                                            entity.getStop_time() == tag.getStop_time()) {
                                        entity.setM3u8_url(tag.getM3u8_url());
                                        break;
                                    }
                                }
                            }
                            String date = Util.getTimeString("yyyy-MM-dd", entity.getStart_time());
                            String time = Util.getTimeString("HH:mm:ss", entity.getStart_time());
                            String endtime = Util.getTimeString("HH:mm:ss", entity.getStop_time());
                            if (!TextUtils.equals(key, date)) continue;
                            LiveEntity.LiveChannel chanl = new LiveEntity.LiveChannel(entity.getM3u8_url(), entity.getName(), entity.getDescription(), time, endtime);
                            Date curDate = new Date();//获取当前时间
                            if (!isflag) {
                                String liveUrl = chanl.url;
                                long current = curDate.getTime();
                                if (entity.getM3u8_url() == null || TextUtils.isEmpty(entity.getM3u8_url())) {
                                    if (entity.getStart_time() * 1000 < current) {
                                        chanl.isLive = true;
                                        chanl.isPlayback = false;
                                        isflag = true;
                                    } else {
                                        int pos = i > 0 ? --i : 0;
                                        if (!dlist.isEmpty() && pos < dlist.size()) {
                                            LiveEntity.LiveChannel tasg = dlist.get(pos);
                                            tasg.isLive = true;
                                            tasg.isPlayback = false;
                                            tasg.url = liveUrl;
                                        }
                                    }
                                    isflag = true;
                                } else {
                                    chanl.isPlayback = true;
                                }
                            }
                            dlist.add(chanl);
                        }
                        findMap.put(key, dlist);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return findMap;
    }

    ArrayList<LiveEntity.LiveChannel> assembleNodes(JSONObject jsonObject) {
        ArrayList<LiveEntity.LiveChannel> dlist = null;
        try {
            JSONArray result = jsonObject.optJSONArray("result");
            if (result != null) {
                dlist = new ArrayList<LiveEntity.LiveChannel>();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject item = (JSONObject) result.get(i);
                    LiveEntity.LiveChannel snews = new LiveEntity.LiveChannel();
                    dlist.add(snews);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dlist;
    }
}

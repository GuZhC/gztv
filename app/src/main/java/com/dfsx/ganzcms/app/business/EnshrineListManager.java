package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.model.CollectEntry;
import com.dfsx.ganzcms.app.App;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by heyang on 2016/12/5
 */
public class EnshrineListManager extends DataFileCacheManager<ArrayList<CollectEntry>> {

    /**
     * 此文件的缓存文件名 SELECTTOPITEMLISTMANAGER
     */
    public static final String ENSHRINELISTMANAGER = App.getInstance().getPackageName() + "EnshrineListManager.tx";
    private AppSession mSession;
    private long mColumnType;


    public EnshrineListManager(Context context, String accountId, long columnType) {
        super(context, accountId, ENSHRINELISTMANAGER + "_" + columnType);
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public EnshrineListManager(Context context, String accountId, long columnType, boolean isLooper) {
        super(context, accountId, ENSHRINELISTMANAGER + "_" + columnType + (isLooper ? "_LooperImg" : ""));
        mSession = App.getInstance().getmSession();
        this.mColumnType = columnType;
    }

    public void setColumnType(int columnType) {
        this.mColumnType = columnType;
    }

    public void start(boolean bAddTail, int offset) {
        String param = "/public/users/current/favorites?";
//        switch ((int) mColumnType) {
//            case ColumnTYPE.POLITICS_NEWS_TYPE:
//                param += "sevice_minsheng_news.json?";
//                break;
//            case ColumnTYPE.INTERNAL_NEWS_TYPE:
//                param += "sevice_yaowen.json?";
//                break;
//            case ColumnTYPE.EXTERNAL_NEWS_TYPE:
//                param += "sevice_huati.json?";
//                break;
//        }
        getDocObjectParam(bAddTail, param, offset);
    }

    private void getDocObjectParam(boolean bAddTail, String param, long offset) {
        String url = App.getInstance().getmSession().getPortalServerUrl();
        url += param;
        url += "page=" + offset + "";
        getData(new HttpParamsBuilder().
                setUrl(url).
                setRequestType(DataReuqestType.GET).
                setToken(App.getInstance().getCurrentToken()).build(), bAddTail,false);
    }

    @Override
    public ArrayList<CollectEntry> jsonToBean(JSONObject jsonObject) {
        ArrayList<CollectEntry> dlist = null;
        if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
            JSONArray data = jsonObject.optJSONArray("data");
            if (data != null) {
                dlist = new ArrayList<CollectEntry>();
                for (int i = 0; i < data.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) data.get(i);
                        CollectEntry entry = new Gson().fromJson(object.toString(), CollectEntry.class);
                        entry.setId(object.optLong("id"));
//                        if (TextUtils.equals(entry.getItem_source(), "community"))
//                            entry.setType(5);
                        dlist.add(entry);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dlist;
    }


}

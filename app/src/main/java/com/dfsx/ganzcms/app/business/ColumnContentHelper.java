package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.util.Log;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.ColumnContentListItem;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏目类容获取
 */
public class ColumnContentHelper {

    private Context context;

    public ColumnContentHelper(Context context) {
        this.context = context;
    }

    public List<ColumnContentListItem> getColumnContentList(String columnIdOrColumnKey, int page, int pageSize) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() +
                "/public/columns/" + columnIdOrColumnKey + "/contents?page=" + page +
                "&size=" + pageSize;

        String res = HttpUtil.executeGet(url, new HttpParameters(), App.getInstance().getCurrentToken());
        Log.d("http", url + "--- response == " + res);
        try {
            JSONObject resJosn = new JSONObject(res);
            JSONArray arr = resJosn.optJSONArray("data");
            List<ColumnContentListItem> list = new ArrayList<>();
            Gson g = new Gson();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.optJSONObject(i);
                ColumnContentListItem iData = g.fromJson(item.toString(), ColumnContentListItem.class);
                list.add(iData);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

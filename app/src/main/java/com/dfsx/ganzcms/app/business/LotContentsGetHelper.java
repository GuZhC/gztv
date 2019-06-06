package com.dfsx.ganzcms.app.business;

import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LotContentsGetHelper<D> {

    public HashMap<Long, D> getContentMap(String api, List<Long> idList, Class<D> dataClass) {
        try {
            String url = api;
            for (int i = 0; i < idList.size(); i++) {
                url += idList.get(i);
                if (i != idList.size() - 1) {
                    url += ",";
                }
            }
            String res = HttpUtil.executeGet(url, new HttpParameters(),
                    App.getInstance().getCurrentToken());
            StringUtil.checkHttpResponseError(res);
            JSONArray array = new JSONArray(res);
            if (array != null) {
                HashMap<Long, D> map = new HashMap<>();
                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    D content = gson.fromJson(item.toString(), dataClass);
                    long id = item.optLong("id");
                    if (id != 0) {
                        map.put(id, content);
                    }
                }
                return map;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

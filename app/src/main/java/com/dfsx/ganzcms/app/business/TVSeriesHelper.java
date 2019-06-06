package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.TVSeriesEntry;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TVSeriesHelper {
    private Context context;

    public TVSeriesHelper(Context context) {
        this.context = context;
    }

    public void getTVSeriesData(long contentId, int page, int pageSize, DataRequest.DataCallback<List<TVSeriesEntry>> callback) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + contentId +
                "/contents?page=" + page + "&size=" + pageSize;
        new DataRequest<List<TVSeriesEntry>>(context) {
            @Override
            public List<TVSeriesEntry> jsonToBean(JSONObject json) {
                ArrayList<TVSeriesEntry> list = new ArrayList<>();
                if (json != null) {
                    JSONArray result = json.optJSONArray("data");
                    if (result != null) {
                        Gson gson = new Gson();
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject item = result.optJSONObject(i);
                            TVSeriesEntry entry = gson.fromJson(item.toString(), TVSeriesEntry.class);
                            list.add(entry);
                        }
                    }
                }
                return list;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setToken(App.getInstance()
                                .getCurrentToken())
                        .setRequestType(DataReuqestType.GET)
                        .build(), page > 1, page == 1)
                .setCallback(callback);
    }
}

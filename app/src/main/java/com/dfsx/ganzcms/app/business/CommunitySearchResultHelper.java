package com.dfsx.ganzcms.app.business;

import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.searchlibaray.businness.BaseSearchResultHelper;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchResult;
import com.google.gson.Gson;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 *   by create heyang  2017/12/11
 */

public class CommunitySearchResultHelper extends BaseSearchResultHelper {
    private TopicalApi topicalApi;

    public CommunitySearchResultHelper(SearchResult searchResult, boolean isAppend, Object searchTag, DataRequest.DataCallback<List<ISearchData>> callback) {
        super(searchResult, isAppend, searchTag, callback);
        topicalApi = new TopicalApi(App.getInstance().getBaseContext());
    }

    @Override
    public ArrayList<ISearchData> getRealDataByIds(Long... ids) {
        String url =App.getInstance().getmSession().getCommunityServerUrl() +
                "/public/threads/multi/";
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                String appendString = ids[i] + "";
                if (i != ids.length - 1) {
                    appendString += ",";
                }
                url += appendString;
            }
            String res = HttpUtil.executeGet(url, new HttpParameters(), null);
            try {
                StringUtil.checkHttpResponseError(res);
                JSONArray array = new JSONArray(res);
                if (array != null) {
                    Gson gson = new Gson();
                    final ArrayList<ISearchData> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        TopicalEntry data = gson.fromJson(array.optJSONObject(i).toString(),
                                TopicalEntry.class);
                        ISearchData tag = topicalApi.getTopicTopicalInfo(data);
//                        ISearchData tag = topicalApi.getTopicParams(data,false);
                        list.add(tag);
                    }
                    return list;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

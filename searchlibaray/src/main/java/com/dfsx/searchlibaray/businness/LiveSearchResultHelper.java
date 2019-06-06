package com.dfsx.searchlibaray.businness;

import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.searchlibaray.AppSearchManager;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.LiveInfo;
import com.dfsx.searchlibaray.model.SearchResult;
import com.google.gson.Gson;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class LiveSearchResultHelper extends BaseSearchResultHelper {

    public LiveSearchResultHelper(SearchResult searchResult, boolean isAppend, Object searchTag, DataRequest.DataCallback<List<ISearchData>> callback) {
        super(searchResult, isAppend, searchTag, callback);
    }

    @Override
    public ArrayList<ISearchData> getRealDataByIds(Long... ids) {
        String url = AppSearchManager.getInstance().getSearchConfig().getLiveHttpUrl() +
                "/public/shows/multi/";
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
                    ArrayList<ISearchData> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        LiveInfo data = gson.fromJson(array.optJSONObject(i).toString(),
                                LiveInfo.class);
                        list.add(data);
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

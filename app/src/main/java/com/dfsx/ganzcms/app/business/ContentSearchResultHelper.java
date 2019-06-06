package com.dfsx.ganzcms.app.business;

import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
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


public class ContentSearchResultHelper extends BaseSearchResultHelper {
    private ContentCmsApi contentCmsApi;
    private SearchResult searchResult;

    public ContentSearchResultHelper(SearchResult searchResult, boolean isAppend, Object searchTag, DataRequest.DataCallback<List<ISearchData>> callback) {
        super(searchResult, isAppend, searchTag, callback);
        contentCmsApi = new ContentCmsApi(App.getInstance().getBaseContext());
    }

    @Override
    public ArrayList<ISearchData> getRealDataByIds(Long... ids) {
        String url =App.getInstance().getmSession().getContentcmsServerUrl()+
                "/public/contents/multi/";
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
                        ContentCmsEntry data = gson.fromJson(array.optJSONObject(i).toString(),
                                ContentCmsEntry.class);
                        int thumn_size = data.getThumbnail_urls() != null ? data.getThumbnail_urls().size() : 0;
                        data.setShowType(contentCmsApi.getShowModeType(data.getList_item_mode(), data.getType()));
                        int modeType = contentCmsApi.getModeType(data.getType(), thumn_size);
                        data.setModeType(modeType);
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

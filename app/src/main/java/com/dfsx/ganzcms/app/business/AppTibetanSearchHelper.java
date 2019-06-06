package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.searchlibaray.model.ISearchData;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppTibetanSearchHelper extends AppSearchHelper {
    private long columnId;

    private ContentCmsApi contentCmsApi;

    public AppTibetanSearchHelper(Context context, long columnId) {
        super(context);
        this.columnId = columnId;
        contentCmsApi = new ContentCmsApi(context);
    }

    @Override
    public void searchContentData(Object searchTag, String keyword, int page, int pageSize, DataRequest.DataCallback<List<ISearchData>> callback) {
        String url = App.getInstance().getmSession().getContentcmsServerUrl() + "/public/columns/" + columnId
                + "/contents?page=" + page + "&size=" + pageSize + "&keyword=" + keyword;
        new DataRequest<List<ISearchData>>(context) {
            @Override
            public List<ISearchData> jsonToBean(JSONObject json) {
                ArrayList<ISearchData> socityNewsAarry = new ArrayList<>();
                try {
                    JSONArray result = json.optJSONArray("data");
                    if (result != null) {
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject item = (JSONObject) result.get(i);
                            ContentCmsEntry entry = new Gson().fromJson(item.toString(), ContentCmsEntry.class);
                            //                    if (TextUtils.equals(entry.getType(), "quick-entry"))
                            //                        continue;
                            //判断是不是多图
                            int thumn_size = entry.getThumbnail_urls() != null ? entry.getThumbnail_urls().size() : 0;
                            entry.setShowType(contentCmsApi.getShowModeType(entry.getList_item_mode(), entry.getType()));
                            int modeType = contentCmsApi.getModeType(entry.getType(), thumn_size);
                            if (modeType == 3) {
                                // show 直播 特殊处理
                                entry.setShowType(modeType);
                                JSONObject extendsObj = item.optJSONObject("extension");
                                if (extendsObj != null) {
                                    JSONObject showObj = extendsObj.optJSONObject("show");
                                    if (showObj != null) {
                                        ContentCmsEntry.ShowExtends showExtends = new Gson().fromJson(showObj.toString(), ContentCmsEntry.ShowExtends.class);
                                        entry.setShowExtends(showExtends);
                                    }
                                }
                            }
                            entry.setModeType(modeType);
                            socityNewsAarry.add(entry);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return socityNewsAarry;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setUrl(url)
                        .setRequestType(DataReuqestType.GET)
                        .setTagView(searchTag)
                        .build(), page > 1).setCallback(callback);

    }
}

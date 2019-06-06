package com.dfsx.searchlibaray.businness;

import android.content.Context;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.searchlibaray.AppSearchManager;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchResult;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.List;

public class SearchHelper {

    protected Context context;

    public SearchHelper(Context context) {
        this.context = context;
    }

    public void search(Object searchTag, String keyword, String source, String type, int page, int pageSize,
                       int order, long startTimeStamp, long endTimeStamp, DataRequest.DataCallbackTag callback) {
        String url = AppSearchManager.getInstance().getSearchConfig().getHttpBaseUrl()
                + "/public/site/searcher?keyword=" + keyword
                + "&source=" + source
                + "&type=" + type
                + "&page=" + page
                + "&size=" + pageSize
                + "&order=" + order
                + "&start=" + startTimeStamp
                + "&stop=" + endTimeStamp;
        new DataRequest<SearchResult>(context) {

            @Override
            public SearchResult jsonToBean(JSONObject json) {
                if (json != null) {
                    SearchResult result = new Gson().fromJson(json.toString(),
                            SearchResult.class);
                    return result;
                }
                return null;
            }
        }
                .getData(new DataRequest.HttpParamsBuilder()
                        .setRequestType(DataReuqestType.GET)
                        .setUrl(url)
                        .setTagView(searchTag)
                        .build(), page > 1)
                .setCallback(callback);
    }

    public void searchContentData(Object searchTag, String keyword,
                                  int page, int pageSize, final DataRequest.DataCallback<List<ISearchData>> callback) {
        search(searchTag, keyword, "cms", "content", page, pageSize, 0, 0L, 0L, new DataRequest.DataCallbackTag<SearchResult>() {
            @Override
            public void onSuccess(Object object, boolean isAppend, SearchResult data) {
                new ContentSearchResultHelper(data, isAppend, object, callback).get();
            }

            @Override
            public void onSuccess(boolean isAppend, SearchResult data) {

            }

            @Override
            public void onFail(ApiException e) {
                if (callback != null) {
                    callback.onFail(e);
                }
            }
        });
    }


    public void searchLiveData(Object searchTag, String keyword,
                               int page, int pageSize, final DataRequest.DataCallback<List<ISearchData>> callback) {
        search(searchTag, keyword, "live", "show", page, pageSize, 0, 0L, 0L,
                new DataRequest.DataCallbackTag<SearchResult>() {
                    @Override
                    public void onSuccess(Object object, boolean isAppend, SearchResult data) {
                        new LiveSearchResultHelper(data, isAppend, object, callback).get();
                    }

                    @Override
                    public void onSuccess(boolean isAppend, SearchResult data) {

                    }

                    @Override
                    public void onFail(ApiException e) {
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }
                });
    }


    /**
     * 搜索圈子数据
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @param callback
     */
    public void searchQuanZiData(Object searchTag, String keyword,
                                 int page, int pageSize, final DataRequest.DataCallback<List<ISearchData>> callback) {

    }


    /**
     * 搜索用户数据
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @param callback
     */
    public void searchUserData(Object searchTag, String keyword,
                               int page, int pageSize, final DataRequest.DataCallback<List<ISearchData>> callback) {
        search(searchTag, keyword, "general", "user", page, pageSize, 0, 0L, 0L,
                new DataRequest.DataCallbackTag<SearchResult>() {
                    @Override
                    public void onSuccess(Object object, boolean isAppend, SearchResult data) {
                        new UserSearchResultHelper(data, isAppend, object, callback).get();
                    }

                    @Override
                    public void onSuccess(boolean isAppend, SearchResult data) {

                    }

                    @Override
                    public void onFail(ApiException e) {
                        if (callback != null) {
                            callback.onFail(e);
                        }
                    }
                });
    }

    /**
     * 搜索任何数据 设计为扩展的类型
     *
     * @param page
     * @param pageSize
     * @param callback
     */
    public void searchAnyData(Object searchTag, int page, int pageSize, final DataRequest.DataCallback<List<ISearchData>> callback) {

    }
}

package com.dfsx.ganzcms.app.business;

import android.content.Context;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.searchlibaray.businness.SearchHelper;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchResult;

import java.util.List;

public class AppSearchHelper extends SearchHelper {

    public AppSearchHelper(Context context) {
        super(context);
    }

    @Override
    public void searchContentData(Object searchTag, String keyword, int page, int pageSize,final DataRequest.DataCallback<List<ISearchData>> callback) {
//        super.searchContentData(searchTag, keyword, page, pageSize, callback);
        //TDO cms搜索的获取
        search(searchTag, keyword, "cms", "content", page, pageSize, 0, 0L, 0L,
                new DataRequest.DataCallbackTag<SearchResult>() {
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

    @Override
    public void searchQuanZiData(Object searchTag, String keyword, int page, int pageSize, final DataRequest.DataCallback<List<ISearchData>> callback) {
        //TODO 圈子的列表获取。 推荐实现BaseSearchResultHelper来实现逻辑
        search(searchTag, keyword, "community", "thread", page, pageSize, 0, 0L, 0L,
                new DataRequest.DataCallbackTag<SearchResult>() {
                    @Override
                    public void onSuccess(Object object, boolean isAppend, SearchResult data) {
                        new CommunitySearchResultHelper(data, isAppend, object, callback).get();
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
}

package com.dfsx.searchlibaray.businness;

import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.SearchItemInfo;
import com.dfsx.searchlibaray.model.SearchResult;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseSearchResultHelper {

    private SearchResult searchResult;
    private boolean isAppend;
    private Object tag;
    private DataRequest.DataCallback<List<ISearchData>> callback;

    public BaseSearchResultHelper(SearchResult searchResult, boolean isAppend, Object searchTag, DataRequest.DataCallback<List<ISearchData>> callback) {
        this.searchResult = searchResult;
        this.isAppend = isAppend;
        this.tag = searchTag;
        this.callback = callback;
    }


    public void get() {
        if (searchResult != null && searchResult.getTotal() > 0) {
            Observable.just(searchResult)
                    .observeOn(Schedulers.io())
                    .map(new Func1<SearchResult, List<ISearchData>>() {
                        @Override
                        public List<ISearchData> call(SearchResult searchResult) {
                            if (searchResult.getData() != null) {
                                ArrayList<Long> idList = new ArrayList<>();
                                HashMap<Long, SearchItemInfo> itemInfoHashMap = new HashMap<>();
                                for (SearchItemInfo itemInfo : searchResult.getData()) {
                                    idList.add(itemInfo.getId());
                                    itemInfoHashMap.put(itemInfo.getId(), itemInfo);
                                }
                                List<ISearchData> list = getRealDataByIds(idList.toArray(new Long[0]));
                                if (list != null) {
                                    for (ISearchData data : list) {
                                        data.setSearchItemInfo(itemInfoHashMap.get(data.getId()));
                                    }
                                }
                                return list;
                            }
                            return null;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<ISearchData>>() {
                        @Override
                        public void call(List<ISearchData> iSearchData) {
                            onSuccessCallback(iSearchData);
                        }
                    });
        } else {
            onSuccessCallback(null);
        }
    }

    private void onSuccessCallback(List<ISearchData> iSearchData) {
        if (callback != null) {
            if (callback instanceof DataRequest.DataCallbackTag) {
                DataRequest.DataCallbackTag dataCallbackTag = (DataRequest.DataCallbackTag) callback;
                dataCallbackTag.onSuccess(tag, isAppend, iSearchData);
            }
            callback.onSuccess(isAppend, iSearchData);
        }
    }

    public abstract ArrayList<ISearchData> getRealDataByIds(Long... ids);
}

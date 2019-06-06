package com.dfsx.ganzcms.app.business;

import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.adapter.ListViewAdapter;
import com.dfsx.ganzcms.app.fragment.SearchWnd;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;

import java.util.ArrayList;

/**
 * Created by heyang on 2017/1/10.
 */
public class ContentSearchImpl implements SearchWnd.OnSearchClickListener {

    private ContentCmsApi mApi = null;

    public ContentSearchImpl() {
        mApi = new ContentCmsApi(App.getInstance().getApplicationContext());
    }

    @Override
    public void onSearch(final SearchWnd searchWindow, String searchKey, int page) {
        mApi.findAllByKey(searchKey, new DataRequest.DataCallback<ArrayList<ContentCmsEntry>>() {
            @Override
            public void onSuccess(final boolean isAppend, ArrayList<ContentCmsEntry> data) {
                if (data != null && data.size() > 0) {
                    searchWindow.onSearchEnd();
                    updateData(searchWindow, isAppend, data);
                } else {
                    Toast.makeText(App.getInstance().getApplicationContext(),
                            "没找到相关数据", Toast.LENGTH_SHORT).show();
                    searchWindow.onSearchEnd();
                }
            }

            @Override
            public void onFail(ApiException e) {
                Toast.makeText(App.getInstance().getApplicationContext(),
                        "搜索失败", Toast.LENGTH_SHORT).show();
                searchWindow.onSearchEnd();
            }
        });

//        dataListHelper.searchData(searchKey, page, 10, new DataRequest.DataCallback<List<ContentCmsEntry>>() {
//            @Override
//            public void onSuccess(boolean isAppend, List<ContentCmsEntry> data) {
//                searchWindow.onSearchEnd();
//                updateData(searchWindow, isAppend, data);
//            }
//
//            @Override
//            public void onFail(ApiException e) {
//                Toast.makeText(App.getInstance().getApplicationContext(),
//                        "搜索失败", Toast.LENGTH_SHORT).show();
//                searchWindow.onSearchEnd();
//            }
//        });
    }

    private void updateData(SearchWnd searchWnd, boolean isAdd, ArrayList<ContentCmsEntry> list) {
        if (searchWnd != null && searchWnd.getListViewAdapter() != null) {
            if (searchWnd.getListViewAdapter() instanceof ListViewAdapter) {
                ListViewAdapter roomAdapter = (ListViewAdapter) searchWnd.getListViewAdapter();
                roomAdapter.update(list, isAdd);
            }
        }
    }
}

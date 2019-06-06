package com.dfsx.ganzcms.app.business;

import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.adapter.LiveRoomAdapter;
import com.dfsx.ganzcms.app.fragment.SearchWnd;
import com.dfsx.ganzcms.app.model.Room;

import java.util.List;

/**
 * Created by liuwb on 2017/1/10.
 */
public class LiveSearchImpl implements SearchWnd.OnSearchClickListener {

    private LivePersonalRoomDataListHelper dataListHelper;

    public LiveSearchImpl() {
        dataListHelper = new LivePersonalRoomDataListHelper(App.getInstance().getApplicationContext());
    }

    @Override
    public void onSearch(final SearchWnd searchWindow, String searchKey, int page) {
        dataListHelper.searchLiveData(false, searchKey, page, 10, new DataRequest.DataCallback<List<Room>>() {
            @Override
            public void onSuccess(boolean isAppend, List<Room> data) {
                searchWindow.onSearchEnd();
                updateData(searchWindow, isAppend, data);
            }

            @Override
            public void onFail(ApiException e) {
                Toast.makeText(App.getInstance().getApplicationContext(),
                        "搜索失败", Toast.LENGTH_SHORT).show();
                searchWindow.onSearchEnd();
            }
        });
    }

    private void updateData(SearchWnd searchWnd, boolean isAdd, List<Room> list) {
        if (searchWnd != null && searchWnd.getListViewAdapter() != null) {
            if (searchWnd.getListViewAdapter() instanceof LiveRoomAdapter) {
                LiveRoomAdapter roomAdapter = (LiveRoomAdapter) searchWnd.getListViewAdapter();
                roomAdapter.update(list, isAdd);
            }
        }
    }
}

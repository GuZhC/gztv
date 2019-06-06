package com.dfsx.searchlibaray;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.util.IntentUtil;
import com.dfsx.searchlibaray.adapter.FilterSearchListAdapter;
import com.dfsx.searchlibaray.businness.SearchHelper;
import com.dfsx.searchlibaray.businness.UserInfo;
import com.dfsx.searchlibaray.model.ISearchData;
import com.dfsx.searchlibaray.model.LiveInfo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.HashMap;
import java.util.List;

public class FilterSearchFragment extends AbsSearchFragment implements PullToRefreshBase.OnRefreshListener2,
        AdapterView.OnItemClickListener {

    protected Activity activity;

    private RadioGroup radioFilterGroup;
    protected PullToRefreshListView pullToRefreshListView;
    protected ListView listView;
    protected LinearLayout containerView;

    protected View rootView;

    /**
     * 搜索数据内存缓存
     */
    private HashMap<Integer, List<ISearchData>> searchDataMap;

    private int currentSourceId = 0;
    private String currentSearchKey;

    private static final int PAGE_SIZE = 20;

    private int curPage;

    protected SearchHelper searchHelper;

    protected FilterSearchListAdapter searchListAdapter;

    private CustomeProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.frag_filter_content_layout, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        searchDataMap = new HashMap<>();
        currentSourceId = R.id.radio_select_word;
        searchHelper = createSearchHelper();
    }

    @Override
    public void search(String key) {
        this.currentSearchKey = key;
        if (searchDataMap != null) {
            searchDataMap.clear();
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = CustomeProgressDialog.show(activity, "加载中...");
        }
        searchBySource(key, currentSourceId, 1);
    }

    @Override
    public void onEditTextTextChange(String text) {
        this.currentSearchKey = text;
    }

    protected SearchHelper createSearchHelper() {
        SearchHelper helper = new SearchHelper(activity);
        return helper;
    }

    protected FilterSearchListAdapter createListViewAdapter() {
        FilterSearchListAdapter adapter = new FilterSearchListAdapter(activity);
        return adapter;
    }

    private void initView(View v) {
        radioFilterGroup = (RadioGroup) v.findViewById(R.id.radio_filter_group);
        containerView = (LinearLayout) v.findViewById(R.id.content_container);
        pullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_to_refresh_list_view);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        listView = pullToRefreshListView.getRefreshableView();
        searchListAdapter = createListViewAdapter();
        listView.setAdapter(searchListAdapter);
        setListEmptyView(containerView);
        if (containerView.getChildCount() > 0) {
            listView.setEmptyView(containerView);
        }
        listView.setOnItemClickListener(this);
        radioFilterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentSourceId = checkedId;
                if (searchDataMap.get(checkedId) != null && searchDataMap.get(checkedId).size() > 0) {
                    updateAdapterData(false, searchDataMap.get(checkedId));
                } else {
                    searchBySource(currentSearchKey, checkedId, 1);
                    progressDialog = CustomeProgressDialog.show(activity, "加载中...");
                }
            }
        });
    }

    protected void setListEmptyView(LinearLayout emptyContainer) {

    }

    protected void searchBySource(String key, int sourceId, int page) {
        if (TextUtils.isEmpty(key)) {
            key = "";
        }
        this.curPage = page;
        if (sourceId == R.id.radio_select_word) {
            searchHelper.searchContentData(sourceId, key, page, PAGE_SIZE, searchCallback);
        } else if (sourceId == R.id.radio_select_live) {
            searchHelper.searchLiveData(sourceId, key, page, PAGE_SIZE, searchCallback);
        } else if (sourceId == R.id.radio_select_quan) {
            searchHelper.searchQuanZiData(sourceId, key, page, PAGE_SIZE, searchCallback);
        } else if (sourceId == R.id.radio_select_user) {
            searchHelper.searchUserData(sourceId, key, page, PAGE_SIZE, searchCallback);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        searchBySource(currentSearchKey, currentSourceId, 1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        curPage++;
        searchBySource(currentSearchKey, currentSourceId, curPage);
    }

    protected void refreshData()
    {
        searchBySource(currentSearchKey, currentSourceId, curPage);
    }

    private void updateAdapterData(boolean isAppend, List<ISearchData> data) {
        if (searchListAdapter != null) {
            searchListAdapter.update(data, isAppend);
        }

    }

    private DataRequest.DataCallbackTag<List<ISearchData>> searchCallback = new DataRequest.DataCallbackTag<List<ISearchData>>() {
        @Override
        public void onSuccess(Object object, boolean isAppend, List<ISearchData> data) {
            if (object instanceof Integer) {
                int tagId = (int) object;
                if (tagId == currentSourceId) {
                    updateAdapterData(isAppend, data);
                }
                if (!isAppend) {
                    searchDataMap.put(tagId, data);
                }
            }
            pullToRefreshListView.onRefreshComplete();

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onSuccess(boolean isAppend, List<ISearchData> data) {

        }

        @Override
        public void onFail(ApiException e) {
            pullToRefreshListView.onRefreshComplete();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int pos = position - listView.getHeaderViewsCount();
        if (pos >= 0 && searchListAdapter.getData() != null && pos < searchListAdapter.getData().size()) {
            ISearchData searchData = searchListAdapter.getData().get(pos);
            if (searchData != null) {
                onSearchItemClick(searchData);
            }
        }
    }

    protected void onSearchItemClick(ISearchData searchData) {
        if (searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_USER) {
            UserInfo info = (UserInfo) searchData.getContentData();
            com.dfsx.core.common.Util.IntentUtil.gotoPersonHomeAct(activity, info.getId());
        } else if (searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_LIVE_SHOW) {
            LiveInfo info = (LiveInfo) searchData.getContentData();
            if (info.getState() == 3) {//回放
                IntentUtil.goBackPlayRoom(activity, info.getId());
            } else {
                IntentUtil.goFullScreenLiveRoom(activity, info.getId());
            }
        } else if (searchData.getShowStyle() == ISearchData.SearchShowStyle.STYLE_LIVE_SERVICE) {
            LiveInfo info = (LiveInfo) searchData.getContentData();
            IntentUtil.goLiveServiceRoom(activity, info.getId());
        }
    }
}

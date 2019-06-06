package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.dfsx.lzcms.liveroom.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by liuwb on 2016/10/17.
 */
public abstract class AbsListFragment extends Fragment implements PullToRefreshListView.OnRefreshListener2 {

    protected Activity act;
    protected Context context;

    protected PullToRefreshListView pullToRefreshListView;
    protected ListView listView;
    protected LinearLayout emptyLayoutContainer;
    protected FrameLayout topListViewContainer;
    protected FrameLayout bottomListViewContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        return inflater.inflate(R.layout.frag_list_view, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topListViewContainer = (FrameLayout) view.
                findViewById(R.id.top_list_view_layout);
        bottomListViewContainer = (FrameLayout) view.
                findViewById(R.id.bottom_list_view_layout);
        pullToRefreshListView = (PullToRefreshListView) view.
                findViewById(R.id.frag_list_view);

        emptyLayoutContainer = (LinearLayout) view.
                findViewById(R.id.empty_layout);
        listView = pullToRefreshListView.getRefreshableView();

        pullToRefreshListView.setMode(getListViewMode());

        pullToRefreshListView.setOnRefreshListener(this);

        setListAdapter(listView);
        setEmptyLayout(emptyLayoutContainer);
        if (emptyLayoutContainer.getChildCount() > 0) {
            listView.setEmptyView(emptyLayoutContainer);
        }

        setTopView(topListViewContainer);
        setBottomView(bottomListViewContainer);
    }

    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }

    public abstract void setListAdapter(ListView listView);

    protected void setEmptyLayout(LinearLayout container) {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    protected void setTopView(FrameLayout topListViewContainer) {

    }

    protected void setBottomView(FrameLayout bottomListViewContainer) {

    }
}

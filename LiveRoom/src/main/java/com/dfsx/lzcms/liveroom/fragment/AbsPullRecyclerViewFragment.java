package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

public abstract class AbsPullRecyclerViewFragment extends Fragment implements PullToRefreshRecyclerView.OnRefreshListener2 {

    protected Context context;
    protected Activity act;
    protected PullToRefreshRecyclerView pullToRefreshRecyclerView;
    protected RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        return inflater.inflate(R.layout.abs_pull_recycler_view_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pullToRefreshRecyclerView = (PullToRefreshRecyclerView) view.findViewById(R.id.pull_recyclerView);
        recyclerView = pullToRefreshRecyclerView.getRefreshableView();

        pullToRefreshRecyclerView.setMode(getPullMode());
        pullToRefreshRecyclerView.setOnRefreshListener(this);

        setRecyclerLayoutManager(recyclerView);
        setAdapter();
    }

    protected void setRecyclerLayoutManager(RecyclerView recyclerView) {
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setAdapter() {
        recyclerView.setAdapter(getRecyclerViewAdapter());
    }

    protected PullToRefreshBase.Mode getPullMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

    public abstract BaseRecyclerViewAdapter getRecyclerViewAdapter();
}

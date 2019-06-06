package com.dfsx.ganzcms.app.fragment;

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
import com.dfsx.ganzcms.app.R;

public abstract class AbsRecyclerViewFragment extends Fragment {

    protected Context context;
    protected Activity act;
    protected RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        return inflater.inflate(R.layout.frag_base_recyclerview, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

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

    public abstract BaseRecyclerViewAdapter getRecyclerViewAdapter();
}

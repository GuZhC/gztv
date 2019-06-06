package com.dfsx.searchlibaray;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.searchlibaray.adapter.BaseListViewAdapter;
import com.dfsx.searchlibaray.businness.SearchHistoryManager;
import com.dfsx.searchlibaray.view.ClearHistoryPopwindow;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

public class HistoryFragment extends AbsListFragment {

    private HistoryAdapter adapter;
    private ClearHistoryPopwindow clearHistoryPopwindow;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
        super.setTopView(topListViewContainer);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.history_title_layout, null);
        view.findViewById(R.id.image_clear_history)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter != null && adapter.getData() != null) {
                            if (clearHistoryPopwindow == null) {
                                clearHistoryPopwindow = new ClearHistoryPopwindow(getActivity());
                                clearHistoryPopwindow.setOnEventClickListener(new ClearHistoryPopwindow.OnEventClickListener() {
                                    @Override
                                    public void onDelClick(View v, Object tag) {
                                        delHis();
                                    }
                                });
                            }
                            clearHistoryPopwindow.show(v);
                        }
                    }
                });
        topListViewContainer.addView(view);
    }

    private void delHis() {
        SearchHistoryManager.clearHistory();
        if (adapter != null) {
            if (adapter.getData() != null) {
                adapter.getData().clear();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayList<String> list = SearchHistoryManager.getHistory();
        adapter.update(list, false);
    }

    @Override
    public void setListAdapter(final ListView listView) {
        adapter = new HistoryAdapter(context);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - listView.getHeaderViewsCount();
                if (adapter.getData() != null && pos >= 0 && pos < adapter.getData().size()) {
                    onHisItemClick(adapter.getData().get(pos));
                }
            }
        });
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        super.setEmptyLayout(container);
        container.removeAllViews();
        EmptyView emptyView = EmptyView.newInstance(context);
        emptyView.loadOver();
        emptyView.setLoadOverView(R.layout.empty_history_layout);
        container.addView(emptyView);
    }

    private void onHisItemClick(String text) {
        Activity activity = getActivity();
        if (activity instanceof SearchActivity) {
            ((SearchActivity) activity).searchText(text);
        }
    }

    class HistoryAdapter extends BaseListViewAdapter<String> {

        public HistoryAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_history_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            TextView tv = holder.getView(R.id.item_text);
            tv.setText(list.get(position));
        }
    }
}

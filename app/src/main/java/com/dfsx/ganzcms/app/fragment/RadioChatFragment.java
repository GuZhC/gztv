package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewDataAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class RadioChatFragment extends CommChatFragment<CommendCmsEntry> {

    private RecycleAdapter mAdapter;
    View rootView;

    public static RadioChatFragment newInstance(int type) {
        RadioChatFragment tabFragment = new RadioChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullDown();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullUp();
            }
        });

        return rootView;
    }

    @Override
    public void iniData() {
        _chatSendBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        mAdapter = new RecycleAdapter();
        return mAdapter;
    }

    public void pullUp() {
        mRecyclerView.onRefreshComplete();
    }

    public void pullDown() {
        mRecyclerView.onRefreshComplete();
    }

    public class RecycleAdapter extends BaseRecyclerViewDataAdapter<CommendCmsEntry> {
        @Override
        public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(act).inflate(R.layout.community_replay_item, viewGroup, false);
            return new BaseRecyclerViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
            View body = holder.getView(R.id.replay_item_hor);
            TextView title = holder.getView(R.id.replay_title_value);
            title.setText(list.get(position).getText());
        }
    }
}

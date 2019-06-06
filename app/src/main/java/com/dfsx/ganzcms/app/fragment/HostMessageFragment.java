package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.common.adapter.BaseRecyclerViewHolder;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.HostPersonInfoActivity;
import com.dfsx.ganzcms.app.adapter.NewsCommendListRecyclerAdapter;
import com.dfsx.ganzcms.app.business.CMSContentCommentHelper;
import com.dfsx.ganzcms.app.business.ICommendReplaylister;
import com.dfsx.ganzcms.app.model.ContentComment;
import com.dfsx.ganzcms.app.model.IHostDetails;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.List;

public class HostMessageFragment extends AbsPullRecyclerViewFragment {

    public static final String KEY_HOST_CONTENT_ID = "HostMessageFragment_content_id";

    private static final int PAGE_SIZE = 10;

    private NewsCommendListRecyclerAdapter messageListAdapter;

    private EmptyView emptyView;

    private CMSContentCommentHelper commentHelper;

    private long contentId;

    private int page;

    private IHostDetails hostDetails;

    @Override
    public BaseRecyclerViewAdapter getRecyclerViewAdapter() {
        messageListAdapter = new NewsCommendListRecyclerAdapter();

        messageListAdapter.setCallBack(new ICommendReplaylister<ContentComment>() {
            @Override
            public void OnItemClick(View v, ContentComment object) {
                replayContentComment(object);
            }
        });

        messageListAdapter.setOnItemViewClickLisenter(new BaseRecyclerViewHolder.OnRecyclerItemViewClickListener() {
            @Override
            public void onItemViewClick(View v) {
                try {
                    ContentComment entry = (ContentComment) v.getTag();
                    String title = entry.getSub_comment_count() + "个回复";
                    if (entry.getSub_comment_count() > 0) {
                        DefaultFragmentActivity.start(context, CommendPageFragment.class.getName(),
                                entry.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return messageListAdapter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        contentId = getArguments() != null ? getArguments().getLong(KEY_HOST_CONTENT_ID) : 0;
        super.onViewCreated(view, savedInstanceState);
        commentHelper = new CMSContentCommentHelper(context);
        pullToRefreshRecyclerView.setPullRecyclerHelper((PullToRefreshRecyclerView.PullRecyclerHelper) act);
        setEmptyView();

        if (contentId != 0) {
            getCommentList(1);
        }
    }

    @Override
    protected PullToRefreshBase.Mode getPullMode() {
        return PullToRefreshBase.Mode.PULL_FROM_END;
    }

    public void setHostData(IHostDetails details) {
        this.hostDetails = details;
        if (contentId != details.getHostDetailsId()) {
            contentId = details.getHostDetailsId();
            getCommentList(1);
        }
    }

    private void replayContentComment(ContentComment comment) {
        if (act instanceof HostPersonInfoActivity) {
            ((HostPersonInfoActivity) act).replayContentComment(comment);
        }
    }

    private void getCommentList(int page) {
        this.page = page;
        commentHelper.getRootCommentList(contentId, page, PAGE_SIZE, 3, new DataRequest.DataCallback<List<ContentComment>>() {
            @Override
            public void onSuccess(boolean isAppend, List<ContentComment> data) {
                if (messageListAdapter != null) {
                    messageListAdapter.update(data, isAppend);
                }
                pullToRefreshRecyclerView.onRefreshComplete();
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                pullToRefreshRecyclerView.onRefreshComplete();
            }
        });
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        page++;
        getCommentList(page);
    }

    public void refreshData() {
        getCommentList(1);
    }

    private void setEmptyView() {
        emptyView = EmptyView.newInstance(context);
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("没有数据");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loadOver();
        if (messageListAdapter != null) {
            messageListAdapter.setEmptyView(emptyView);
        }
    }
}

package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * 一个竖直方向的recyclerView的封装类。让他类似于ListView, 有setOnItemClickListener的监听
 * <p/>
 * Created by liuwb on 2016/6/29.
 */
public class ListRecyclerView extends LinearLayout implements PullToRefreshBase.OnRefreshListener2 {
    protected Context context;

    protected PullToRefreshRecyclerView pullToRefreshRecyclerView;

    protected RecyclerView recyclerView;

    private RecyclerItemClickListener clickListener;

    private PullToRefreshBase.OnRefreshListener2 refreshListener;


    public ListRecyclerView(Context context) {
        this(context, null);
    }

    public ListRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        pullToRefreshRecyclerView = new PullToRefreshRecyclerView(context);
        recyclerView = pullToRefreshRecyclerView.getRefreshableView();
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        pullToRefreshRecyclerView.setLayoutParams(params);

        pullToRefreshRecyclerView.setOnRefreshListener(this);

        recyclerView.setLayoutManager(getLayoutManager());

        addView(pullToRefreshRecyclerView);

        clickListener = new RecyclerItemClickListener(context);
        recyclerView.addOnItemTouchListener(clickListener);

        setRefreshMode(PullToRefreshBase.Mode.DISABLED);
    }

    protected LinearLayoutManager getLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        return linearLayoutManager;
    }

    /**
     * 设置item的点击事件
     *
     * @param l
     */
    public void setOnItemClickListener(RecyclerItemClickListener.OnItemClickListener l) {
        clickListener.setOnItemClickListener(l);
    }

    public void setOnRecyclerViewClickListener(RecyclerItemClickListener.OnClickListener l) {
        clickListener.setOnClickListener(l);
    }

    public void setOnPullEventListener(PullToRefreshBase.OnPullEventListener<RecyclerView> listener) {
        pullToRefreshRecyclerView.setOnPullEventListener(listener);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public boolean isFirstItemVisible() {
        if (recyclerView != null) {
            final RecyclerView.Adapter adapter = recyclerView.getAdapter();
            final LinearLayoutManager mLayoutmanager = (LinearLayoutManager) recyclerView.getLayoutManager();


            if (null == adapter || adapter.getItemCount() == 0) {
                return true;
            } else {
                /**
                 * This check should really just be:
                 * mRootView.getFirstVisiblePosition() == 0, but PtRListView
                 * internally use a HeaderView which messes the positions up. For
                 * now we'll just add one to account for it and rely on the inner
                 * condition which checks getTop().
                 */

                int[] into = {0, 0};
                if (mLayoutmanager != null)
                    into[0] = mLayoutmanager.findFirstVisibleItemPosition();
                if (into.length > 0 && into.length > 0 && into[0] <= 1) {
                    final View firstVisibleChild = recyclerView.getChildAt(0);
                    if (firstVisibleChild != null) {
                        return firstVisibleChild.getTop() >= recyclerView.getTop();
                    }
                }
            }
        }

        return false;
    }

    public boolean isLastItemVisiable() {
        if (recyclerView != null) {
            final RecyclerView.Adapter adapter = recyclerView.getAdapter();
            final LinearLayoutManager mLayoutmanager = (LinearLayoutManager) recyclerView.getLayoutManager();


            if (null == adapter || adapter.getItemCount() == 0) {
                return true;
            } else {
                final int lastItemPosition = adapter.getItemCount() - 1;
                int[] into = {0, 0};
                if (mLayoutmanager != null)
                    into[0] = mLayoutmanager.findLastVisibleItemPosition();
                if (into[0] >= lastItemPosition - 1) {
                    int dex = into[0] - mLayoutmanager.findFirstVisibleItemPosition();
                    final View lastVisibleChild = recyclerView.getChildAt(dex);
                    if (lastVisibleChild != null) {
                        return lastVisibleChild.getBottom() <= recyclerView.getBottom();
                    }
                }
            }
        }

        return false;
    }

    public void setRefreshMode(PullToRefreshBase.Mode mode) {
        pullToRefreshRecyclerView.setMode(mode);
    }

    public void onRefreshComplete() {
        pullToRefreshRecyclerView.onRefreshComplete();
    }

    public void setOnRefreshListener(PullToRefreshBase.OnRefreshListener2 l) {
        this.refreshListener = l;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        if (refreshListener != null) {
            refreshListener.onPullDownToRefresh(refreshView);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (refreshListener != null) {
            refreshListener.onPullUpToRefresh(refreshView);
        }
    }
}

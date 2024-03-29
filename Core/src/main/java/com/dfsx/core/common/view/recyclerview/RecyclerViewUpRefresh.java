package com.dfsx.core.common.view.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 *  by heyang 2017/11/30
 */
public class RecyclerViewUpRefresh extends RecyclerView {

    private Context mContext;

    private LoadMoreListener loadMoreListener;
    //是否可加载更多
    private boolean canloadMore = true;

    private Adapter mAdapter;

    private Adapter mFooterAdapter;

    private ListView list;

    public boolean isLoadingData = false;
    //加载更多布局
    private LoadingMoreFooter loadingMoreFooter;

    public RecyclerViewUpRefresh(Context context) {
        this(context, null);
    }

    public RecyclerViewUpRefresh(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewUpRefresh(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LoadingMoreFooter footView = new LoadingMoreFooter(mContext);
        addFootView(footView);
        footView.setGone();
    }


    //点击监听
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (mFooterAdapter != null && mFooterAdapter instanceof FooterAdapter) {
            ((FooterAdapter) mFooterAdapter).setOnItemClickListener(onItemClickListener);
        }
    }


    //长按监听
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        if (mFooterAdapter != null && mFooterAdapter instanceof FooterAdapter) {
            ((FooterAdapter) mFooterAdapter).setOnItemLongClickListener(listener);
        }
    }

    /**
     * 底部加载更多视图
     *
     * @param view
     */
    public void addFootView(LoadingMoreFooter view) {
        loadingMoreFooter = view;
    }

    //设置底部加载中效果
    public void setFootLoadingView(View view) {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.addFootLoadingView(view);
        }
    }

    //设置底部到底了布局
    public void setFootEndView(View view) {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.addFootEndView(view);
        }
    }

    //下拉刷新后初始化底部状态
    public void refreshComplete() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setGone();
        }
        isLoadingData = false;
    }

    public void loadMoreComplete() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setGone();
        }
        isLoadingData = false;
    }


    //到底了
    public void loadMoreEnd() {
        if (loadingMoreFooter != null) {
            loadingMoreFooter.setEnd();
        }
    }

    //设置是否可加载更多
    public void setCanloadMore(boolean flag) {
        canloadMore = flag;
    }

    //设置加载更多监听
    public void setLoadMoreListener(LoadMoreListener listener) {
        loadMoreListener = listener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mFooterAdapter = new FooterAdapter(this,loadingMoreFooter, adapter);
        super.setAdapter(mFooterAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && loadMoreListener != null && !isLoadingData && canloadMore) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = last(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (flag && layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1) {
                if (loadingMoreFooter != null) {
                    loadingMoreFooter.setVisible();
                }
                isLoadingData = true;
                loadMoreListener.onLoadMore();
            }
        }
    }

//    boolean loadingMoreEnabled = true;
//    设置不给上拉加载
//    public void setLoadingMoreEnabled(boolean loadingMoreEnabled) {
//        this.loadingMoreEnabled = loadingMoreEnabled;
//    }

    public boolean flag;


    float startY = 0;
    float moveY = 0;


    /**
     *
     * @param e
     * @return  处理数据不满一屏的手势判断
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = e.getRawY();
                if (moveY > startY)
                    flag = false;
                else
                    flag = true;
                startY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(e);
    }



    //取到最后的一个节点
    private int last(int[] lastPositions) {
        int last = lastPositions[0];
        for (int value : lastPositions) {
            if (value > last) {
                last = value;
            }
        }
        return last;
    }


    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mFooterAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mFooterAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mFooterAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mFooterAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

}

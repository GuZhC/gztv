package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.dfsx.core.common.adapter.BaseRecyclerViewAdapter;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ICommendReplaylister;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.lzcms.liveroom.view.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

public abstract class CommChatFragment<T> extends Fragment {
    public static final String TYPE = "title";
    protected int _typeId = 0;
    //    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected PullToRefreshRecyclerView mRecyclerView;
    protected List<T> mDatas = new ArrayList<T>();
    private BaseRecyclerViewAdapter mAdapter;
    protected Activity act;
    ImageView _chatSendBtn;
    NewsDatailHelper _newsHelper;
    ICommendReplaylister<T> onItemlick;
    View rootView;
    boolean isRightBtnVisiable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_chat_bottom, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        act = getActivity();
        if (getArguments() != null) {
            _typeId = getArguments().getInt(TYPE);
        }
        _newsHelper = new NewsDatailHelper(getContext());

        initView(view);
        iniData();
        if (savedInstanceState != null) {
            resetData(savedInstanceState);
        }
//        initAction();
//        initPullRefresh();
//        initLoadMoreListener();
    }

//    public  void  initAction(){}

    public void setRightBtnVisiable(boolean rightBtnVisiable) {
        isRightBtnVisiable = rightBtnVisiable;
    }

    public void setOnLtclick(ICommendReplaylister<T> onLtclick) {
        this.onItemlick = onLtclick;
    }

    public void initView(View view) {
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (PullToRefreshRecyclerView) view
                .findViewById(R.id.id_stickynavlayout_innerscrollview);
        _chatSendBtn = (ImageView) view.findViewById(R.id.send_btn);
//        _chatSendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                show(rootView);
//            }
//        });
        mRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        mRecyclerView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));

        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
//        mRecyclerView.getRefreshableView().setHasFixedSize(true);

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

//        创建并设置Adapter
        mAdapter = getRecyclerViewAdapter();
        mRecyclerView.getRefreshableView().setAdapter(mAdapter);
    }

    public abstract void iniData();

    public abstract void pullDown();

    public abstract void pullUp();

    public void resetData(Bundle outState) {
    }

    public void  saveOutst()
    {

    }

    public abstract BaseRecyclerViewAdapter getRecyclerViewAdapter();

//    protected abstract void initPullRefresh();

//    protected abstract void initLoadMoreListener();

//    public  abstract   void iniData() {
//        for (int i = 0; i < 10; i++) {
//            mDatas.add(mTitle + " -> " + i);
//        }
//        mAdapter.update(mDatas, false);
//    }

    public void show(View v) {
        _newsHelper.showCommendDialog(v, -1,-1, new NewsDatailHelper.ICommendDialogLbtnlister() {
            @Override
            public boolean onParams(long id, long ref_id,String context) {
                _newsHelper.pubCommnedReplay(-1, id, context, new DataRequest.DataCallback() {
                    @Override
                    public void onSuccess(boolean isAppend, Object data) {

                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
                return true;
            }
        });

    }
}

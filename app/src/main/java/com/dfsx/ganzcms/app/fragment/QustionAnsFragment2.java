package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataFileCacheManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.QuestionInfoAct;
import com.dfsx.ganzcms.app.business.TopicListManager;
import com.dfsx.ganzcms.app.business.TopicalApi;
import com.dfsx.ganzcms.app.model.SocirtyNewsChannel;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyang on 2016/9/16.
 */
public class QustionAnsFragment2 extends AbsListFragment implements AdapterView.OnItemClickListener {

    private ListImagesAdapter adapter;
    private int offset;
    private DataFileCacheManager<ArrayList<SocirtyNewsChannel>> dataFileCacheManager;
    private EmptyView mEmptyView;
    TopicListManager dataRequester;
    private long mCLoumnType = -1;
    private TopicalApi mTopicalApi = null;
    private LinearLayout topView;

    public static QustionAnsFragment2 newInstance(long type) {
        Bundle bundle = new Bundle();
        bundle.putLong("type", type);
        QustionAnsFragment2 fragment = new QustionAnsFragment2();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCLoumnType = bundle.getLong("type");
        }
        mEmptyView.loadOver();
        pullToRefreshListView.onRefreshComplete();
        mTopicalApi = new TopicalApi(getActivity());
        dataRequester = new TopicListManager(getActivity(), 11 + "", mCLoumnType);
        dataRequester.setCallback(
                new DataRequest.DataCallback<ArrayList<TopicalEntry>>() {
                    @Override
                    public void onSuccess(final boolean isAppend, ArrayList<TopicalEntry> topicalies) {
                        mEmptyView.loadOver();
                        pullToRefreshListView.onRefreshComplete();
                        if (topicalies == null || topicalies.isEmpty()) {
                            return;
                        }
                        Observable.from(topicalies)
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<TopicalEntry, TopicalEntry>() {
                                    @Override
                                    public TopicalEntry call(TopicalEntry topicalEntry) {
                                        TopicalEntry tag = mTopicalApi.getTopicTopicalInfo(topicalEntry);
                                        return tag;
                                    }
                                })
                                .toList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<List<TopicalEntry>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(List<TopicalEntry> data) {
                                        if (data != null) {
                                            adapter.update(data, isAppend);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                        mEmptyView.loadOver();
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
        getData(1);
    }

    @Override
    public void setListAdapter(final ListView listView) {
        if (adapter == null) {
            adapter = new ListImagesAdapter(getActivity());
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                try {
                    boolean isLeagle = position >= 0 && position < adapter.getCount();
                    if (isLeagle) {
                        TopicalEntry channel = adapter.getData().get(position);
                        int a = 0;
                        goDetail(channel, position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void goDetail(TopicalEntry channel, int listPos) {
        Intent intent = new Intent(getActivity(), QuestionInfoAct.class);
        intent.putExtra("tid", channel.getId());
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        offset = 0;
        getData(offset);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        offset++;
        getData(offset);
    }

    @Override
    protected void setTopView(FrameLayout topListViewContainer) {
        super.setTopView(topListViewContainer);
        topView = new LinearLayout(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        topView.setLayoutParams(p);
        topListViewContainer.addView(topView, p);
        View topLayout = LayoutInflater.from(context).
                inflate(R.layout.news_answer_header_layout, null);
        topView.addView(topLayout, p);
        View question = (View) topLayout.findViewById(R.id.quest_view);
        View answer = (View) topLayout.findViewById(R.id.anwer_view);
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultFragmentActivity.start(getActivity(), MyquswerTabFragment.class.getName());
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhiteTopBarActivity.startAct(getActivity(), MyAnswerFragment.class.getName(), "我来回答");
            }
        });
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        mEmptyView = new EmptyView(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mEmptyView.setLayoutParams(p);
        container.addView(mEmptyView);
        View emptyLayout = LayoutInflater.from(context).
                inflate(R.layout.no_video_layout, null);
        TextView ttx = (TextView) emptyLayout.findViewById(R.id.my_video_xt);
        ttx.setText(R.string.request_netdata_result);
        mEmptyView.setLoadOverView(emptyLayout);
        mEmptyView.loading();
    }

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    protected void getData(int offset) {
        mEmptyView.loading();
        dataRequester.start(mCLoumnType, false, offset);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - listView.getHeaderViewsCount();
        try {
            boolean isLeagle = position >= 0 && position < adapter.getCount();
            if (isLeagle) {
                TopicalEntry channel = adapter.getData().get(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ListImagesAdapter extends BaseListViewAdapter<TopicalEntry> {

        public ListImagesAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.news_answer_list_item;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView itemImage = holder.getView(R.id.news_news_list_item_image);
            TextView itemTiltle = holder.getView(R.id.news_list_item_title);
            TextView commend = holder.getView(R.id.news_list_item_answernum_tx);
            TextView itemCreateTime = holder.getView(R.id.item_create_time);
            TopicalEntry channel = list.get(position);
//            GlideImgManager.getInstance().showImg(context, itemImage, channel.ge);
            itemTiltle.setText(channel.getContent());
        }
    }

}

package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ColumnContentHelper;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题列表页面
 * <p>
 * 优先使用传入的栏目Id, 没有栏目id再通过Key获取栏目数据.栏目可以需要手动配置 specialColumnKey
 */
public class SpecialTopicListFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView> {

    public static final String KEY_INIT_COLUMN_ID = "SpecialTopicListFragment_column_id";

    private SpecialTopicAdapter adapter;
    private int curPage = 1;
    private ColumnContentHelper columnHelper;


    private long columnId;

    private NewsDatailHelper datailHelper;

    private String specialColumnKey = "zhuanti";

    private PullToRefreshListView pullListview;
    private ListView listView;
    private View footerView;

    public static SpecialTopicListFragment newInstance(long columnId) {
        SpecialTopicListFragment fragment = new SpecialTopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_INIT_COLUMN_ID, columnId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        try {
            columnId = getArguments().getLong(KEY_INIT_COLUMN_ID, 0L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pullListview = (PullToRefreshListView) view.findViewById(R.id.news_scroll_layout);
        pullListview.setOnRefreshListener(this);
        pullListview.setMode(PullToRefreshBase.Mode.BOTH);
        listView = ((ListView) pullListview.getRefreshableView());
        listView.setDividerHeight(0);
        adapter = new SpecialTopicAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int pos = position - listView.getHeaderViewsCount();
                    SpecialTopicItem item = (SpecialTopicItem) adapter.getData().get(pos);
                    String url = App.getInstance().getContentShareUrl() + item.getSpecialId();
                    datailHelper.preDetail(item.getSpecialId(), 7, item.getContent().getTitle(), item.getSpecialThumbImage(),
                            item.getContent().getCommentCount(), url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        columnHelper = new ColumnContentHelper(getActivity());
        datailHelper = new NewsDatailHelper(getActivity());
        readCacheAndUpdate();
        getData(1);
    }



    private void getData(int page) {
        curPage = page;
        Observable.just(page)
                .observeOn(Schedulers.io())
                .concatMap(new Func1<Integer, Observable<Bundle>>() {
                    @Override
                    public Observable<Bundle> call(final Integer integer) {
                        if (columnId == 0) {
                            Observable.OnSubscribe<Bundle> subscribe = new Observable.OnSubscribe<Bundle>() {
                                @Override
                                public void call(final Subscriber<? super Bundle> subscriber) {
                                    ColumnBasicListManager.getInstance().queryColumnEntry(specialColumnKey, new Action1<ColumnCmsEntry>() {
                                        @Override
                                        public void call(ColumnCmsEntry columnCmsEntry) {
                                            long mId = 0L;
                                            if (columnCmsEntry != null) {
                                                mId = columnCmsEntry.getId();
                                                columnId = mId;
                                            }
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("page", integer);
                                            bundle.putLong("this_column_id", mId);
                                            subscriber.onNext(bundle);
                                        }
                                    });
                                }
                            };
                            return Observable.create(subscribe);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putInt("page", integer);
                            bundle.putLong("this_column_id", columnId);
                            return Observable.just(bundle);
                        }
                    }
                })
                .flatMap(new Func1<Bundle, Observable<List<ColumnContentListItem>>>() {
                    @Override
                    public Observable<List<ColumnContentListItem>> call(Bundle bundle) {
                        int page = bundle.getInt("page", 1);
                        long mId = bundle.getLong("this_column_id", 0L);
                        List<ColumnContentListItem> list = columnHelper.
                                getColumnContentList(mId + "", page, 20);
                        return Observable.just(list);
                    }
                })
                .map(new Func1<List<ColumnContentListItem>, List<SpecialTopicItem>>() {

                    BaseWrapContent.ICreateWrapContent<SpecialTopicItem> creator;

                    private BaseWrapContent.ICreateWrapContent<SpecialTopicItem> getCreator() {
                        if (creator == null) {
                            creator = new BaseWrapContent.ICreateWrapContent<SpecialTopicItem>() {
                                @Override
                                public SpecialTopicItem createNewsInstance() {
                                    return new SpecialTopicItem();
                                }
                            };
                        }
                        return creator;
                    }

                    @Override
                    public List<SpecialTopicItem> call(List<ColumnContentListItem> columnContentListItems) {
                        return BaseWrapContent.toWrapContentList(columnContentListItems, getCreator());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SpecialTopicObserver(page));
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData(1);
        curPage = 1;
        if (listView.getFooterViewsCount() > 0){
            pullListview.setMode(PullToRefreshBase.Mode.BOTH);
            listView.removeFooterView(footerView);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData(++curPage);
    }




    class SpecialTopicObserver implements Observer<List<SpecialTopicItem>> {
        private int page;

        public SpecialTopicObserver(int page) {
            this.page = page;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            pullListview.onRefreshComplete();
            e.printStackTrace();
        }

        @Override
        public void onNext(List<SpecialTopicItem> specialTopicItems) {
            updateAdapter(specialTopicItems, page > 1);
            if (page == 1) {
                cacheFistPage(specialTopicItems);
            }
            pullListview.onRefreshComplete();
            showFooterView(specialTopicItems);
        }
    }

    private void showFooterView(List<SpecialTopicItem> specialTopicItems){
        if (specialTopicItems == null || specialTopicItems.size() == 0){
            if (footerView == null){
                footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_footer_no_more,null);
            }
            if (listView.getFooterViewsCount() > 0){
                listView.removeFooterView(footerView);
            }
            listView.addFooterView(footerView);
            pullListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

    private void readCacheAndUpdate() {
        try {
            Object object = FileUtil.getFileByAccountId(getActivity(), "dazhou_special_data.txt", "all");
            List<SpecialTopicItem> specialTopicItems = (List<SpecialTopicItem>) object;
            updateAdapter(specialTopicItems, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cacheFistPage(List<SpecialTopicItem> specialTopicItems) {
        FileUtil.saveFileByAccountId(getActivity(),
                "dazhou_special_data.txt", "all", specialTopicItems);
    }

    private void updateAdapter(List<SpecialTopicItem> specialTopicItems, boolean isAdd) {
        List<ISpecialTopic> list = new ArrayList<>();
        if (specialTopicItems != null) {
            list.addAll(specialTopicItems);
        }
        adapter.update(list, isAdd);
    }

    class SpecialTopicAdapter extends BaseListViewAdapter<ISpecialTopic> {

        public SpecialTopicAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_special_topic_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView imageView = holder.getView(R.id.item_thumb_iamge);
            TextView tvTitle = holder.getView(R.id.item_title_tv);
            TextView tvTime = holder.getView(R.id.item_time_tv);
            TextView tvSeeNum = holder.getView(R.id.item_see_num_tv);
            ISpecialTopic spt = list.get(position);

            GlideImgManager.getInstance().showImg(context, imageView, spt.getSpecialThumbImage());
            tvTitle.setText(spt.getSpecialTitle());
            tvTime.setText(StringUtil.getTimeAgoText(spt.getSpecialTime()));
            tvSeeNum.setText(spt.getSpecialSeeNum());
        }
    }
}

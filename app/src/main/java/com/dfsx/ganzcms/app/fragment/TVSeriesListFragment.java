package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.TVSeriesDetailsActivity;
import com.dfsx.ganzcms.app.business.TVSeriesHelper;
import com.dfsx.ganzcms.app.model.ITVSeries;
import com.dfsx.ganzcms.app.model.TVSeriesEntry;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 电视连续剧列表页面
 */
public class TVSeriesListFragment extends AbsListFragment {
    public static final String KEY_CONTENT_ID = "TVSeriesListFragment_dfsx.cms.content_id";

    private TVSeriesAdapter adapter;

    private EmptyView emptyView;

    private TVSeriesHelper dataHelper;

    private int curPage;
    private static final int PAGE_SIZE = 20;

    private long contentId;

    private long testContentId = 104436;


    public static TVSeriesListFragment newInstance(long contentId) {
        TVSeriesListFragment fragment = new TVSeriesListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_CONTENT_ID, contentId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            contentId = getArguments().getLong(KEY_CONTENT_ID, testContentId);
        }
        super.onViewCreated(view, savedInstanceState);
        dataHelper = new TVSeriesHelper(context);
        getData(1);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int pos = position - listView.getHeaderViewsCount();
                    Intent intent = new Intent(context, TVSeriesDetailsActivity.class);
                    intent.putExtra("index", adapter.getData().get(pos).getId());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }

    private void getData(int page) {
        this.curPage = page;
        dataHelper.getTVSeriesData(contentId, page, PAGE_SIZE, new DataRequest.DataCallback<List<TVSeriesEntry>>() {
            @Override
            public void onSuccess(boolean isAppend, List<TVSeriesEntry> data) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
                try {
                    if (adapter != null) {
                        List<ITVSeries> list = new ArrayList<>();
                        for (TVSeriesEntry entry : data) {
                            list.add(entry);
                        }
                        adapter.update(list, isAppend);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ApiException e) {
                pullToRefreshListView.onRefreshComplete();
                emptyView.loadOver();
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        getData(++curPage);
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new TVSeriesAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("暂无数据");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }

    class TVSeriesAdapter extends BaseListViewAdapter<ITVSeries> {
        public TVSeriesAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_tv_series_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            ImageView tvImage = holder.getView(R.id.item_tv_image);
            TextView tvNameText = holder.getView(R.id.item_tv_name);
            TextView tvSizeText = holder.getView(R.id.item_tv_size_text);
            TextView tvDescText = holder.getView(R.id.item_tv_desc_text);

            ITVSeries series = list.get(position);
            Glide.with(getActivity())
                    .load(series.getTVImage())
                    .asBitmap()
                    .placeholder(R.drawable.glide_default_image)
                    .error(R.drawable.glide_default_image)
                    .centerCrop()
                    .into(tvImage);
            tvNameText.setText(series.getTVName());
            String sizeStr = context.getResources().getString(R.string.tv_series_num);
            tvSizeText.setText(String.format(sizeStr, series.getTVSize()));
            tvDescText.setText(series.getTVDesc());
        }
    }
}

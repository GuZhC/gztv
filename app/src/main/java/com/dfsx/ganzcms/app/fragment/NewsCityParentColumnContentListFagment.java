package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.CityGridDataHelper;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.INewsGridData;
import com.dfsx.ganzcms.app.model.NewsGridItem;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.List;

/**
 * 市州矩阵 根节点栏目的内容列表
 */
public class NewsCityParentColumnContentListFagment extends AbsListFragment {

    private CityColumnContentListAdapter adapter;

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new CityColumnContentListAdapter(context);
        listView.setAdapter(adapter);
    }

    private ColumnCmsEntry columnEntry;

    private CityGridDataHelper dataHelper;
    private int curPage;
    private long columnId;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            columnEntry = (ColumnCmsEntry) getArguments().getSerializable(NewsTwoListFragment.KEY_CITY_COLUMN_DATA);
            columnId = columnEntry != null ? columnEntry.getId() : 0L;
        }
        super.onViewCreated(view, savedInstanceState);
        dataHelper = new CityGridDataHelper(context);
        getData(1);
    }

    private void getData(int page) {
        this.curPage = page;
        dataHelper.getColumnContentList(columnId, page, 20, new DataRequest.DataCallback<List<NewsGridItem>>() {
            @Override
            public void onSuccess(boolean isAppend, List<NewsGridItem> data) {
                adapter.update(data, isAppend);
                emptyView.loadOver();
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onFail(ApiException e) {
                emptyView.loadOver();
                pullToRefreshListView.onRefreshComplete();
            }
        });
    }

    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.BOTH;
    }


    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        super.onPullUpToRefresh(refreshView);
        getData(++curPage);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData(1);
    }

    private EmptyView emptyView;

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("没有数据");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }


    class CityColumnContentListAdapter extends BaseGridListAdapter<INewsGridData> {

        public CityColumnContentListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        protected int getHDLeftDivideLineWidth() {
            return PixelUtil.dp2px(context, 5);
        }

        @Override
        protected int getHDLeftDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDRightDivideLineWidth() {
            return PixelUtil.dp2px(context, 5);
        }

        @Override
        protected int getHDRightDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDivideLineWidth() {
            return PixelUtil.dp2px(context, 5);
        }

        @Override
        protected int getHDivideLineRes() {
            return R.color.white;
        }

        @Override
        public int getGridItemLayoutId() {
            return R.layout.adapter_news_grid_item_layout;
        }

        @Override
        public void setGridItemViewData(BaseViewHodler hodler, INewsGridData data) {
            ImageView imageView = hodler.getView(R.id.item_news_image);
            TextView newsTitleText = hodler.getView(R.id.item_news_title_text);
            TextView newsColumnNameText = hodler.getView(R.id.item_news_column_name_text);
            TextView newsTimeText = hodler.getView(R.id.item_news_time_text);
            View gridContentView = hodler.getView(R.id.news_grid_item_content_view);

            String columnName = "";
            try {
                if (data instanceof NewsGridItem) {
                    columnName = ((NewsGridItem) data).getData().getColumn_name();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            newsColumnNameText.setText(columnName);
            gridContentView.setVisibility(data.getId() == 0 ? View.INVISIBLE : View.VISIBLE);
            gridContentView.setTag(data);
            Glide.with(NewsCityParentColumnContentListFagment.this)
                    .load(data.getImagePath())
                    .asBitmap()
                    .error(R.drawable.glide_default_image)
                    .placeholder(R.drawable.glide_default_image)
                    .centerCrop()
                    .into(imageView);
            newsTitleText.setText(data.getShowTitle());
            newsTimeText.setText(StringUtil.getTimeAgoText(data.getShowTime()));
            gridContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    INewsGridData gridData = (INewsGridData) v.getTag();
                    if (gridData != null && gridData.getId() != 0) {
                        NewsGridItem gridItem = (NewsGridItem) gridData;
                        new NewsDatailHelper(context).goDetail(gridItem.getData());
                    }
                }
            });
        }
    }
}

package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.GanZiTopBarActivity;
import com.dfsx.ganzcms.app.business.CityGridDataHelper;
import com.dfsx.ganzcms.app.business.NewsDatailHelper;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.INewsGridData;
import com.dfsx.ganzcms.app.model.NewsGridItem;
import com.dfsx.ganzcms.app.model.NewsGridMenu;
import com.dfsx.lzcms.liveroom.adapter.BaseGridListAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * 甘孜的市政矩阵类型的新闻样式
 */
public class NewsGridMenuListFragment extends AbsListFragment {

    public static final String KEY_CITY_MENU_DATA = "NewsGridMenuListFragment_menu_data";

    private NewsGridMenuListAdapter adapter;

    private ColumnCmsEntry columnEntry;

    private CityGridDataHelper dataHelper;
    private NewsDatailHelper newsDatailHelper;

    public static NewsGridMenuListFragment newInstance(ColumnCmsEntry columnMenu) {
        NewsGridMenuListFragment gridMenuListFragment = new NewsGridMenuListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewsGridMenuListFragment.KEY_CITY_MENU_DATA, columnMenu);
        gridMenuListFragment.setArguments(bundle);
        return gridMenuListFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            columnEntry = (ColumnCmsEntry) getArguments().getSerializable(KEY_CITY_MENU_DATA);
        }
        super.onViewCreated(view, savedInstanceState);
        dataHelper = new CityGridDataHelper(context);
        newsDatailHelper = new NewsDatailHelper(context);
        listView.setDivider(context.getResources()
                .getDrawable(R.drawable.shape_news_grid_devide));
        listView.setDividerHeight(PixelUtil.dp2px(context, 4));
        getData();
    }

    private void getData() {
        List<NewsGridMenu> menulist = dataHelper.getNewsGridMenu(columnEntry);
        if (menulist != null) {
            dataHelper.getColumn2DataList(columnEntry, new GridDataAction(menulist));
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        super.onPullDownToRefresh(refreshView);
        getData();
    }

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new NewsGridMenuListAdapter(context);
        listView.setAdapter(adapter);
    }

    private void setTestData() {
        ArrayList<TestClass> menulist = new ArrayList<>();
        ArrayList<TestClass> list = new ArrayList<>();
        int count = 6;
        for (int i = 0; i < count; i++) {
            menulist.add(new TestClass());
            for (int j = 0; j < 2; j++) {
                list.add(new TestClass());
            }
        }
        adapter.update(list, menulist, false);
    }

    class GridDataAction implements Action1<List<NewsGridItem>> {

        private List<NewsGridMenu> menus;

        public GridDataAction(List<NewsGridMenu> menus) {
            this.menus = menus;
        }

        @Override
        public void call(List<NewsGridItem> newsGridItems) {
            adapter.update(newsGridItems, menus, false);
            pullToRefreshListView.onRefreshComplete();
        }
    }

    class TestClass implements INewsGridData {

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public String getImagePath() {
            return "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3902812836,1542379313&fm=175&s=1474518000410AFCC4C4A19403000081&w=640&h=409&img.JPEG";
        }

        @Override
        public String getShowTitle() {
            return "test";
        }

        @Override
        public long getShowTime() {
            return 1515034754;
        }
    }

    class NewsGridMenuListAdapter extends BaseGridListAdapter<INewsGridData> {

        public NewsGridMenuListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getGridItemLayoutId() {
            return R.layout.adapter_news_grid_item_layout;
        }

        @Override
        protected int getGridMenuWidth() {
            return PixelUtil.dp2px(context, 89);
        }

        @Override
        protected int getGridMenuLayoutId() {
            return R.layout.adapter_news_grid_menu_layout;
        }

        @Override
        protected int getHDLeftDivideLineWidth() {
            return PixelUtil.dp2px(context, 7);
        }

        @Override
        protected int getHDLeftDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDRightDivideLineWidth() {
            return PixelUtil.dp2px(context, 7);
        }

        @Override
        protected int getHDRightDivideLineRes() {
            return R.color.white;
        }

        @Override
        protected int getHDivideLineWidth() {
            return PixelUtil.dp2px(context, 14);
        }

        @Override
        protected int getHDivideLineRes() {
            return R.color.white;
        }

        @Override
        public void setGridItemViewData(BaseViewHodler hodler, INewsGridData data) {
            ImageView imageView = hodler.getView(R.id.item_news_image);
            TextView newsTitleText = hodler.getView(R.id.item_news_title_text);
            TextView newsTimeText = hodler.getView(R.id.item_news_time_text);
            View gridContentView = hodler.getView(R.id.news_grid_item_content_view);
            gridContentView.setVisibility(data.getId() == 0 ? View.INVISIBLE : View.VISIBLE);
            gridContentView.setTag(data);
            GlideImgManager.getInstance().showImg(context, imageView, data.getImagePath());
            newsTitleText.setText(data.getShowTitle());
            newsTimeText.setText(StringUtil.getTimeAgoText(data.getShowTime()));
            gridContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    INewsGridData gridData = (INewsGridData) v.getTag();
                    if (gridData != null && gridData.getId() != 0) {
                        NewsGridItem gridItem = (NewsGridItem) gridData;
                        newsDatailHelper.goDetail(gridItem.getData());
                    }
                }
            });
        }

        @Override
        protected void setGridMenuItemData(BaseViewHodler lineHolder, BaseViewHodler menuHolder, int postion) {
            View menuView = menuHolder.getView(R.id.item_menu_view);
            ImageView imageView = menuHolder.getView(R.id.news_menu_logo);
            TextView menuTitle = menuHolder.getView(R.id.text_menu);
            INewsGridData data = menuList.get(postion);
            GlideImgManager.getInstance().showImg(context, imageView, data.getImagePath());
            menuTitle.setText(data.getShowTitle());
            menuView.setTag(data);
            menuView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    INewsGridData menuData = (INewsGridData) v.getTag();
                    NewsGridMenu gridMenu = (NewsGridMenu) menuData;
                    ColumnCmsEntry entry = gridMenu.getData();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", menuData.getId());
                    bundle.putString("type", entry.getKey());
                    bundle.putLong("slideId", entry.getSliderId());
                    bundle.putSerializable(CityNewsListFragment.KEY_GRID_MENU, gridMenu);
                    intent.putExtras(bundle);
                    GanZiTopBarActivity.start(context,
                            CityNewsListFragment.class.getName(), intent);
                }
            });
        }
    }
}

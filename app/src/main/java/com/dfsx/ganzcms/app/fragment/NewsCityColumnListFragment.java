package com.dfsx.ganzcms.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.GanZiTopBarActivity;
import com.dfsx.ganzcms.app.model.ColumnCmsEntry;
import com.dfsx.ganzcms.app.model.INewsGridData;
import com.dfsx.ganzcms.app.model.NewsGridMenu;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.fragment.AbsListFragment;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

/**
 * 市州矩阵模式栏目列表
 */
public class NewsCityColumnListFragment extends AbsListFragment {

    private CityChildColumnAdapter adapter;

    @Override
    public void setListAdapter(ListView listView) {
        adapter = new CityChildColumnAdapter(context);
        listView.setAdapter(adapter);
    }

    private ColumnCmsEntry columnEntry;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            columnEntry = (ColumnCmsEntry) getArguments().getSerializable(NewsTwoListFragment.KEY_CITY_COLUMN_DATA);
        }
        super.onViewCreated(view, savedInstanceState);

        ArrayList<INewsGridData> list = new ArrayList<>();

        if (columnEntry != null && columnEntry.getDlist() != null) {
            for (ColumnCmsEntry cmsEntry : columnEntry.getDlist()) {
                NewsGridMenu menu = new NewsGridMenu(cmsEntry);
                list.add(menu);
            }
        }

        adapter.update(list, false);
        emptyView.loadOver();
    }

    private EmptyView emptyView;

    @Override
    protected void setEmptyLayout(LinearLayout container) {
        emptyView = EmptyView.newInstance(context);
        TextView tv = new TextView(context);
        tv.setText("没有市县子栏目信息");
        tv.setTextColor(context.getResources().getColor(R.color.black_36));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        emptyView.setLoadOverView(tv);
        emptyView.loading();
        container.addView(emptyView);
    }


    @Override
    protected PullToRefreshBase.Mode getListViewMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    class CityChildColumnAdapter extends BaseListViewAdapter<INewsGridData> {

        public CityChildColumnAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.adapter_news_grid_menu_layout;
        }

        @Override
        public void setItemViewData(BaseViewHodler holder, int position) {
            View menuView = holder.getView(R.id.item_menu_view);
            ImageView imageView = holder.getView(R.id.news_menu_logo);
            TextView menuTitle = holder.getView(R.id.text_menu);
            INewsGridData data = list.get(position);
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

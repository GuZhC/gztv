package com.dfsx.ganzcms.app.fragment;

import android.view.View;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.ScrollItem;

import java.util.ArrayList;

/**
 * Created by heyang on 2016/10/25  多个头栏目滚动
 */
public class HeadScrollFragment extends CommunityTabFragment implements View.OnClickListener {

    @Override
    public void initData() {
        hideEmptryView();
        itemList = new ArrayList<ScrollItem>();
//        long tvId = ColumnBasicListManager.getInstance().findIdByName("电视");
        ScrollItem item = new ScrollItem("视听", DzLiveTvFragment.newInstance(-1));
        itemList.add(item);
//        long radId = ColumnBasicListManager.getInstance().findIdByName("广播");
        item = new ScrollItem("广播", DzBrodcastFragment.newInstance(-1));
        itemList.add(item);
//        long columnId = ColumnBasicListManager.getInstance().findIdByName("点播");
        item = new ScrollItem("点播", ColumnPlayFragment.newInstance("vod"));
        itemList.add(item);
        item = new ScrollItem("主持人", new HostListFragment());
        itemList.add(item);
        fragments.clear();
        itemWidthList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ScrollItem scrollItem = (ScrollItem) itemList.get(i);
            RelativeLayout layout = new RelativeLayout(context);
            TextView titletxt = new TextView(context);
            titletxt.setId(R.id.scroll_item_text_id);
            titletxt.setText(scrollItem.getItemTitle());
            titletxt.setTextSize(BAR_TEXT_SIZE_SP);
            titletxt.setTextColor(getResources().getColor(R.color.COLOR_WHITE_50));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(titletxt, params);
            int textLength = Util.dp2px(context, scrollItem.getItemTitle().length() * BAR_TEXT_SIZE_SP);
            int itemWidth = 2 * Util.dp2px(context, ITEM_MIN_SPACE_DP) + textLength;
            mLinearLayout.addView(layout, itemWidth, Util.dp2px(context, 40));
            itemWidthList.add(itemWidth);
//            layout.setOnClickListener(this);
//            layout.setTag(i);
            fragments.add(scrollItem.getFragment());
        }

       changeBar.setBarTextArray(DEFAULT_SELECTED_COUNT, new String[]{"视听", "广播","点播","主持人"});

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
//        pager.setCurrentItem(de);
        pager.setCurrentItem(DEFAULT_SELECTED_COUNT, true);

//        setSelectedTextColor(0);
    }

    public int getCurrentPos() {
        return currentFragmentIndex;
    }

    @Override
    public void onClick(View v) {
        if (v == mSearchBtn) {
        } else {
//            int pos = (Integer) v.getTag();
//            pager.setCurrentItem(pos);
//            setSelectedTextColor(pos);
        }
    }
}

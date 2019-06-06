package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 *一个水平的listView控件
 * Created by liuwb on 2016/6/29.
 */
public class HorizontalListRecyclerView extends ListRecyclerView{

    public HorizontalListRecyclerView(Context context) {
        super(context);
    }

    public HorizontalListRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LinearLayoutManager getLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        return linearLayoutManager;
    }
}

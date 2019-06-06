package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import com.dfsx.lzcms.liveroom.view.async.AsyncExpandableListView;

/**
 * Created by liuwb on 2017/7/13.
 */
public class PullToRefreshExpandableRecyclerView extends PullToRefreshRecyclerStyleView<AsyncExpandableListView> {
    public PullToRefreshExpandableRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshExpandableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshExpandableRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshExpandableRecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        return new AsyncExpandableListView<>(context, attrs);
    }
}

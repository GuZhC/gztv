package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class PullNestedListView extends PullToRefreshListView {
    public PullNestedListView(Context context) {
        super(context);
    }

    public PullNestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullNestedListView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullNestedListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    protected ListView createListView(Context context, AttributeSet attrs) {
        return new NestedListView(context,attrs);
    }
}

package com.dfsx.core.common.view.banner;

import android.support.v4.view.PagerAdapter;

/**
 * Created by liuwb on 2016/4/28.
 */
public abstract class LoopPagerAdapter extends PagerAdapter {

    /**
     * 代理adapter对象
     */
    private PagerAdapter wrapperAdapter;

    @Override
    public void notifyDataSetChanged() {
        if(wrapperAdapter != null) {
            wrapperAdapter.notifyDataSetChanged();
        }
        super.notifyDataSetChanged();
    }

    public void setWrapperAdapter(PagerAdapter wrapperAdapter) {
        this.wrapperAdapter = wrapperAdapter;
    }
}

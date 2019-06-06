package com.dfsx.core.common.adapter;

import android.view.View;
import android.view.ViewGroup;

public abstract class BaseEmptyViewRecyclerAdapter<T> extends BaseRecyclerViewDataAdapter<T> {

    private View emptyView;
    private static final int EMPTY_VIEW = 1001;

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == EMPTY_VIEW) {
            v = emptyView;
        } else {
            v = getItemView(parent, viewType);
        }
        return new BaseRecyclerViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == EMPTY_VIEW) {
            onBindEmptyView(holder, position);
        } else {
            onBindItemView(holder, position);
        }
    }


    protected void onBindEmptyView(BaseRecyclerViewHolder holder, int position) {
    }

    public abstract View getItemView(ViewGroup parent, int viewType);

    public abstract void onBindItemView(BaseRecyclerViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ? list.size() :
                (emptyView != null ? 1 : 0);
    }

    @Override
    public final int getItemViewType(int position) {
        if ((list == null || list.size() == 0) && emptyView != null) {
            return EMPTY_VIEW;
        }
        return getOtherItemViewType(position);
    }

    public int getOtherItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}

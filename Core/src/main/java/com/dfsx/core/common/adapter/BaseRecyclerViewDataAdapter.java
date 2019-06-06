package com.dfsx.core.common.adapter;

import java.util.List;

public abstract class BaseRecyclerViewDataAdapter<T> extends BaseRecyclerViewAdapter {

    protected List<T> list;

    public void setData(List<T> data) {
        this.list = list;
    }

    public List<T> getData() {
        return list;
    }

    public void update(List<T> data, boolean isAdd) {
        if (data == null || data.size() <= 0) {
            return;
        }
        if (!isAdd || list == null) {
            list = data;
            notifyDataSetChanged();
        } else {
            int index = list.size();
            list.addAll(data);
            notifyItemRangeChanged(index, data.size());
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}

package com.dfsx.lzcms.liveroom.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.dfsx.core.common.adapter.BaseViewHodler;

import java.util.List;

/**
 * 此类暂时不支持不同的样式
 * Created by liuwb on 2016/10/26.
 */
public abstract class BaseListViewAdapter<T> extends BaseAdapter {

    protected Context context;

    protected List<T> list;

    public BaseListViewAdapter(Context context) {
        this.context = context;
    }

    public void update(List<T> data, boolean isAdd) {
        if (!isAdd || list == null) {
            list = data;
        } else {
            list.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHodler holdler = BaseViewHodler.get(convertView, parent,
                getItemViewLayoutId(), position);
        setItemViewData(holdler, position);
        return holdler.getConvertView();
    }

    /**
     * 获取每个Item的布局
     *
     * @return
     */
    public abstract int getItemViewLayoutId();

    /**
     * 设置每个item显示的数据
     *
     * @param holder
     * @param position
     */
    public abstract void setItemViewData(BaseViewHodler holder, int position);
}

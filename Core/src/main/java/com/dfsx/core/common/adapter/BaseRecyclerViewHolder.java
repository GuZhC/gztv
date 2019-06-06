package com.dfsx.core.common.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * �?个基�?的RecyclerView.ViewHolder的实现类�?
 * 功能是自动实现缓存View，不用写很多的自定义ViewHolder类，添加各种各样的View属�??
 * 调用getView方法获取Layout里面的子控件
 * Created by liuwb on 2016/6/29.
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private SparseArray<View> viewMap;
    private int viewType;
    private View convertView;
    private OnRecyclerItemViewClickListener onRecyclerItemViewClickListener;

    /**
     * @param v        每条显示的View
     * @param viewType
     */
    public BaseRecyclerViewHolder(View v, int viewType) {
        super(v);
        this.viewType = viewType;
        this.convertView = v;
        viewMap = new SparseArray<View>();
        v.setOnClickListener(this);
    }

    public BaseRecyclerViewHolder(int layoutRes, ViewGroup parent, int viewType) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
        this.viewType = viewType;
        this.convertView = itemView;
        viewMap = new SparseArray<View>();
        itemView.setOnClickListener(this);
    }

    /**
     * 这里实现缓存�?部控件功�?
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = viewMap.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            viewMap.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return convertView;
    }

    public BaseRecyclerViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public BaseRecyclerViewHolder setImageResource(int viewId, int imageId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(imageId);
        return this;
    }

    public BaseRecyclerViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    public BaseRecyclerViewHolder setBackgroundColor(int viewId, int color) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundColor(color);
        return this;
    }

    public BaseRecyclerViewHolder setVisible(int viewId, boolean show) {
        getView(viewId).setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseRecyclerViewHolder setOnclick(int viewId, View.OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
        return this;
    }

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public void setOnRecyclerItemViewClickListener(OnRecyclerItemViewClickListener listener) {
        this.onRecyclerItemViewClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerItemViewClickListener != null) {
            onRecyclerItemViewClickListener.onItemViewClick(v);
        }
    }

    public interface OnRecyclerItemViewClickListener {
        void onItemViewClick(View v);
    }
}

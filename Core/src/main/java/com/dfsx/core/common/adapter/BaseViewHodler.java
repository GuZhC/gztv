package com.dfsx.core.common.adapter;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ListView的iewHolder
 */
public class BaseViewHodler {
    private SparseArray<View> viewMap;
    private int position;
    private View convertView;

    public BaseViewHodler(ViewGroup parent, int layoutId, int position) {
        this.position = position;
        this.convertView = View.inflate(parent.getContext(), layoutId, null);
        viewMap = new SparseArray<View>();
        this.convertView.setTag(this);
    }

    public static BaseViewHodler get(View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new BaseViewHodler(parent, layoutId, position);
        } else {
            return (BaseViewHodler) convertView.getTag();
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = viewMap.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            viewMap.put(viewId, view);
        }
        return (T) view;
    }

    public int getPosition() {
        return position;
    }

    public View getConvertView() {
        return convertView;
    }

    public BaseViewHodler setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public BaseViewHodler setImageResource(int viewId, int imageId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(imageId);
        return this;
    }

    public BaseViewHodler setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    public BaseViewHodler setBackgroundColor(int viewId, int color) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundColor(color);
        return this;
    }

    public BaseViewHodler setVisible(int viewId, boolean show) {
        getView(viewId).setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseViewHodler setOnclick(int viewId, View.OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
        return this;
    }
}

package com.dfsx.core.common.business;

import android.view.View;


/**
 * by  create by heyang  2017/10/26
 * <p>
 * 针对圈子item 不同的点击事件
 */

public class IButtonClickData<T> {

    public View getTag() {
        return tag;
    }

    public T getObject() {
        return object;
    }

    protected View tag;
    protected T object;

    public IButtonClickData(View v, T data) {
        this.tag = v;
        object = data;
    }

}
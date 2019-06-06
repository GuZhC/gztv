package com.dfsx.ganzcms.app.business;

import android.view.View;


/**
 * by  create by heyang  2017/10/26
 * <p>
 * 针对圈子item 不同的点击事件
 */

public interface ILbtnClickLiser {

    public enum lbtnClickType {
        ITEM_CLICK,
        ATTION_CLICK,
        VISTI_CLICK,
        PRAISE_CLICK,
        COMMEND_CLICK,
        SHARE_CLICK,
        FARVITY_CLICK,
        DEL_CLICK,
    }

    public void onLbtClick(lbtnClickType tyep, MyData data);

    public static class MyData<T> {
        public View getTag() {
            return tag;
        }

        public T getObject() {
            return object;
        }

        protected View tag;
        protected T object;

        public MyData(View v, T data) {
            this.tag = v;
            object = data;
        }
    }

}
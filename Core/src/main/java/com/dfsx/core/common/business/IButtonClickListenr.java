package com.dfsx.core.common.business;

/**
 * by  create by heyang  2017/10/26
 * <p>
 * 针对圈子item 不同的点击事件
 */

public interface IButtonClickListenr<T> {

    public void onLbtClick(int type, IButtonClickData<T> data);

}
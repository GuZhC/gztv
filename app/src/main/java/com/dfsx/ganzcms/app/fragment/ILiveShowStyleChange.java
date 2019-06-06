package com.dfsx.ganzcms.app.fragment;

/**
 * Created by liuwb on 2017/6/14.
 * 切换主播的列表页显示的模式
 */
public interface ILiveShowStyleChange {
    int STYLE_LIST = 1;
    int STYLE_GRID = 2;

    /**
     * #STYLE_LIST
     * #STYLE_GRID
     *
     * @param style
     */
    void onStyleChange(int style);
}

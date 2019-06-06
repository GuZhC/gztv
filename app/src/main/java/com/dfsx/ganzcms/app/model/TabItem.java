package com.dfsx.ganzcms.app.model;

/**
 * 用于消息传递的实体类,用来代表fragment的切换
 * Created by wxl on 2016/12/21.
 */

public class TabItem {
    public static final String BOTTOM_TAB_NEWS = "bottom_tab_news";
    public static final String BOTTOM_TAB_LIVE = "bottom_tab_live";
    public static final String BOTTOM_TAB_TV = "bottom_tab_tv";
    public static final String BOTTOM_TAB_TOPIC = "bottom_tab_topic";
    public static final String BOTTOM_TAB_ME = "bottom_tab_me";

    private String msg;

    public TabItem(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.dfsx.searchlibaray.model;

import java.io.Serializable;

public interface ISearchData<D> extends Serializable {
    enum SearchShowStyle {
        STYLE_WORD(0),
        STYLE_WORD_THREE(1),
        STYLE_LIVE_SERVICE(2),
        STYLE_LIVE_SHOW(3),
        STYLE_QUANZI(4),
        STYLE_USER(5),
        STYLE_CMS_ACTIVITY(6),    //heyang
        STYLE_CMS_VIDEO(7);   //heyang

        private static final int ALL_SHOW_TYPE = 8;

        private int typeCount;

        SearchShowStyle(int typeCount) {
            this.typeCount = typeCount;
        }

        public static SearchShowStyle getShowStyle(int count) {
            if (count == 0) {
                return STYLE_WORD;
            } else if (count == 1) {
                return STYLE_WORD_THREE;
            } else if (count == 2) {
                return STYLE_LIVE_SERVICE;
            } else if (count == 3) {
                return STYLE_LIVE_SHOW;
            } else if (count == 4) {
                return STYLE_QUANZI;
            } else if (count == 5) {
                return STYLE_USER;
            } else if (count == 6) {
                return STYLE_CMS_ACTIVITY;
            } else if (count == 7) {
                return STYLE_CMS_VIDEO;
            } else {
                return STYLE_WORD;
            }
        }

        public int getTypeCount() {
            return typeCount;
        }

        public static int getAllShowTypeCount() {
            return ALL_SHOW_TYPE;
        }
    }

    long getId();

    SearchShowStyle getShowStyle();

    D getContentData();

    /**
     * 获取搜索接口返回的直接数据类型
     *
     * @return
     */
    SearchItemInfo getSearchItemInfo();

    /**
     * 设置搜索接口返回的直接数据类型
     *
     * @return
     */
    void setSearchItemInfo(SearchItemInfo itemInfo);
}

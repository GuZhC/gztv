package com.dfsx.ganzcms.app.model;

import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/8/19.
 */
public class ScrollItem implements Serializable {
    private String itemTitle;
    private Fragment fragment;

    public ScrollItem() {

    }

    public ScrollItem(String tile, Fragment fragment) {
        this.itemTitle = tile;
        this.fragment = fragment;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public static class ScrollItemEx extends ScrollItem {

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        private String icon;
        private  long cid;

        public ScrollItemEx(String tile, Fragment fragment, String icon) {
            super(tile, fragment);
            this.icon = icon;
        }

        public ScrollItemEx(String tile, Fragment fragment, long id) {
            super(tile, fragment);
            this.cid = id;
        }

    }
}

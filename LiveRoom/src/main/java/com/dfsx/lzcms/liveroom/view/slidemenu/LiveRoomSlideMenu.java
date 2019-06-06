package com.dfsx.lzcms.liveroom.view.slidemenu;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by liuwb on 2017/4/6.
 */
public class LiveRoomSlideMenu extends SlideMenu {
    public LiveRoomSlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LiveRoomSlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveRoomSlideMenu(Context context) {
        super(context);
    }

    @Override
    protected boolean isSupportPrimaryMenuTouchControl() {
        return true;
    }

    @Override
    protected boolean isVisiblePrimaryMenu() {
        return true;
    }

    @Override
    protected boolean isAutoCloseByBackKeyPress() {
        return false;
    }
}

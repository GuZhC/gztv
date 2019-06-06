package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2017/3/10.
 */
public abstract class AbsPopupwindow {

    protected Context context;

    protected PopupWindow popupWindow;

    protected View popContainer;

    protected Object tag;

    public AbsPopupwindow(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        popContainer = LayoutInflater.from(context).
                inflate(getPopupwindowLayoutId(), null);
        onInitWindowView(popContainer);
        popupWindow = new PopupWindow(popContainer);

        //这里需要设置成可以获取焦点，否则无法响应OnKey事件
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        setPopupWindowSize();
        // 这里用上了我们在popupWindow中定义的animation了
        popupWindow.setAnimationStyle(getPopAnimationStyle());
        Drawable drawable = context.getResources().getDrawable(R.color.transparent);
        popupWindow.setBackgroundDrawable(drawable);

        onInitFinish();
    }

    protected void onInitFinish() {

    }

    protected int getPopAnimationStyle() {
        return android.R.style.Animation_Dialog;
    }

    protected void setPopupWindowSize() {
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    protected abstract int getPopupwindowLayoutId();

    protected abstract void onInitWindowView(View popView);

    public void show(View parent) {
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener l) {
        popupWindow.setOnDismissListener(l);
    }
}

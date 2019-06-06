package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 这个类可以设置有冲突的事件的View,让DrawerLayout不在拦截
 * Created by liuwb on 2016/6/30.
 */
public class ChildInterceptTouchEventDrawerLayout extends DrawerLayout {
    private ArrayList<Integer> childInterceptIdList = new ArrayList<Integer>();

    public ChildInterceptTouchEventDrawerLayout(Context context) {
        this(context, null);
    }

    public ChildInterceptTouchEventDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChildInterceptTouchEventDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addInterceptTouchEventChildId(int id) {
        childInterceptIdList.add(id);
    }

    public void removeInterceptTouchEventChildId(int id) {
        childInterceptIdList.remove((Integer) id);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (childInterceptIdList.size() > 0) {
            for (Integer id : childInterceptIdList) {
                View scroll = findViewById(id);
                if (scroll != null) {
                    Rect rect = new Rect();
                    scroll.getGlobalVisibleRect(rect);
                    if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                        return false;
                    }
                }

            }
        }
        boolean superBack = true;
        try {
            superBack = super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return superBack;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}

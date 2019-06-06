package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * Created by liuwb on 2016/12/6.
 */
public class LiveRoomLinearLayout extends LinearLayout {
    private int mTouchSlop;

    public LiveRoomLinearLayout(Context context) {
        this(context, null);
    }

    public LiveRoomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveRoomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveRoomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            init();
        }
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        Log.e("TAG", "mTouchSlop === " + mTouchSlop);
        Log.e("TAG", "count == " + getChildCount());
    }

    private float[] downPos = new float[2];
    private float mLastXIntercept = 0, mLastYIntercept = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        boolean isScroll = false;
        if (action == MotionEvent.ACTION_DOWN) {
            downPos[0] = ev.getX();
            downPos[1] = ev.getY();
            Log.e("TAG", "onInterceptTouchEvent down Ypos == " + downPos[1]);
            mLastXIntercept = downPos[0];
            mLastYIntercept = downPos[1];
            if (onInterceptTouchEventListener != null) {
                onInterceptTouchEventListener.onTouchDown(ev);
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            float dx = ev.getX() - mLastXIntercept;
            float dy = ev.getY() - mLastYIntercept;
            mLastXIntercept = ev.getX();
            mLastYIntercept = ev.getY();
            if (abs(dy) > mTouchSlop && abs(dy) - abs(dx) > 0) {
                isScroll = true;
            } else {
                isScroll = false;
            }
            if (onInterceptTouchEventListener != null) {
                onInterceptTouchEventListener.onTouchMove(ev);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (onInterceptTouchEventListener != null) {
                onInterceptTouchEventListener.onTouchUp(ev);
            }
        }
        return isScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TAG", "onTouchEvent111111111111111111111");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e("TAG", "onTouchEvent ACTION_DOWN == " + event.getY());
        }
        return super.onTouchEvent(event);
    }


    private float abs(float a) {
        return Math.abs(a);
    }

    private int abs(int a) {
        return Math.abs(a);
    }

    private OnInterceptTouchEventListener onInterceptTouchEventListener;

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener onInterceptTouchEventListener) {
        this.onInterceptTouchEventListener = onInterceptTouchEventListener;
    }


    public interface OnInterceptTouchEventListener {
        void onTouchDown(MotionEvent ev);

        void onTouchMove(MotionEvent ev);

        void onTouchUp(MotionEvent ev);
    }
}

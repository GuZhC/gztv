package com.dfsx.lzcms.liveroom;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.dfsx.lzcms.liveroom.view.LiveRoomLinearLayout;

/**
 * Created by liuwb on 2016/12/5.
 */
public class LiveRoomSwitchActivity extends AbsChatRoomActivity {

    public static final int MIN_SCROLL_SIZE = 450;

    protected FragmentActivity context;
    private LiveRoomLinearLayout actContainerView;
    private FrameLayout upViewContainer;
    private FrameLayout centerViewContainer;
    private FrameLayout downViewContainer;

    private ImageView upLiveImage;
    private ImageView downLiveImage;

    protected int sw;

    protected int sh;

    private int startScrollIndex;

    private int downYpos;

    private int downXpos;

    private SmoothScroll smoothScroll;

    private boolean isScroll;
    private boolean isMoving;

    /**
     * 当前显示页面count
     */
    private int currentSelectedPageCount;
    private int latestY;
    private int latestX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.act_switch_room_layout);
        initView();
        smoothScroll = new SmoothScroll();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Rect outRect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
            sw = outRect.width();
            sh = outRect.height();

            setViewSize();
        }
    }

    private void initView() {
        actContainerView = (LiveRoomLinearLayout) findViewById(R.id.act_view_container);
        upViewContainer = (FrameLayout) findViewById(R.id.up_view_container);
        centerViewContainer = (FrameLayout) findViewById(R.id.center_view_container);
        downViewContainer = (FrameLayout) findViewById(R.id.down_view_container);

        upLiveImage = (ImageView) findViewById(R.id.up_room_image);
        downLiveImage = (ImageView) findViewById(R.id.down_room_image);

        actContainerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouchEvent(event);
            }
        });

        actContainerView.setOnInterceptTouchEventListener(new LiveRoomLinearLayout.OnInterceptTouchEventListener() {
            @Override
            public void onTouchDown(MotionEvent ev) {
                latestY = (int) ev.getY();
                latestX = (int) ev.getX();
                startScrollIndex = getScrollIndex();
            }

            @Override
            public void onTouchMove(MotionEvent ev) {

            }

            @Override
            public void onTouchUp(MotionEvent ev) {

            }
        });
    }

    private void setViewSize() {
        LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams(sw, sh);

        upViewContainer.setLayoutParams(matchParams);
        centerViewContainer.setLayoutParams(matchParams);
        downViewContainer.setLayoutParams(matchParams);

        setUpViewContainer(upViewContainer);
        setCenterViewContainer(centerViewContainer);
        setDownViewContainer(downViewContainer);

        goneTopAndDownView();

        setScrollTo(-sh);
    }

    private void goneTopAndDownView() {
//        upViewContainer.setVisibility(View.GONE);
        downViewContainer.setVisibility(View.GONE);
    }

    private void visibleTopAndDownView() {
//        upViewContainer.setVisibility(View.VISIBLE);
        downViewContainer.setVisibility(View.VISIBLE);
    }

    protected void setUpViewContainer(FrameLayout upViewContainer) {

    }

    protected void setCenterViewContainer(FrameLayout centerViewContainer) {

    }

    protected void setDownViewContainer(FrameLayout downViewContainer) {

    }

    protected void setUpViewImageResource(int currentSelectedPageCount) {

    }

    private int eventY;
    private int eventX;

    protected final boolean handleTouchEvent(MotionEvent event) {
        int action = event.getAction();
        eventY = (int) event.getY();
        eventX = (int) event.getX();
        if (action == MotionEvent.ACTION_DOWN) {
            onTouchDown();
            downYpos = latestY = eventY;
            downXpos = latestX = eventX;
            startScrollIndex = getScrollIndex();
            Log.e("TAG", "downYpos == " + downYpos);
        } else if (action == MotionEvent.ACTION_MOVE) {
            onTouchMove();
            int dy = (eventY - latestY);
            int dx = (eventX - latestX);
            latestX = eventX;
            latestY = eventY;
            visibleTopAndDownView();
            Log.e("TAG", "dy == " + dy);
            int set = getScrollIndex() + dy;
            if (set <= -2 * sh) {
                set = -2 * sh;
            }
            if (set >= 0) {
                set = 0;
            }
            setScrollTo(set);
        } else if (action == MotionEvent.ACTION_UP) {
            onTouchUp();
            Log.e("TAG", "ACTION_UP === ");
            int dScroll = getScrollIndex() - startScrollIndex;

            boolean toUp = dScroll > 0;

            int to = 0;
            int back = 0;
            if (toUp) {
                int percent = (startScrollIndex + sh) / sh;
                to = percent * sh;
                back = to - sh;
            } else {
                int percent = (startScrollIndex - sh) / sh;
                to = percent * sh;
                back = to + sh;
            }
            if (Math.abs(dScroll) < MIN_SCROLL_SIZE) {
                smoothScroll.smoothScrollTo(back);
            } else {
                smoothScroll.smoothScrollTo(to);
                if (toUp) {
                    currentSelectedPageCount--;
                } else {
                    currentSelectedPageCount++;
                }
            }
        }
        return true;
    }

    protected void onTouchDown() {

    }

    protected void onTouchMove() {

    }

    protected void onTouchUp() {

    }

    private void setScrollTo(int paddingTop) {
        actContainerView.setPadding(0, paddingTop, 0, 0);
    }

    private int getScrollIndex() {
        return actContainerView.getPaddingTop();
    }


    private void scrollEnd() {
        setScrollTo(-sh);
        setUpViewImageResource(currentSelectedPageCount);
        goneTopAndDownView();
    }

    class SmoothScroll {

        public static final int SCROLL_INDEX = 55;

        public static final long SPEED = 10;

        private int tagPadding;

        private Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int index = msg.arg1;
                int to = getScrollIndex() + index;
                if (index > 0 && to >= tagPadding) {
                    to = tagPadding;
                }
                if (index < 0 && to <= tagPadding) {
                    to = tagPadding;
                }
                setScrollTo(to);

                if (to != tagPadding) {
                    postMessage(index);
                } else {
                    isMoving = false;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollEnd();
                        }
                    }, 50);
                }
            }
        };

        public SmoothScroll() {

        }


        private void smoothScrollTo(int tag) {
            this.tagPadding = tag;
            int curPadding = getScrollIndex();
            int index = tag - curPadding > 0 ? SCROLL_INDEX : -1 * SCROLL_INDEX;
            postMessage(index);
            isMoving = true;
        }

        private void postMessage(int index) {
            Message msg = handler.obtainMessage(0, index, 0);
            handler.sendMessageDelayed(msg, SPEED);
        }
    }
}

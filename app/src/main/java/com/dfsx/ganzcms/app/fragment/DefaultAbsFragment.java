package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.business.DefaultLoadFailedListen;
import com.dfsx.lzcms.liveroom.view.EmptyView;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;

/*****
 * Created by heyang on 2017/8/16.
 * <p/>
 * 加载
 * 重写  initView  initData  setGesBodyViewLayout
 * 在initData 中增加    emptyView.loading();
 *   if (data != null && data.size() > 0) {
 *      hideEmptryView();
 *          initAction(data);
 *        }else {
 *             showError();
 *      }
 *
 *      在initViews 中增加点击刷新实践
 *       setGestureOnclick(new DefaultLoadFailedListen() {
 *               @Override
 *                public void onButtonFreshClick() {
 *                   initData();
 *}
 *            });
 *
 */
public abstract class DefaultAbsFragment extends Fragment implements GestureDetector.OnGestureListener {

    protected Activity act;
    protected Context context;
    DefaultLoadFailedListen mGestureRetryButton;

    LinearLayout gesBodyView;
    LinearLayout emptyLayoutContainer;
    GestureDetector mGestureDetector;
    GestureDetector.OnGestureListener mGesturelister;
    int mScreenWidth;
    EmptyView emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        context = getContext();
        return inflater.inflate(R.layout.act_default_frg, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyLayoutContainer = (LinearLayout) view.
                findViewById(R.id.empty_layout);

        gesBodyView = (LinearLayout) view.
                findViewById(R.id.body_layout);

//        mScreenWidth = UtilHelp.getScreenWidth(act);

//        mGestureDetector = new GestureDetector(act, act);
//        gesBodyView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return mGestureDetector.onTouchEvent(motionEvent);
//            }
//        });
//
        setGesBodyViewLayout(gesBodyView);
        setEmptyLayout(emptyLayoutContainer);
        initView();
        initData();
    }

    protected void setGestureOnclick(DefaultLoadFailedListen liter) {
        mGestureRetryButton = liter;
    }

    protected abstract void initView();

    protected abstract void initData();

    protected void setEmptyLayout(LinearLayout container) {
        if (container == null) return;
        emptyView = new EmptyView(context);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(p);

        container.addView(emptyView);
        View emptyLayout = LayoutInflater.from(context).
                inflate(R.layout.no_default_frg_layout, null);

        emptyView.setLoadOverView(emptyLayout);
        emptyView.loading();
        ImageView btnStart = (ImageView) emptyLayout.findViewById(R.id.retyr_btn);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGestureRetryButton != null)
                    mGestureRetryButton.onButtonFreshClick();
            }
        });
    }

    protected void showError() {
//        gesBodyView.removeAllViews();
        gesBodyView.setVisibility(View.GONE);
        emptyView.loadOver();
        emptyView.setVisibility(View.VISIBLE);
    }

    protected void hideEmptryView() {
        gesBodyView.setVisibility(View.VISIBLE);
        emptyView.loadOver();
        emptyView.setVisibility(View.GONE);
    }

    protected void setGesBodyViewLayout(LinearLayout container) {
    }

    public VideoAdwarePlayView getVideoPlyer() {
        if (context instanceof MainTabActivity) {
            return ((MainTabActivity) context).getVideoPlayer();
        }
        return null;
    }

    public void addVideoPlayerToFullContainer(VideoPlayView videoPlayer, FrameLayout videoContainer) {
        if (context instanceof MainTabActivity) {
            videoContainer = ((MainTabActivity) context).getActivityContainer();
        }
        videoContainer.setVisibility(View.VISIBLE);
//        fullContainer.setVisibility(View.GONE);
        if (videoContainer != null) {
            if (!(videoContainer.getChildAt(0) != null &&
                    videoContainer.getChildAt(0) instanceof VideoPlayView)) {
                removeVideoPlayer();
                videoContainer.addView(videoPlayer, 0);
            }
        }
    }

    public void addVideoPlayerToContainer(VideoPlayView videoPlayer, FrameLayout videoContainer) {
        videoContainer.setVisibility(View.VISIBLE);
        if (videoContainer != null) {
            if (!(videoContainer.getChildAt(0) != null &&
                    videoContainer.getChildAt(0) instanceof VideoPlayView)) {
                removeVideoPlayer();
                videoContainer.addView(videoPlayer, 0);
            }
        }
    }

    public void removeVideoPlayer() {
        if (getVideoPlyer() != null) {
            ViewGroup view = (ViewGroup) getVideoPlyer().getParent();
            if (view != null) {
                view.removeView(getVideoPlyer());
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            //闄愬埗蹇呴』寰楀垝杩囧睆骞曠殑1/4鎵嶈兘绠楀垝杩?
            float x_limit = mScreenWidth / 2;
            float y_limit = mScreenWidth / 2;
            float x_abs = Math.abs(x);
            float y_abs = Math.abs(y);
            if (x_abs >= y_abs) {
                //gesture left or right
                if (x > x_limit || x < -x_limit) {
                    if (x > 0) {
                        /***
                         *  right 鐩稿綋浜庡線鍙宠竟鍒?
                         */
                        act.finish();
                    } else if (x <= 0) {
//                        //left
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
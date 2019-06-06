package com.dfsx.ganzcms.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.view.banner.SimpleImageBanner;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;

/**
 * Created by heyang on 2017/7/24
 * 左划 右划操作的 Fragment
 */
public class GestureFragment extends Fragment implements GestureDetector.OnGestureListener {
    private static final String TAG = "GestureFragment";
    public static final int RESULT_OK = -1;
    private final int NETWORK_BUSY = 54;
    private int mScreenWidth;
    private ImageButton mLoadRetryBtn;
    private boolean mBshowViewPager = false;
    private long mCLoumnType = -1;
    private SimpleImageBanner topBanner;
    private int destheight = 250;


    public Handler myHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == NETWORK_BUSY) {
                if (getActivity() != null) {
                    AlertDialog adig = new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("网络繁忙，是否重新加载数据.....？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                          adapters.SetInitStatus(false);
                            GestureFragment.this.onResume();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        }
                    }).create();
                    adig.show();
                } else {
                }
            }
            return false;
        }
    });


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v2) {
        try {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            //限制必须得划过屏幕的1/4才能算划过
            float x_limit = Util.dp2px(getActivity(), 35);
            float y_limit = Util.dp2px(getActivity(), 35);
            float x_abs = Math.abs(x);
            float y_abs = Math.abs(y);
            final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;
            if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling down
                Log.i("MyGesture", "Fling down");
                Toast.makeText(getActivity(), "Fling down", Toast.LENGTH_SHORT).show();
            } else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling up
                Log.i("MyGesture", "Fling up");
                Toast.makeText(getActivity(), "Fling up", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
//        mScreenHeight = (mScreenWidth * 9) / 16;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getVideoPlyer() != null) {
            getVideoPlyer().onDestroy();
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.news, container, false);
//        mLoadRetryBtn = (ImageButton) view.findViewById(R.id.reload_btn);
//        mLoadRetryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GestureFragment.this.onResume();
//            }
//        });
//        return view;
//    }

    public VideoAdwarePlayView getVideoPlyer() {
        if (getActivity() instanceof MainTabActivity) {
            return ((MainTabActivity) getActivity()).getVideoPlayer();
        }
        return null;
    }

    public void stopVideo() {
        if (getVideoPlyer() != null) {
            getVideoPlyer().stop();
            getVideoPlyer().release();
            removeVideoPlayer();
            if (getVideoPlyer().getTag() != null) {
                View tag = (View) getVideoPlyer().getTag();
                tag.setVisibility(View.VISIBLE);
            }
        }
    }

    private void removeVideoPlayer() {
        if (getVideoPlyer() != null) {
            ViewGroup view = (ViewGroup) getVideoPlyer().getParent();
            if (view != null) {
                view.removeView(getVideoPlyer());
            }
        }
    }

    private Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        if (fragment == null) return null;
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case 0:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

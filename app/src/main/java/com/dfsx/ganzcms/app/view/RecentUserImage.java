package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.view.OnCirbuttonClickListener;

/**
 * Created by  heyang on 2016/4/25.
 * 下面的编辑框，右边默认有一个发表按钮
 */
public class RecentUserImage extends ImageView implements OnCirbuttonClickListener {
    private boolean isScaling;

    private int mPrevMoveX;
    private int mPrevMoveY;
    private OnCirbuttonClickListener mOnTouchListener;

    public RecentUserImage(Context context) {
        super(context);
        mOnTouchListener = this;
    }

    public RecentUserImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOnTouchListener = this;
    }

    public RecentUserImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOnTouchListener = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecentUserImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mOnTouchListener = this;
    }

    @Override
    public boolean onTouchEventSS(View v, MotionEvent event) {
        if (v.getTag(com.dfsx.core.R.id.cirbutton_user_id) != null) {
            //            if (v.getTag() instanceof Long) {
            long id = (Long) v.getTag(com.dfsx.core.R.id.cirbutton_user_id);
            //            if (id == pireId) return true;
            //            pireId = id;
            IntentUtil.gotoPersonHomeAct(getContext(), id);
            //            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //        if (mDetector.onTouchEvent(event)) {
        //            return true;
        //        }
        int touchCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_1_DOWN:
            case MotionEvent.ACTION_POINTER_2_DOWN:
                if (touchCount >= 2) {
                    //                    float distance = distance(event.getX(0), event.getX(1),
                    //                            event.getY(0), event.getY(1));
                    //                    mPrevDistance = distance;
                    //                    isScaling = true;

                    int a = 0;
                    int b = 0;
                } else {
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();

                    mOnTouchListener.onTouchEventSS(this, event);
                }
            case MotionEvent.ACTION_MOVE:
                if (touchCount >= 2 && isScaling) {
                    //                    float dist = distance(event.getX(0), event.getX(1),
                    //                            event.getY(0), event.getY(1));
                    //                    float scale = (dist - mPrevDistance) / dispDistance();
                    //                    mPrevDistance = dist;
                    //                    scale += 1;
                    //                    scale = scale * scale;
                    //    zoomTo(scale, mWidth / 2, mHeight / 2);
                    //   cutting();
                    int a = 0;
                    int b = 0;

                } else if (!isScaling) {
                    int distanceX = mPrevMoveX - (int) event.getX();
                    int distanceY = mPrevMoveY - (int) event.getY();
                    mPrevMoveX = (int) event.getX();
                    mPrevMoveY = (int) event.getY();
                    //    mMatrix.postTranslate(-distanceX, -distanceY);
                    //     cutting();

                    int a = 0;
                    int b = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_2_UP:
                if (event.getPointerCount() <= 1) {
                    isScaling = false;
                }
                break;
        }
        return true;
    }
}

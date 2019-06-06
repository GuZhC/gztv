package com.dfsx.lzcms.liveroom.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.dfsx.lzcms.liveroom.R;

public class LoadingView extends View {
    private int mStartX;

    public enum Direction {
        LEFT, RIGHT;
    }

    private int mDirection = DIRECTION_RIGHT;

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;

    private int mOriginColor = 0xffff0000;
    private int mChangeColor = 0xff00ff00;

    private float mProgress = 0.5f;

    private Drawable mIconDrawable;
    private int mIconWidth;
    private int mIconHeight;
    private Rect mIconBound;

    private Canvas mCanavs;
    private Bitmap mOriginBitmap;

    private float mScale = 1.0f;
    // TODO
    private int mScaleType = FIT_CENTER;
    public static final int FIT_CENTER = 0;
    public static final int FIT_XY = 1;

    private ObjectAnimator animator;
    private long animDuration;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.LoadingView);
        mOriginColor = ta
                .getColor(R.styleable.LoadingView_text_origin_color,
                        mOriginColor);
        mProgress = ta.getFloat(R.styleable.LoadingView_progress, 0);
        mScale = ta.getFloat(R.styleable.LoadingView_scale, 1.0f);
        mDirection = ta.getInt(R.styleable.LoadingView_direction,
                mDirection);
        mScaleType = ta.getInt(R.styleable.LoadingView_scale_type,
                FIT_CENTER);
        mIconDrawable = ta.getDrawable(R.styleable.LoadingView_track_icon);

        mIconWidth = mIconDrawable.getIntrinsicWidth();
        mIconHeight = mIconDrawable.getIntrinsicHeight();
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("TAG", MeasureSpec.toString(widthMeasureSpec));
        Log.e("TAG", MeasureSpec.toString(heightMeasureSpec));

        int width = resolveSize((int) (mIconWidth * mScale), widthMeasureSpec);
        int height = resolveSize((int) (mIconHeight * mScale),
                heightMeasureSpec);

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.e("TAG", "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        int realWidth = getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight();
        int realHeight = getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom();

        switch (mScaleType) {
            case FIT_CENTER:
                if (mIconWidth * mScale > realWidth
                        || mIconHeight * mScale > realHeight) {
                    float scale = Math.min(realWidth * 1.0f / mIconWidth,
                            realHeight * 1.0f / mIconHeight);
                    mScale = Math.min(scale, mScale);
                }
                mIconWidth = (int) (mIconWidth * mScale);
                mIconHeight = (int) (mIconHeight * mScale);
                mStartX = realWidth / 2 - mIconWidth / 2;

                mIconBound = new Rect(mStartX, //
                        getMeasuredHeight() / 2 - mIconHeight / 2, //
                        mStartX + mIconWidth,//
                        getMeasuredHeight() / 2 + mIconHeight / 2);
                break;
            case FIT_XY:
                mIconWidth = realWidth;
                mIconHeight = realHeight;
                mStartX = getPaddingLeft();

                mIconBound = new Rect(getPaddingLeft(), getPaddingRight(),
                        getPaddingLeft() + mIconWidth, getPaddingRight()
                        + mIconHeight);
                break;
        }

        mIconDrawable.setBounds(mIconBound);
        setUpBitmap();

        start(3000);
    }

    public void start(long duration) {
        animDuration = duration;
        if (animator != null && animator.isRunning()) {
            return;
        }
        animator = ObjectAnimator.ofFloat(this, "progress",
                0, 1);
        animator.setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            start(animDuration);
        } else {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }
        }
    }

    private void setUpBitmap() {
        // setUpOriginBm
        if (mOriginBitmap == null) {
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            if (w > 0 && h > 0) {
                mOriginBitmap = Bitmap.createBitmap(w,
                        h, Bitmap.Config.ARGB_8888);
                mCanavs = new Canvas(mOriginBitmap);
                mIconDrawable.draw(mCanavs);
                mCanavs.drawColor(mOriginColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    public void reverseColor() {
        int tmp = mOriginColor;
        mOriginColor = mChangeColor;
        mChangeColor = tmp;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e("TAG", "onAttachedToWindow ----- ");
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.e("TAG", "onWindowVisibilityChanged visibility " + visibility);
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        super.onWindowSystemUiVisibilityChanged(visible);
        Log.e("TAG", "onWindowSystemUiVisibilityChanged visibility " + visible);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mIconDrawable.draw(canvas);
        if (mDirection == DIRECTION_LEFT) {
            int r = (int) (mProgress * mIconWidth + mStartX);
            _draw(canvas, mOriginBitmap, mStartX, r);
        } else {
            int r = (int) (mIconWidth * (1 - mProgress) + mStartX);
            _draw(canvas, mOriginBitmap, mStartX, r);
        }

    }

    private void _draw(Canvas canvas, Bitmap bm, int startX, int endX) {
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(startX, 0, endX, getMeasuredHeight());
        canvas.drawBitmap(bm, 0, 0, null);
        canvas.restore();
    }

    public float getProgress() {
        return mProgress;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        mDirection = direction;
        invalidate();
    }

    public void changeDirection() {
        if (mDirection == DIRECTION_LEFT) {
            mDirection = DIRECTION_RIGHT;
        } else {
            mDirection = DIRECTION_LEFT;
        }
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        setUpBitmap();
        invalidate();
    }

    public int getOriginColor() {
        return mOriginColor;
    }

    public void setAnimDuration(long animDuration) {
        this.animDuration = animDuration;
    }

    public void setOriginColor(int mTextOriginColor) {
        this.mOriginColor = mTextOriginColor;
        setUpBitmap();
        invalidate();
    }

    public int getChangeColor() {
        return mChangeColor;
    }

    public void setChangeColor(int mTextChangeColor) {
        this.mChangeColor = mTextChangeColor;
        setUpBitmap();
        invalidate();
    }

    public void setIcon(Drawable icon) {
        this.mIconDrawable = icon;
        mIconWidth = icon.getIntrinsicWidth();
        mIconHeight = icon.getIntrinsicHeight();
        requestLayout();
        invalidate();
    }

    public void setScale(float scale) {
        mScale = scale;
        mIconWidth = mIconDrawable.getIntrinsicWidth();
        mIconHeight = mIconDrawable.getIntrinsicHeight();
        requestLayout();
    }

    public float getScale() {
        return mScale;
    }

}

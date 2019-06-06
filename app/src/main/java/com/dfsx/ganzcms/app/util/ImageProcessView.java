package com.dfsx.ganzcms.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageProcessView extends ImageView {

    private Paint mPaint;// 画笔
    int width = 0;
    int height = 0;
    Context context = null;
    int progress = 0;

    public boolean isStartUp() {
        return isStartUp;
    }

    public void setIsStartUp(boolean isStartUp) {
        this.isStartUp = isStartUp;
    }

    boolean isStartUp = false;

    public ImageProcessView(Context context) {
        super(context);
// TODO Auto-generated constructor stub
    }

    public ImageProcessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageProcessView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mPaint = new Paint();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#70000000"));// 半透明
        if (isStartUp) {
            canvas.drawRect(0, 0, getWidth(), getHeight() - getHeight() * progress
                    / 100, mPaint);
        }

        mPaint.setColor(Color.parseColor("#00000000"));// 全透明
        if (isStartUp) {
            canvas.drawRect(0, getHeight() - getHeight() * progress / 100,
                    getWidth(), getHeight(), mPaint);
        }

        mPaint.setTextSize(50);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
//        mPaint.setColor(Color.parseColor("#fb5054"));
        mPaint.setStrokeWidth(5);
        Rect rect = new Rect();
        mPaint.getTextBounds("100%", 0, "100%".length(), rect);// 确定文字的宽度
        if (isStartUp) {
            canvas.drawText(progress + "%", getWidth() / 2 - rect.width() / 2,
                    getHeight() / 2, mPaint);
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if(this.progress==-1)  isStartUp=false;
        postInvalidate();
    }

}
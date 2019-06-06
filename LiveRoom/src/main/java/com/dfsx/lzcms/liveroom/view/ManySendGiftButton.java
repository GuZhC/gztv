package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

/**
 * 多次发送按钮
 * Created by liuwb on 2016/7/20.
 */
public class ManySendGiftButton extends View {

    private int textSize = 14;//sp

    private Paint myPaint;
    private Paint bkgPaint;
    private Paint centerpaint;

    private Paint textPaint;
    private Paint myFramePaint;
    public String text;
    private float startAngle;
    public float temp;
    float sweepAngle = 360;
    private int flag = 0;
    private RectF rect;
    private RectF rectBkg;
    private RectF rectCenter;
    private int pix = 0;

    private Context context;
    private CountDownTimer countDownTimer;
    private OnTimeFinishListener timeFinishListener;

    public ManySendGiftButton(Context context) {
        this(context, null);
    }

    public ManySendGiftButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ManySendGiftButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        myPaint = new Paint();
        bkgPaint = new Paint();
        centerpaint = new Paint();
        DisplayMetrics metrics = getContext().getResources()
                .getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float scarea = width * height;
        pix = (int) Math.sqrt(scarea * 0.0217);

        myPaint.setAntiAlias(true);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setColor(Color.WHITE);  //Edit this to change progress arc color.
        myPaint.setStrokeWidth(7);

        bkgPaint.setAntiAlias(true);
        bkgPaint.setStyle(Paint.Style.FILL);
        bkgPaint.setColor(Color.parseColor("#C8ea8010"));  //Edit this to change progress arc color.

        centerpaint.setAntiAlias(true);
        centerpaint.setStyle(Paint.Style.FILL);
        centerpaint.setColor(Color.parseColor("#ea8010"));  //Edit this to change progress arc color.

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(PixelUtil.dp2px(context, textSize));

        myFramePaint = new Paint();
        myFramePaint.setAntiAlias(true);
        myFramePaint.setColor(Color.TRANSPARENT);

        float startx = (float) (pix * 0.05);
        float endx = (float) (pix * 0.95);
        float starty = (float) (pix * 0.05);
        float endy = (float) (pix * 0.95);
        rectBkg = new RectF(startx, starty, endx, endy);
        float d = 13;
        rect = new RectF(startx + d, starty + d, endx - d, endy - d);
        float dStroke = 7;
        rectCenter = new RectF(startx + d + dStroke,
                starty + d + dStroke,
                endx - d - dStroke,
                endy - d - dStroke);
    }

    public void setUpProgress(int progress) {

        //Updating progress arc

        sweepAngle = (float) (progress * 3.6);
        flag = 0;
    }

    public void startCountDownTimer(final long time) {
        setUpProgress(100);
        text = time / 1000 + "";
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long k = 100 / (time / 1000);
                int progress = (int) (k * (millisUntilFinished / 1000));
                text = millisUntilFinished / 1000 + "";
                setUpProgress(progress);
            }

            @Override
            public void onFinish() {
                setUpProgress(0);
                if (timeFinishListener != null) {
                    timeFinishListener.onTimeFinish();
                }
            }
        };
        countDownTimer.start();
    }

    public void setOnTimeFinishListener(OnTimeFinishListener l) {
        this.timeFinishListener = l;
    }

    public void reset() {

        //Resetting progress arc

        sweepAngle = 100;
        startAngle = -90;
        flag = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = pix;
        int desiredHeight = pix;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;


        if (widthMode == MeasureSpec.EXACTLY) {

            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {

            width = Math.min(desiredWidth, widthSize);
        } else {

            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {

            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {

            height = Math.min(desiredHeight, heightSize);
        } else {

            height = desiredHeight;
        }


        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawOval(rectBkg, bkgPaint);
        canvas.drawOval(rectCenter, centerpaint);
        canvas.drawArc(rect, startAngle, -sweepAngle, false, myPaint);

        if (!TextUtils.isEmpty(text)) {
            Paint.FontMetricsInt fontMetrics = centerpaint.getFontMetricsInt();
            // 转载请注明出处：http://blog.csdn.net/hursing
            int baseline = (int) ((rectCenter.bottom + rectCenter.top - fontMetrics.bottom - fontMetrics.top) / 2);
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, rectCenter.centerX(),
                    baseline - PixelUtil.dp2px(context, textSize / 2), textPaint);
            canvas.drawText("连送", rectCenter.centerX(),
                    baseline + PixelUtil.dp2px(context, textSize / 2), textPaint);
        }

        startAngle = -90;

        if (sweepAngle <= 360 && flag == 0) {

            invalidate();

        } else if (flag == 1) {

            sweepAngle = 0;
            startAngle = -90;
            flag = 0;
            invalidate();
        } else {

            sweepAngle = 0;
            startAngle = -90;

        }
    }

    public interface OnTimeFinishListener {
        void onTimeFinish();
    }
}

package com.dfsx.lzcms.liveroom.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class RoundBackgroundColorSpan extends ReplacementSpan {

    private int bgColor;
    private int textColor;
    private int textHeight;

    private BackgroundTopPaddingListener topPaddingListener;

    public RoundBackgroundColorSpan(int bgColor, int textColor, int textHeight) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.textHeight = textHeight;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return ((int) paint.measureText(text, start, end) + 60);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color1 = paint.getColor();
        paint.setColor(this.bgColor);
        int t = Math.max(top + 1, y - textHeight - 1);
        int b = bottom - 1;
        if (topPaddingListener != null) {
            topPaddingListener.onSetTopPadding(t - top);
        }
        //        Log.e("TAG", " t, b ==  " + t + "," + b);
        canvas.drawRoundRect(new RectF(x, t, x + ((int) paint.measureText(text, start, end) + 30), b), 10, 10, paint);
        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x + 15, y, paint);
        paint.setColor(color1);
    }

    public void setBackgroundTopPaddingListener(BackgroundTopPaddingListener listener) {
        this.topPaddingListener = listener;
    }

    public interface BackgroundTopPaddingListener {
        void onSetTopPadding(int topPadding);
    }
}

package com.dfsx.ganzcms.app.util;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 *  heyang 2016-12-14
 *
 */
public class Richtext extends TextView {
    boolean dontConsumeNonUrlClicks = true;
    boolean linkHit;
    public Richtext(Context context) {
        super(context);
    }
    public Richtext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Richtext(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 一、 listview    setOnItemLongClickListener  BUG
        // linkHit = false;
        // boolean res = super.onTouchEvent(event);
        //
        // if (dontConsumeNonUrlClicks)
        // return linkHit;
        // return res;

        // 二、有误触textview内链接BUG
        if (getMovementMethod() == null) {
            boolean result = super.onTouchEvent(event);
            return result;
        }
        MovementMethod m = getMovementMethod();
        setMovementMethod(null);
        boolean mt = m.onTouchEvent(this, (Spannable) getText(), event);
        if (mt && event.getAction() == MotionEvent.ACTION_DOWN) {
            event.setAction(MotionEvent.ACTION_UP);
            mt = m.onTouchEvent(this, (Spannable) getText(), event);
            event.setAction(MotionEvent.ACTION_DOWN);
        }
        boolean st = super.onTouchEvent(event);
        setMovementMethod(m);
        setFocusable(false);
        return mt || st;
    }
    @Override
    public boolean hasFocusable() {
        return false;
    }
    public static class LocalLinkMovementMethod extends LinkMovementMethod {
        static LocalLinkMovementMethod sInstance;
        public static LocalLinkMovementMethod getInstance() {
            if (sInstance == null)
                sInstance = new LocalLinkMovementMethod();
            return sInstance;
        }
        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer,
                                    MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();
                x += widget.getScrollX();
                y += widget.getScrollY();
                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);
                ClickableSpan[] link = buffer.getSpans(off, off,
                        ClickableSpan.class);
                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }
                    if (widget instanceof Richtext) {
                        ((Richtext) widget).linkHit = true;
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                    Touch.onTouchEvent(widget, buffer, event);
                    return false;
                }
            }
            return Touch.onTouchEvent(widget, buffer, event);
        }
    }
}
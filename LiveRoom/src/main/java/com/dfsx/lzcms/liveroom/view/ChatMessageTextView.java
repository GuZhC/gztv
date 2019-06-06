package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 当前聊天显示的文本控件
 * Created by liuwb on 2016/11/29.
 */
public class ChatMessageTextView extends TextView implements RoundBackgroundColorSpan.BackgroundTopPaddingListener {

    private static final int NAME_COLOR = Color.parseColor("#dcdcdc");

    private Context context;

    private CharSequence text;

    private NameBackgroundTopPaddingListener backgroundTopPaddingListener;

    public ChatMessageTextView(Context context) {
        this(context, null);
    }

    public ChatMessageTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatMessageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChatMessageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void setContent(CharSequence name,
                           CharSequence content) {
        setContent(name, NAME_COLOR, content);
    }

    public void setContent(CharSequence name, int nameColor,
                           CharSequence content) {
        SpannableString nameString = new SpannableString(name);
        int bkgColor = Color.parseColor("#33000000");
        int size = (int) getTextSize();
        RoundBackgroundColorSpan roundBackgroundColorSpan = new RoundBackgroundColorSpan(bkgColor, nameColor, size);
        roundBackgroundColorSpan.setBackgroundTopPaddingListener(this);
        nameString.setSpan(roundBackgroundColorSpan,
                0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(nameString)
                .append(" ")
                .append(content);

        setText(spannableStringBuilder);
    }

    public void setNameBackgroundTopPaddingListener(NameBackgroundTopPaddingListener listener) {
        this.backgroundTopPaddingListener = listener;
    }

    @Override
    public void onSetTopPadding(int topPadding) {
        if (backgroundTopPaddingListener != null) {
            backgroundTopPaddingListener.onNameBkgPaddingTop(topPadding);
        }
    }

    public interface NameBackgroundTopPaddingListener {
        void onNameBkgPaddingTop(int padding);
    }
}

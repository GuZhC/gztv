package com.dfsx.ganzcms.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2016/4/25.
 * 下面的编辑框，右边默认有一个发表按钮
 */
public class BottomEditBar extends LinearLayout {

    /**
     * 右边控件统一大小为dp
     */
    protected static final int RIGHT_VIEW_SIZE = 36;

    protected Context context;
    private EditText editText;
    private LinearLayout rightContainer;

    private TextView sendTextView;

    public BottomEditBar(Context context) {
        this(context, null);
    }

    public BottomEditBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BottomEditBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.bottom_edit_layout, this);
        editText = (EditText) findViewById(R.id.edit_text);
        rightContainer = (LinearLayout) findViewById(R.id.right_layout);

        setRightView(rightContainer);
    }

    protected void setRightView(LinearLayout rightContainer) {
        sendTextView = createSendTextView(context.getResources().getString(R.string.send_text));
        rightContainer.addView(sendTextView);

        sendTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendClickListener != null) {
                    sendClickListener.onSendClick(view);
                }
            }
        });
    }

    private TextView createSendTextView(String text) {
        TextView tv = new TextView(context);
        LayoutParams params = new LayoutParams(Util.dp2px(context, RIGHT_VIEW_SIZE),
                Util.dp2px(context, RIGHT_VIEW_SIZE));
        params.rightMargin = Util.dp2px(context, 8);
        tv.setLayoutParams(params);
        tv.setId(R.id.bottom_bar_send);
        tv.setBackgroundColor(context.getResources().getColor(R.color.blue2));
        tv.setTextColor(context.getResources().getColor(R.color.white));
        tv.setTextSize(13);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    public TextView getSendTextView() {
        return sendTextView;
    }

    public String getEditString() {
        return editText.getText().toString();
    }

    public EditText getEditText() {
        return editText;
    }

    protected OnSendClickListener sendClickListener;

    public void setOnSendClickListener(OnSendClickListener l) {
        this.sendClickListener = l;
    }

    public interface OnSendClickListener {
        void onSendClick(View v);
    }
}

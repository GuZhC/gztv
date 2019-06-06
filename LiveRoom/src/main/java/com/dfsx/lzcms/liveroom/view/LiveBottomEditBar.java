package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2016/10/10.
 */
public class LiveBottomEditBar extends RelativeLayout {

    private Context context;

    private EditText editText;

    private ImageView giftImg;

    private Button sendBtn;

    private InputMethodManager im;

    private Handler handler = new Handler();

    private boolean isShowGiftButton = true;

    public LiveBottomEditBar(Context context) {
        this(context, null);
    }

    public LiveBottomEditBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveBottomEditBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveBottomEditBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }


    private void init() {

        im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.bottom_edit_bar, this);

        editText = (EditText) findViewById(R.id.edit_text);

        giftImg = (ImageView) findViewById(R.id.send_gift);

        sendBtn = (Button) findViewById(R.id.btn_send_text);

        giftImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (barClickListener != null) {
                            barClickListener.onSendGiftClick();
                        }
                    }
                }, 100);

            }
        });
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barClickListener != null) {
                    barClickListener.onSendTextClick(editText.getText().toString());
                }
                editText.setText("");
                hideInput();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    setGiftImgVisiable(true);
                    sendBtn.setVisibility(GONE);
                } else {
                    setGiftImgVisiable(false);
                    sendBtn.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void setGiftImgVisiable(boolean isVisible) {
        giftImg.setVisibility(isShowGiftButton && isVisible ? VISIBLE : GONE);
    }

    public void showInput() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                im.showSoftInput(editText, InputMethodManager.SHOW_FORCED);

            }
        });
    }

    public void hideInput() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (im.isActive()) {
                    im.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        });

    }

    /**
     * 设置是否显示送礼物的按钮
     *
     * @param isVisiable
     */
    public void setSendGiftViewVisiable(boolean isVisiable) {
        isShowGiftButton = isVisiable;
        setGiftImgVisiable(isShowGiftButton);
    }

    private OnBarClickListener barClickListener;

    public void setOnBarClickListener(OnBarClickListener l) {
        this.barClickListener = l;
    }

    public interface OnBarClickListener {
        void onSendTextClick(String text);

        void onSendGiftClick();
    }
}

package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.dfsx.lzcms.liveroom.R;

public class LiveBackPlayBottomBar extends FrameLayout implements View.OnClickListener {
    /**
     * 当前显示标记为工具功能按键显示
     */
    private static final int STAET_TOOL_BAR = 1121;
    /**
     * 当前显示标记为可编辑状态
     */
    private static final int STAET_EDIT_BAR = 1122;

    private Context context;
    private InputMethodManager im;

    private View toolContainerView;
    private View editContainerView;
    private EditText editTextView;
    private Button btnSendText;

    private ImageView videoPauseImage, chatImage, shareImage, giftImage;

    private int currentViewState;

    private OnEventClickListener clickListener;

    public LiveBackPlayBottomBar(Context context) {
        this(context, null);
    }

    public LiveBackPlayBottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveBackPlayBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LiveBackPlayBottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.view_back_play_bottom_bar, this);

        videoPauseImage = (ImageView) findViewById(R.id.btn_play);
        chatImage = (ImageView) findViewById(R.id.live_chat);
        shareImage = (ImageView) findViewById(R.id.share);
        giftImage = (ImageView) findViewById(R.id.send_gift);

        toolContainerView = findViewById(R.id.bottom_tool_view);
        editContainerView = findViewById(R.id.bottom_edit_view);

        editTextView = (EditText) findViewById(R.id.edit_text);
        btnSendText = (Button) findViewById(R.id.btn_send);

        shareImage.setOnClickListener(this);
        chatImage.setOnClickListener(this);
        videoPauseImage.setOnClickListener(this);
        giftImage.setOnClickListener(this);
        btnSendText.setOnClickListener(this);

        showToolView();
    }

    public void showEditView() {
        setViewState(STAET_EDIT_BAR);
        showInputMethod();
    }

    public void showToolView() {
        setViewState(STAET_TOOL_BAR);
        hideInputMethod();
    }

    public boolean isEditState() {
        return currentViewState == STAET_EDIT_BAR;
    }

    /**
     * @param state #STAET_TOOL_BAR, #STAET_EDIT_BAR
     */
    private void setViewState(int state) {
        if (currentViewState != state) {
            //
        }
        currentViewState = state;
        if (currentViewState == STAET_TOOL_BAR) {
            toolContainerView.setVisibility(VISIBLE);
            editContainerView.setVisibility(GONE);
            editTextView.clearFocus();
        } else {
            currentViewState = STAET_EDIT_BAR;
            toolContainerView.setVisibility(GONE);
            editContainerView.setVisibility(VISIBLE);
            editTextView.requestFocus();
            editTextView.setSelection(0);
            editTextView.setFocusable(true);
            editTextView.setFocusableInTouchMode(true);
        }
    }

    public void setBtnPlayStatus(boolean playing) {
        int res = playing ?
                R.drawable.icon_back_play_playing :
                R.drawable.icon_back_play_pause;
        videoPauseImage.setImageResource(res);
    }

    private void showInputMethod() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                im.showSoftInput(editTextView, InputMethodManager.SHOW_FORCED);
            }
        }, 300);
    }

    private void hideInputMethod() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (im.isActive()) {
                    im.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    @Override
    public void onClick(View v) {
        if (v == shareImage) {
            if (clickListener != null) {
                clickListener.onShareClick(v);
            }
        } else if (v == videoPauseImage) {
            if (clickListener != null) {
                clickListener.onVideoPauseClick(v);
            }
        } else if (v == chatImage) {
            showEditView();
        } else if (v == giftImage) {
            if (clickListener != null) {
                clickListener.onGiftClick(v);
            }
        } else if (v == btnSendText) {
            showToolView();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickListener != null) {
                        clickListener.onChatSendClick(btnSendText,
                                editTextView.getText().toString());
                    }
                    editTextView.setText("");
                }
            }, 200);

        }
    }

    public void setOnEventClickListener(OnEventClickListener l) {
        this.clickListener = l;
    }

    public interface OnEventClickListener {
        void onShareClick(View v);

        void onVideoPauseClick(View v);

        void onGiftClick(View v);

        void onChatSendClick(View v, String text);
    }
}

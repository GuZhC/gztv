package me.lake.librestreaming.sample.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import me.lake.librestreaming.sample.R;

/**
 * Created by liuwb on 2017/3/14.
 */
public class LiveAnchorBottomToolBar extends FrameLayout implements View.OnClickListener {

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

    private ImageView shareImage;
    private ImageView beautyImage;
    private ImageView chatImage;
    private ImageView switchImage;

    private View toolContainerView;
    private View editContainerView;
    private EditText editTextView;
    private Button btnSendText;

    private OnEventClickListener clickListener;

    private OnBarViewShowChangeListener changeListener;

    private int currentViewState;


    public LiveAnchorBottomToolBar(Context context) {
        this(context, null);
    }

    public LiveAnchorBottomToolBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveAnchorBottomToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveAnchorBottomToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.live_anchor_bottom_bar, this);
        shareImage = (ImageView) findViewById(R.id.live_share);
        beautyImage = (ImageView) findViewById(R.id.live_beauty);
        chatImage = (ImageView) findViewById(R.id.live_chat);
        switchImage = (ImageView) findViewById(R.id.live_switch_btn);

        toolContainerView = findViewById(R.id.bottom_tool_view);
        editContainerView = findViewById(R.id.bottom_edit_view);

        editTextView = (EditText) findViewById(R.id.edit_text);
        btnSendText = (Button) findViewById(R.id.btn_send);

        shareImage.setOnClickListener(this);
        beautyImage.setOnClickListener(this);
        chatImage.setOnClickListener(this);
        switchImage.setOnClickListener(this);
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

    /**
     * 当前是否显示的是编辑条
     *
     * @return
     */
    public boolean isEditViewShow() {
        return currentViewState == STAET_EDIT_BAR;
    }

    /**
     * @param state #STAET_TOOL_BAR, #STAET_EDIT_BAR
     */
    private void setViewState(int state) {
        if (currentViewState != state) {
            if (changeListener != null) {
                changeListener.onShowChange(state == STAET_EDIT_BAR);
            }
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

    @Override
    public void onClick(View v) {
        if (v == shareImage) {
            if (clickListener != null) {
                clickListener.onClickShare(v);
            }
        } else if (v == beautyImage) {
            if (clickListener != null) {
                clickListener.onClickBeauty(v);
            }
        } else if (v == chatImage) {
            showEditView();
        } else if (v == switchImage) {
            if (clickListener != null) {
                clickListener.onClickSwitch(v);
            }
        } else if (v == btnSendText) {
            showToolView();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickListener != null) {
                        clickListener.onClickSend(btnSendText,
                                editTextView.getText().toString());
                    }
                    editTextView.setText("");
                }
            }, 200);

        }
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

    public void setOnEventClickListener(OnEventClickListener l) {
        this.clickListener = l;
    }

    public void setOnBarViewShowChangeListener(OnBarViewShowChangeListener l) {
        this.changeListener = l;
    }

    public interface OnEventClickListener {
        void onClickShare(View v);

        void onClickBeauty(View v);

        void onClickSwitch(View v);

        void onClickSend(View v, String text);
    }

    public interface OnBarViewShowChangeListener {
        /**
         * 当前显示的是编辑状态么
         *
         * @param isEditViewShow
         */
        void onShowChange(boolean isEditViewShow);
    }
}

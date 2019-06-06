package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2017/6/26.
 */
public class LiveServiceBottomBar extends FrameLayout implements View.OnClickListener {

    public static final int TYPE_EDIT = 1;
    public static final int TYPE_TEXT_GIFT = 2;
    private Context context;

    private View textGiftView;
    private View editLayoutView;
    private TextView textView;
    private ImageView btnSendGift;
    private EditText editText;
    private ImageButton btnSendText;
    private View emptyBackView;

    private Handler handler = new Handler();

    private int currentType = TYPE_TEXT_GIFT;
    private OnViewShowTypeChangeListener changeListener;
    private OnViewBtnClickListener viewBtnClickListener;

    private InputMethodManager im;

    private boolean isCouldSendChat = true;

    public LiveServiceBottomBar(Context context) {
        this(context, null);
    }

    public LiveServiceBottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveServiceBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveServiceBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater.from(context)
                .inflate(R.layout.live_service_bottom_bar, this);

        textGiftView = findViewById(R.id.text_view_layout);
        editLayoutView = findViewById(R.id.edit_view_layout);
        textView = (TextView) findViewById(R.id.edit_text_text);
        btnSendGift = (ImageView) findViewById(R.id.send_gift);
        editText = (EditText) findViewById(R.id.edit_send_text);
        btnSendText = (ImageButton) findViewById(R.id.btn_send_text);
        emptyBackView = findViewById(R.id.empty_back_view);

        setViewShowType(currentType);

        textGiftView.setOnClickListener(this);
        editLayoutView.setOnClickListener(this);
        btnSendGift.setOnClickListener(this);
        textView.setOnClickListener(this);
        textGiftView.setOnClickListener(this);
        btnSendText.setOnClickListener(this);
        emptyBackView.setOnClickListener(this);
    }

    /**
     * #TYPE_EDIT
     * #TYPE_TEXT_GIFT
     *
     * @param type
     */
    public void setViewShowType(int type) {
        if (type != currentType) {
            currentType = type;
            if (type == TYPE_EDIT) {
                textGiftView.setVisibility(GONE);
                editLayoutView.setVisibility(VISIBLE);

                editText.requestFocus();
                editText.setSelection(0);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
            } else {
                textGiftView.setVisibility(VISIBLE);
                editLayoutView.setVisibility(GONE);
            }
            if (changeListener != null) {
                changeListener.onShowTypeChange(currentType);
            }
        }
    }

    public int getCurrentType() {
        return currentType;
    }

    public boolean isEditViewShow() {
        return currentType == TYPE_EDIT;
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

    @Override
    public void onClick(View v) {
        if (v == textView) {
            if (isCouldSendChat) {
                setViewShowType(TYPE_EDIT);
                showInput();
            }
        } else if (v == btnSendGift) {
            if (viewBtnClickListener != null) {
                viewBtnClickListener.onSendGiftClick(v);
            }
        } else if (v == btnSendText) {
            String text = editText.getText().toString();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(context, "请输入文字", Toast.LENGTH_SHORT).show();
            } else {
                editText.setText("");
                setViewShowType(TYPE_TEXT_GIFT);
                hideInput();
            }
            if (viewBtnClickListener != null) {
                viewBtnClickListener.onSendTextClick(v, text);
            }
        } else if (emptyBackView == v) {
            setViewShowType(TYPE_TEXT_GIFT);
            hideInput();
        }
    }

    public boolean onBackPressed() {
        if (isEditViewShow()) {
            setViewShowType(TYPE_TEXT_GIFT);
            hideInput();
            return true;
        }
        return false;
    }

    public void setOnViewShowTypeChangeListener(OnViewShowTypeChangeListener l) {
        this.changeListener = l;
    }

    public void setOnViewBtnClickListener(OnViewBtnClickListener l) {
        this.viewBtnClickListener = l;
    }

    public void setCouldSendChat(boolean couldSendChat) {
        isCouldSendChat = couldSendChat;
    }

    public interface OnViewShowTypeChangeListener {
        void onShowTypeChange(int type);
    }

    public interface OnViewBtnClickListener {
        void onSendGiftClick(View v);

        void onSendTextClick(View v, String text);
    }
}

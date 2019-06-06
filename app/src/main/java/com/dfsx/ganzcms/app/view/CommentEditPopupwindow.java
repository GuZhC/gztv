package com.dfsx.ganzcms.app.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.view.AbsPopupwindow;

import java.net.URLEncoder;

public class CommentEditPopupwindow extends AbsPopupwindow {

    private EditText mCommendEdt;
    private ImageButton mSendBtn;
    private OnBtnSendClickListener sendClickListener;
    private long conentId;
    private InputMethodManager imm;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Activity act;

    public CommentEditPopupwindow(Activity context) {
        super(context);
        this.act = context;
        imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected int getPopupwindowLayoutId() {
        return R.layout.layout_more;
    }

    @Override
    protected void setPopupWindowSize() {
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @Override
    protected void onInitWindowView(View popView) {
        mCommendEdt = (EditText) popView.findViewById(R.id.commentEdit_replay_edt);
        mSendBtn = (ImageButton) popView.findViewById(R.id.commentButton);
        mCommendEdt.addTextChangedListener(new EditChangedLister(new EditChangedLister.EditeTextStatuimpl() {
            @Override
            public void onTextStatusCHanged(boolean isHas) {
                if (isHas) {
                    mSendBtn.setImageResource(R.drawable.video_send_select);
                } else {
                    mSendBtn.setImageResource(R.drawable.video_send_normal);
                }
            }
        }));
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contnt = mCommendEdt.getText().toString().trim();
                if (sendClickListener != null) {
                    sendClickListener.onSendClick(conentId, getTag(), contnt);
                }
                setSoftInput(false);
                mCommendEdt.setText("");
                dismiss();
            }
        });

    }

    @Override
    protected void onInitFinish() {
        super.onInitFinish();
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setSoftInput(false);
            }
        });
    }

    public void setSoftInput(final boolean isShow) {
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isShow) {
                    // 显示输入法
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    // 隐藏输入法
                    imm.hideSoftInputFromWindow(act.getWindow().
                            getDecorView().getWindowToken(), 0);
                }
            }
        }, 100);
    }

    public void show(View parent) {
        show(parent, "");
    }

    public void show(View parent, String textHint) {
        popupWindow.showAtLocation(parent, Gravity.BOTTOM,
                0, 0);
        if (!TextUtils.isEmpty(textHint)) {
            mCommendEdt.setText("");
            mCommendEdt.setHint(textHint);
        }
        setSoftInput(true);
    }

    @Override
    public void dismiss() {
        mCommendEdt.setText("");
        super.dismiss();
    }

    public void setOnBtnSendClickListener(OnBtnSendClickListener clickListener) {
        this.sendClickListener = clickListener;
    }

    public long getConentId() {
        return conentId;
    }

    public void setConentId(long conentId) {
        this.conentId = conentId;
    }


    public interface OnBtnSendClickListener {
        void onSendClick(long id, Object tag, String content);
    }
}

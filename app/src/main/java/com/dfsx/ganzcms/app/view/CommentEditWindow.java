package com.dfsx.ganzcms.app.view;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.ganzcms.app.R;

import java.net.URLEncoder;

public class CommentEditWindow extends BaseEditWindow {
    private EditText mCommendEdt;
    private ImageButton mSendBtn;
    private OnBtnSendClickListener sendClickListener;
    private long conentId;

    @Override
    public int getWindowLayoutRes() {
        return R.layout.layout_more;
    }

    @Override
    public EditText getEditText() {
        return mCommendEdt;
    }

    @Override
    public int getLayoutHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public void onWindowCreateView(View v) {
        mCommendEdt = (EditText) v.findViewById(R.id.commentEdit_replay_edt);
        mSendBtn = (ImageButton) v.findViewById(R.id.commentButton);
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
                    sendClickListener.onSendClick(conentId, getObjectTag(), contnt);
                }
                hideInput();
                mCommendEdt.setText("");
                dismiss();
            }
        });
    }

    public void show(FragmentManager fm, String tag, View anchor) {
        show(fm, tag, anchor, "");
    }

    public void show(FragmentManager fm, String tag, View anchor, String textHint) {
        super.show(fm, tag, anchor);
        CommentEditRunnable editRunnable = new CommentEditRunnable();
        editRunnable.setHit(textHint);
        if (!isCreated) {
            runAfterCreated = editRunnable;
        } else {
            editRunnable.run();
            runAfterCreated = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCommendEdt != null) {
            mCommendEdt.setText("");
        }
        hideInput();
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


    class CommentEditRunnable implements Runnable {

        private String hit;

        @Override
        public void run() {
            if (!TextUtils.isEmpty(hit) && mCommendEdt != null) {
                mCommendEdt.setText("");
                mCommendEdt.setHint(hit);
                showInput();
            }
        }

        public void setHit(String hit) {
            this.hit = hit;
        }
    }
}

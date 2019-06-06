package com.dfsx.ganzcms.app.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2016/12/16.
 */
public class EditLiveRoomPasswordDialog extends Dialog {
    private Context context;
    private View baseView;

    private EditText editPassword;

    private TextView noteText;

    private Button btnCancel, btnOk;

    private OnPositiveButtonClickListener clickListener;

    private InputMethodManager imm;

    private Handler handler = new Handler(Looper.getMainLooper());

    private OnDismissListener mDismissListener;

    private boolean isAutoOpenInputMethod;

    public EditLiveRoomPasswordDialog(@NonNull Context context) {
        super(context, com.dfsx.lzcms.liveroom.R.style.transparentFrameWindowStyle);
        this.context = context;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        setView();
    }

    public EditLiveRoomPasswordDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        initSetting();
        setView();
    }

    protected EditLiveRoomPasswordDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.context = context;
        initSetting();
        setView();
    }

    private void initSetting() {
        this.setCanceledOnTouchOutside(true);
        super.setOnDismissListener(dismissListener);
    }

    private void setView() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        baseView = inflater.inflate(R.layout.dialog_edit_room_password, null);
        setContentView(baseView);

        editPassword = (EditText) findViewById(R.id.edit_room_password);
        noteText = (TextView) findViewById(R.id.note_text);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.
                            onPositiveButtonClick(EditLiveRoomPasswordDialog.this, v);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (isAutoOpenInputMethod) {
            showInput();
        }
    }

    private void hideInput() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(editPassword.getWindowToken(), 0);
                }
            }
        }, 100);
    }


    private void showInput() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm.showSoftInput(editPassword, InputMethodManager.SHOW_FORCED);
            }
        }, 500);
    }

    public EditText getEditPassword() {
        return editPassword;
    }

    public void clearEditTextInput() {
        editPassword.setText("");
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public void updateNoteText(String noteText, int color) {
        this.noteText.setTextColor(color);
        this.noteText.setText(noteText);
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener l) {
        this.clickListener = l;
    }

    /**
     * 设置是否自动打开输入法
     *
     * @param autoOpenInputMethod
     */
    public void setAutoOpenInputMethod(boolean autoOpenInputMethod) {
        isAutoOpenInputMethod = autoOpenInputMethod;
    }

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick(EditLiveRoomPasswordDialog dialog, View v);
    }

    private OnDismissListener dismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            hideInput();
            if (mDismissListener != null) {
                mDismissListener.onDismiss(dialog);
            }
        }
    };
}

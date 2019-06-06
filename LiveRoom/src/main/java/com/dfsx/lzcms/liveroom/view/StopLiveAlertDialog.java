package com.dfsx.lzcms.liveroom.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2016/12/19.
 */
public class StopLiveAlertDialog extends Dialog {
    private Context context;

    private Button btnStopOk;

    private ImageView btnCacel;

    private OnPositiveButtonClickListener clickListener;

    public StopLiveAlertDialog(@NonNull Context context) {
        super(context, R.style.transparentFrameWindowStyle);
        this.context = context;
        initSetting();
        setView();
    }

    public StopLiveAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        initSetting();
        setView();
    }

    protected StopLiveAlertDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        initSetting();
        setView();
    }

    private void initSetting() {
        this.setCanceledOnTouchOutside(true);
    }

    private void setView() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View baseView = inflater.inflate(R.layout.dialog_stop_live, null);
        setContentView(baseView);

        btnStopOk = (Button) findViewById(R.id.btn_stop_ok);
        btnCacel = (ImageView) findViewById(R.id.btn_cancel_image);


        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnStopOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onPositiveButtonClick(StopLiveAlertDialog.this, v);
                }
                dismiss();
            }
        });
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener l) {
        this.clickListener = l;
    }

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick(StopLiveAlertDialog dialog, View v);
    }
}

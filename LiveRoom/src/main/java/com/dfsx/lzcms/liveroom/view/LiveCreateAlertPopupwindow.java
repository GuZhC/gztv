package com.dfsx.lzcms.liveroom.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.*;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.LiveAlertWindowManager;

/**
 * Created by liuwb on 2017/3/28.
 */
public class LiveCreateAlertPopupwindow extends AbsPopupwindow {

    private TextView alertTextView;
    private CheckBox checkBox;
    private Button btnOk;
    private boolean isAgree;
    private OnAgreeCallBackListener listener;

    public LiveCreateAlertPopupwindow(Context context) {
        super(context);
    }

    @Override
    protected int getPopupwindowLayoutId() {
        return R.layout.pop_create_live_alert;
    }

    @Override
    protected void onInitWindowView(View popView) {
        alertTextView = (TextView) popView.findViewById(R.id.live_alert_text);
        checkBox = (CheckBox) popView.findViewById(R.id.check_agree);
        btnOk = (Button) popView.findViewById(R.id.btn_ok);

        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnOk.setEnabled(isChecked);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAgree = true;
                LiveAlertWindowManager.setIsShowed(context, true);
                dismiss();
            }
        });
    }

    public void setOnAgreeCallBackListener(OnAgreeCallBackListener l) {
        listener = l;
    }

    @Override
    public void show(View parent) {
        super.show(parent);
        isAgree = false;
    }

    @Override
    protected void onInitFinish() {
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.agreeCallBack(isAgree);
                }
                //                if (LiveAlertWindowManager.isNeedShowWindow(context) &&
                //                        context instanceof Activity) {
                //                    ((Activity) context).finish();
                //                }
            }
        });
    }

    public void setShowText(String text) {
        alertTextView.setText(text);
    }

    public interface OnAgreeCallBackListener {
        void agreeCallBack(boolean isAgree);
    }
}

package com.dfsx.ganzcms.app.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;

public class OpenAppOKDialog extends Dialog {

    private Context context;

    public OpenAppOKDialog(Context context) {
        super(context, R.style.transparentFrameWindowStyle);
        this.context = context;
        this.setCanceledOnTouchOutside(true);
        setView();
    }

    private void setView() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View baseView = inflater.inflate(R.layout.dialog_task_open_app, null);
        setContentView(baseView);
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void autoShow() {
        Activity act = App.getInstance().getTopActivity();
        if (act != null && !act.isFinishing()) {
            OnceDayShowInfoMananger.updateKeyStatus(OnceDayShowInfoMananger.KEY_TASK_OPEN_APP, true);
            show();
        }
    }
}

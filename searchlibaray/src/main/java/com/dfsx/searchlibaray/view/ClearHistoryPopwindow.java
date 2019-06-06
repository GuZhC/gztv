package com.dfsx.searchlibaray.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.dfsx.lzcms.liveroom.view.AbsPopupwindow;
import com.dfsx.searchlibaray.R;

public class ClearHistoryPopwindow extends AbsPopupwindow implements View.OnClickListener {

    private TextView btnCancle;
    private TextView btnDel;

    public ClearHistoryPopwindow(Context context) {
        super(context);
    }

    @Override
    protected int getPopupwindowLayoutId() {
        return R.layout.pop_clear_history;
    }

    @Override
    protected void onInitWindowView(View popView) {
        btnCancle = (TextView) popView.findViewById(R.id.cancel_del_text);
        btnDel = (TextView) popView.findViewById(R.id.del_history);

        popView.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        btnDel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnDel) {
            if (listener != null) {
                listener.onDelClick(v, tag);
            }
        }
        dismiss();
    }

    private OnEventClickListener listener;

    public void setOnEventClickListener(OnEventClickListener l) {
        this.listener = l;
    }

    public interface OnEventClickListener {
        void onDelClick(View v, Object tag);
    }
}

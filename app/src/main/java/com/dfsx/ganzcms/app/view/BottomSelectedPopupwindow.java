package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.lzcms.liveroom.view.AbsPopupwindow;

public class BottomSelectedPopupwindow extends AbsPopupwindow implements View.OnClickListener {

    private LinearLayout popButtomContainer;
    private Button btnCancle;
    private View emptyView;

    private String[] btnStrings;

    private OnBottomItemClickListener itemClickListener;

    public BottomSelectedPopupwindow(Context context) {
        super(context);
        btnStrings = new String[2];
        btnStrings[0] = "拍照";
        btnStrings[1] = "从相册中选取";
    }

    public BottomSelectedPopupwindow(Context context, String... btnStrings) {
        super(context);
        this.btnStrings = btnStrings;
        if (this.btnStrings == null) {
            this.btnStrings = new String[2];
            this.btnStrings[0] = "拍照";
            this.btnStrings[1] = "从相册中选取";
        }
    }

    @Override
    protected int getPopupwindowLayoutId() {
        return R.layout.bottom_selected_pop_layout;
    }

    @Override
    protected void onInitWindowView(View popView) {
        popButtomContainer = (LinearLayout) popView.findViewById(R.id.pop_button_container);
        btnCancle = (Button) popView.findViewById(R.id.item_popupwindows_cancel);
        emptyView = popView.findViewById(R.id.empty_pop_view);


        btnCancle.setOnClickListener(this);
        emptyView.setOnClickListener(this);
    }

    public void initDataView() {
        popButtomContainer.removeAllViews();
        for (int i = 0; i < btnStrings.length; i++) {
            Button btn = createButton(btnStrings[i], i);
            btn.setOnClickListener(this);
            popButtomContainer.addView(btn);
            if (i != btnStrings.length - 1) {
                addButtonDivideView();
            }
        }
    }

    private Button createButton(String text, int id) {
        Button btn = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                PixelUtil.dp2px(context, 55));
        btn.setLayoutParams(params);
        btn.setTextColor(Color.parseColor("#585858"));
        btn.setBackgroundResource(R.drawable.bt_nobgd);
        btn.setText(text);
        btn.setTextSize(18);
        btn.setId(id);
        return btn;
    }

    private void addButtonDivideView() {
        View view = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                2);
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.parseColor("#f2f2f2"));
        popButtomContainer.addView(view);
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            int id = v.getId();
            int count = btnStrings != null ? btnStrings.length : 0;
            if (id >= 0 && id < count) {
                itemClickListener.onItemClick(v, id);
            }
        }
        dismiss();
    }

    public void setOnBottomItemClickListener(OnBottomItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnBottomItemClickListener {

        void onItemClick(View v, int position);
    }
}

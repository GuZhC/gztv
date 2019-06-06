package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.ganzcms.app.R;
import com.dfsx.thirdloginandshare.share.ShareContent;

/**
 * Created by heyang on 2017/3/28.
 */
public class ReportPopupWindow {

    private Activity context;

    private View popContainer;
    private PopupWindow popupWindow;
    private LinearLayout popLayout;
    private LinearLayout popBgView;
    private Button btnReport, btnCancle;
    private ShareContent mShareInfo;

    private OnPopViewClickListener clickListener;

    private int sw, sh;

    public ReportPopupWindow(Activity context) {
        this.context = context;
        popContainer = LayoutInflater.from(context).
                inflate(R.layout.report_middle_popupwindows, null);

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        sw = windowManager.getDefaultDisplay().getWidth();
        sh = windowManager.getDefaultDisplay().getHeight();

        initView(popContainer);

        popupWindow = new PopupWindow(popContainer);
        //这里需要设置成可以获取焦点，否则无法响应OnKey事件
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 这里用上了我们在popupWindow中定义的animation了
//        popupWindow.setAnimationStyle(R.style.LiveVideoPopupStyle);
        Drawable drawable = context.getResources().getDrawable(R.color.transparent);
        popupWindow.setBackgroundDrawable(drawable);

//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        backgroundAlpha(1f);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //     full(false);
//                backgroundAlpha(1f);
            }
        });
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void initView(View v) {
        popLayout = (LinearLayout) v.findViewById(R.id.pop_layout);
        popBgView = (LinearLayout) v.findViewById(R.id.ll_popup);
        btnReport = (Button) v
                .findViewById(R.id.item_popupwindows_report);
        btnCancle = (Button) v
                .findViewById(R.id.item_popupwindows_cancel);
        int setWidth = (int) (sw * 0.65f);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) popBgView.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(setWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            params.width = setWidth;
        }
        popBgView.setLayoutParams(params);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onFavityClick(mShareInfo);
                }
                IntentUtil.goReport(v.getContext());
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onCancleClick();
                }
                dismiss();
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        popBgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private Window getWindow() {
        return context.getWindow();
    }

    private void full(boolean enable) {
        if (enable) {//隐藏状态栏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
        } else {//显示状态栏
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
        }
    }

    public void show(View v) {
        show(v, 0, 0);
    }

    public void show(View v, int x, int y) {
        //   full(true);
//        popupWindow.showAtLocation(v, Gravity.CENTER, x, y);

        popupWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }


    public void setOnPopViewClickListener(OnPopViewClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public interface OnPopViewClickListener {
        void onShareClick();

        void onFavityClick(ShareContent  info);

        void onNextClick();

        void onCancleClick();
    }
}

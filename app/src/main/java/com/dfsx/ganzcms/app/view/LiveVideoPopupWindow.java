package com.dfsx.ganzcms.app.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import com.dfsx.ganzcms.app.R;

/**
 * Created by liuwb on 2016/12/6.
 */
public class LiveVideoPopupWindow {

    private Activity context;

    private View popContainer;
    private PopupWindow popupWindow;
    private LinearLayout popLayout;

    private LinearLayout popBgView;
    private Button btnStartLive, btnUploadVide, btnYuGaoLive, btnServiceLive;
    private Button btnCancle;

    private OnPopViewClickListener clickListener;

    private int sw, sh;

    public LiveVideoPopupWindow(Activity context) {
        this.context = context;
        popContainer = LayoutInflater.from(context).
                inflate(R.layout.item_middle_popupwindows, null);

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
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 这里用上了我们在popupWindow中定义的animation了
        popupWindow.setAnimationStyle(R.style.LiveVideoPopupStyle);
        Drawable drawable = context.getResources().getDrawable(R.color.transparent);
        popupWindow.setBackgroundDrawable(drawable);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                full(false);
            }
        });
    }

    private void initView(View v) {
        popLayout = (LinearLayout) v.findViewById(R.id.pop_layout);
        popBgView = (LinearLayout) v.findViewById(R.id.ll_popup);

        btnStartLive = (Button) v
                .findViewById(R.id.item_popupwindows_live);
        btnUploadVide = (Button) v
                .findViewById(R.id.item_popupwindows_up);
        btnYuGaoLive = (Button) v
                .findViewById(R.id.item_popupwindows_yugao);
        btnServiceLive = (Button) v
                .findViewById(R.id.item_popupwindows_service_live);
        btnCancle = (Button) v
                .findViewById(R.id.item_popupwindows_cannels);
        int setWidth = (int) (sw * 0.65f);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) popBgView.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(setWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            params.width = setWidth;
        }
        popBgView.setLayoutParams(params);

        btnYuGaoLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onStartYuGaoLiveClick();
                }
                dismiss();
            }
        });
        btnServiceLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onServiceLiveClick();
                }
                dismiss();
            }
        });
        btnStartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onStartLiveClick();
                }
                dismiss();
            }
        });
        btnUploadVide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onUploadVideoClick();
                }
                dismiss();
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
        full(true);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, y);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }


    public void setOnPopViewClickListener(OnPopViewClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public interface OnPopViewClickListener {
        void onStartLiveClick();

        void onUploadVideoClick();

        void onStartYuGaoLiveClick();

        void onServiceLiveClick();

        void onCancleClick();
    }
}

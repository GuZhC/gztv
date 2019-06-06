package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.ganzcms.app.R;
import com.dfsx.thirdloginandshare.share.SharePlatform;

/**
 * Created by  heynag on 2018/5/17
 */
public class CommunitActSharePopwindow {

    protected Context context;
    protected PopupWindow popupWindow;
    protected View popContainer;
    private TextView shareQQ, shareWx, shareWB, shareFriend;
    private TextView cancelText;
    private OnShareClickListener onShareClickListener;
    private ImageView   juBaoBtn;


    public CommunitActSharePopwindow(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        popContainer = LayoutInflater.from(context).
                inflate(R.layout.communiact_share_popwindow_layout, null);
        initView(popContainer);
        popupWindow = new PopupWindow(popContainer);

        //这里需要设置成可以获取焦点，否则无法响应OnKey事件
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 这里用上了我们在popupWindow中定义的animation了
        popupWindow.setAnimationStyle(R.style.UpInDownOutPopupStyle);
        Drawable drawable = context.getResources().getDrawable(R.color.transparent);
        popupWindow.setBackgroundDrawable(drawable);
    }

    private void initView(View v) {
        juBaoBtn=(ImageView)v.findViewById(R.id.comunty_jubao_btn);
        shareQQ = (TextView) v.findViewById(R.id.share_qq);
        shareWB = (TextView) v.findViewById(R.id.share_wb);
        shareWx = (TextView) v.findViewById(R.id.share_wx);
        shareFriend = (TextView) v.findViewById(R.id.share_wxfriends);
        cancelText = (TextView) v.findViewById(R.id.btn_cancle);

        v.findViewById(R.id.empty_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        shareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onShareClickListener != null) {
                    onShareClickListener.onShareClick(v);
                }
            }
        });
        shareWB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onShareClickListener != null) {
                    onShareClickListener.onShareClick(v);
                }
            }
        });
        shareWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onShareClickListener != null) {
                    onShareClickListener.onShareClick(v);
                }
            }
        });
        shareFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShareClickListener != null) {
                    onShareClickListener.onShareClick(v);
                }
            }
        });

        juBaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.goReport(v.getContext());
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show(View parent) {
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    public void setOnShareClickListener(OnShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    public SharePlatform getSharePlatform(View v) {
        SharePlatform sharePlatform = null;
        if (v.getId() == R.id.share_qq) {
            sharePlatform = SharePlatform.QQ;
        } else if (v.getId() == R.id.share_wx) {
            sharePlatform = SharePlatform.Wechat;
        } else if (v.getId() == R.id.share_wb) {
            sharePlatform = SharePlatform.WeiBo;
        } else if (v.getId() == R.id.share_wxfriends) {
            sharePlatform = SharePlatform.Wechat_FRIENDS;
        }
        return sharePlatform;
    }

    public interface OnShareClickListener {
        void onShareClick(View v);
    }
}

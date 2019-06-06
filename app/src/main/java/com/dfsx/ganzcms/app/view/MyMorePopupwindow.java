package com.dfsx.ganzcms.app.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.model.PlayBackInfo;
import com.dfsx.thirdloginandshare.share.SharePlatform;

/**
 * Created by liuwb on 2016/10/26.
 */
public class MyMorePopupwindow {
    private Context context;

    private PopupWindow popupWindow;

    private View popContainer;

    private TwoRelyView wxShare, wbShare, QQShare, friendsShare;

    private View emptyView;
    private TextView btnDel, btnSetNoBackPlay;

    private OnMoreClickListener clickListener;

    private Object tag;

    public MyMorePopupwindow(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        popContainer = LayoutInflater.from(context).
                inflate(R.layout.my_live_more_layout, null);
        initView(popContainer);
        popupWindow = new PopupWindow(popContainer);

        //这里需要设置成可以获取焦点，否则无法响应OnKey事件
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 这里用上了我们在popupWindow中定义的animation了
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        Drawable drawable = context.getResources().getDrawable(R.color.transparent);
        popupWindow.setBackgroundDrawable(drawable);
    }

    private void initView(View view) {
        emptyView = view.findViewById(R.id.empty_view);
        wxShare = (TwoRelyView) view.findViewById(R.id.share_wx);
        wbShare = (TwoRelyView) view.findViewById(R.id.share_wb);
        QQShare = (TwoRelyView) view.findViewById(R.id.share_qq);
        friendsShare = (TwoRelyView) view.findViewById(R.id.share_wxfriends);
        btnDel = (TextView) view.findViewById(R.id.btn_del);
        btnSetNoBackPlay = (TextView) view.findViewById(R.id.btn_no_back_play);

        setBtnNoBackPlayText();

        emptyView.setOnClickListener(allClickListener);
        wxShare.setOnClickListener(allClickListener);
        wbShare.setOnClickListener(allClickListener);
        QQShare.setOnClickListener(allClickListener);
        friendsShare.setOnClickListener(allClickListener);
        btnDel.setOnClickListener(allClickListener);
        btnSetNoBackPlay.setOnClickListener(allClickListener);
    }


    private View.OnClickListener allClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == wxShare) {
                share(SharePlatform.Wechat);
            } else if (v == wbShare) {
                share(SharePlatform.WeiBo);
            } else if (v == QQShare) {
                share(SharePlatform.QQ);
            } else if (v == friendsShare) {
                share(SharePlatform.Wechat_FRIENDS);
            } else if (v == btnDel) {
                if (clickListener != null) {
                    clickListener.onDelete(tag);
                }
            } else if (v == btnSetNoBackPlay) {
                if (clickListener != null && clickListener instanceof OnMoreClickListener1) {
                    ((OnMoreClickListener1) clickListener).onSetNoBackPlay(tag);
                }
            }
            popupWindow.dismiss();
        }
    };

    private void share(SharePlatform platform) {
        if (clickListener != null) {
            clickListener.onShare(platform, tag);
        }
    }

    public void setClickListener(OnMoreClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void show(View parent) {
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void setTag(Object tag) {
        this.tag = tag;
        setBtnNoBackPlayText();
    }

    private void setBtnNoBackPlayText() {
        if (tag != null && tag instanceof PlayBackInfo) {
            String noBackPlayText = ((PlayBackInfo) tag).isPrivacy() ?
                    "对外回放" : "不对外回放";
            if (btnSetNoBackPlay != null) {
                btnSetNoBackPlay.setText(noBackPlayText);
            }
        }
    }

    public interface OnMoreClickListener {
        void onShare(SharePlatform platform, Object tag);

        void onDelete(Object tag);
    }

    public interface OnMoreClickListener1 extends OnMoreClickListener {
        void onSetNoBackPlay(Object tag);
    }
}

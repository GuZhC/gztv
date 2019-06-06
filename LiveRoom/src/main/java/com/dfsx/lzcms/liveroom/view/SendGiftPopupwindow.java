package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.business.MyMoneyInfoManager;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.model.UserMoneyInfo;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.StringUtil;

/**
 * Created by liuwb on 2016/7/5.
 */
public class SendGiftPopupwindow implements View.OnClickListener {

    private Context context;

    private View popContainer;
    private PopupWindow popupWindow;

    private LiveGiftSelectView giftSelectView;
    private TextView goldExtraText, addGoldText;
    private ManySendGiftButton manySendGiftButton;

    private Button btnSend;

    private OnClickEventListener listener;

    public SendGiftPopupwindow(Context context) {
        this.context = context;

        popContainer = LayoutInflater.from(context).
                inflate(R.layout.send_gift_popupwindow_layout, null);
        initView(popContainer);
        popupWindow = new PopupWindow(popContainer);

        //这里需要设置成可以获取焦点，否则无法响应OnKey事件
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 这里用上了我们在popupWindow中定义的animation了
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        Drawable drawable = context.getResources().getDrawable(R.color.bg_send_gift);
        popupWindow.setBackgroundDrawable(drawable);
    }

    private void initView(View v) {
        giftSelectView = (LiveGiftSelectView) v.findViewById(R.id.selected_gift_view);
        goldExtraText = (TextView) v.findViewById(R.id.gold_extra);
        addGoldText = (TextView) v.findViewById(R.id.add_gold);
        btnSend = (Button) v.findViewById(R.id.send_gift);
        manySendGiftButton = (ManySendGiftButton) v.findViewById(R.id.more_send_gift);

        hideManySendBtn();
        updateMoneyText();
        giftSelectView.setSelectedtGiftByViewCount(0);

        addGoldText.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        manySendGiftButton.setOnClickListener(this);

        giftSelectView.setOnSelectedChangeListener(new SelectGiftView.
                OnSelectedChangeListener() {
            @Override
            public void onSelectedChange() {
                hideManySendBtn();
            }
        });

        manySendGiftButton.setOnTimeFinishListener(new ManySendGiftButton.
                OnTimeFinishListener() {
            @Override
            public void onTimeFinish() {
                hideManySendBtn();
            }
        });
    }

    public void show(View parent) {
        giftSelectView.setSelectedtGiftByViewCount(0);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    private void hideManySendBtn() {
        btnSend.setVisibility(View.VISIBLE);
        manySendGiftButton.setVisibility(View.GONE);
    }

    private void showManySendBtn() {
        btnSend.setVisibility(View.GONE);
        manySendGiftButton.setVisibility(View.VISIBLE);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        popupWindow.setOnDismissListener(listener);
    }

    public void setOnClickEventListener(OnClickEventListener l) {
        this.listener = l;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.add_gold == id) {
            if (listener != null) {
                listener.onBuyGoldClick();
            }
        } else if (R.id.send_gift == id) {
            showManySendBtn();
            manySendGiftButton.startCountDownTimer(25 * 1000);
            if (listener != null) {
                listener.onSendGiftClick(giftSelectView.getSelectedGift(),
                        getSendGiftNum(), false);
            }
        } else if (R.id.more_send_gift == id) {
            manySendGiftButton.startCountDownTimer(25 * 1000);
            if (!new IsLoginCheck(context).checkLogin()) {
                return;
            }
            if (listener != null) {
                listener.onSendGiftClick(giftSelectView.getSelectedGift(),
                        getSendGiftNum(), true);
            }
        }
    }

    protected int getSendGiftNum() {
        return 1;
    }

    public void updateMoneyText() {
        MyMoneyInfoManager.getInstance().getMoneyInfo(context, new ICallBack<UserMoneyInfo>() {
            @Override
            public void callBack(UserMoneyInfo data) {
                int money = 0;
                if (data != null) {
                    money = (int) data.getCoins();
                }
                goldExtraText.setText(StringUtil.moneyToString(money));
            }
        });
    }

    public interface OnClickEventListener {
        void onBuyGoldClick();

        /**
         * @param gift
         * @param num        每次发送的数量
         * @param isManySend 是否是连发
         */
        void onSendGiftClick(GiftModel gift, int num, boolean isManySend);
    }
}

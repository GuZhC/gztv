package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.model.GiftModel;
import com.dfsx.lzcms.liveroom.util.PixelUtil;

import java.util.LinkedList;

/**
 * 直播特效显示空间
 * Created by liuwb on 2016/7/20.
 */
public class LiveSpecialEffectView extends LinearLayout implements OnRemoveViewListener {

    public static final int TYPE_GIFT_RECEIVE = 1001;
    public static final int TYPE_USER_COME = 1002;

    private static final String KEY_USER_PERSON = "LiveSpecialEffectView_user_person";

    public long VIEW_TIME = 5 * 1000;
    private Context context;

    private LinkedList<ReceiveItem> showList = new LinkedList<ReceiveItem>();

    public LiveSpecialEffectView(Context context) {
        this(context, null);
    }

    public LiveSpecialEffectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveSpecialEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);

        View emptyView = new View(context);
        LayoutParams emptyParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        emptyParams.weight = 1;
        emptyView.setLayoutParams(emptyParams);
        addView(emptyView, emptyParams);
    }

    public void receiveGiftView(String senderId,
                                String senderLogo, String senderName,
                                GiftModel gift, int sendNum) {
        String key = senderId + "_" + (gift != null ? gift.getId() : "0");
        ReceiveItem item = findItemFromList(key);
        View v = null;
        boolean isExitItemView = item != null;
        if (item == null) {
            v = getItemGiftView();
            item = new ReceiveItem(v, getNewGiftCountDownTimer(v));
            item.keyword = senderId + "_" + gift.getId();
            item.type = TYPE_GIFT_RECEIVE;
            ImageView logo = (ImageView) v.findViewById(R.id.logo);
            TextView name = (TextView) v.findViewById(R.id.sender_name);
            TextView sendText = (TextView) v.findViewById(R.id.send_tx);
            ImageView giftImg = (ImageView) v.findViewById(R.id.gift_img);
            gift.showGifImage(context, giftImg);
            name.setText(senderName);
            sendText.setText("送" + gift.getName());
            if (!TextUtils.isEmpty(senderLogo)) {
                GlideImgManager.getInstance().showImg(context,
                        logo, senderLogo);
            }

        } else {
            v = item.view;
        }

        TextView numText = (TextView) v.findViewById(R.id.send_num);
        String numTextStr = "";
        if (isExitItemView) {
            int oldNum = 0;
            try {
                oldNum = Integer.valueOf(numText.getText().toString());
            } catch (Exception e) {
                oldNum = 0;
            }
            numTextStr = (oldNum + sendNum) + "";
        } else {
            numTextStr = sendNum + "";
        }
        numText.setText(numTextStr);

        addGiftItem(item);
    }

    public void showUserCome(String userName, String userGrade) {
        if (TextUtils.isEmpty(userGrade)) {
            userGrade = "欢  迎";
        }
        String key = KEY_USER_PERSON;
        ReceiveItem item = findItemFromList(key);
        View v = null;
        if (item == null) {
            v = getItemPersonView();
            item = new ReceiveItem(v, getNewPersonCountDownTimer(v));
            item.keyword = KEY_USER_PERSON;
            item.type = TYPE_USER_COME;
        } else {
            v = item.view;
        }
        TextView gradeText = (TextView) v.findViewById(R.id.user_grade_text);
        TextView nameText = (TextView) v.findViewById(R.id.user_name);
        gradeText.setText(userGrade);
        nameText.setText(userName);

        addPersonItem(item);
    }

    private ReceiveItem findItemFromList(String keyWords) {
        for (ReceiveItem item : showList) {
            if (TextUtils.equals(keyWords, item.keyword)) {
                return item;
            }
        }
        return null;
    }

    protected View getItemGiftView() {
        View v = LayoutInflater.from(context).
                inflate(R.layout.receive_gift_layout, null);
        return v;
    }

    protected View getItemPersonView() {
        View v = LayoutInflater.from(context).
                inflate(R.layout.live_user_come_effect_layout, null);
        return v;
    }

    private void addGiftItem(ReceiveItem item) {
        if (item != null) {
            ReceiveItem tagItem = (ReceiveItem) item.view.getTag();
            View numLayout = item.view.findViewById(R.id.send_num_layout);
            if (tagItem == null) {//说明当前显示的集合里面没有这一条
                if (showList.add(item)) {
                    item.view.setTag(item);
                }
                addGiftItemView(item.view);
                item.downTimer.start();
                startScaleAnimation(numLayout);
            } else {
                item.downTimer.cancel();
                item.downTimer = getNewGiftCountDownTimer(item.view);
                item.downTimer.start();
                startScaleAnimation(numLayout);
            }
        }
    }

    private void addPersonItem(ReceiveItem item) {
        if (item != null) {
            ReceiveItem tagItem = (ReceiveItem) item.view.getTag();
            if (tagItem == null) {//说明当前显示的集合里面没有这一条
                if (showList.add(item)) {
                    item.view.setTag(item);
                }
                addPersonItemView(item.view);
                item.downTimer.start();
            } else {
                item.downTimer.cancel();
                item.downTimer = getNewPersonCountDownTimer(item.view);
                item.downTimer.start();
            }
        }
    }

    private CountDownTimer getNewGiftCountDownTimer(View item) {
        return new ViewCountDownTimer(item, this);
    }

    private CountDownTimer getNewPersonCountDownTimer(View item) {
        return new ViewCountDownTimer(item, 2 * 1000, this);
    }

    private void addGiftItemView(View v) {
        LayoutParams params = (LayoutParams) v.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        params.setMargins(0,
                0, 0, PixelUtil.dp2px(context, 15));
        params.gravity = Gravity.BOTTOM;
        Animation animation = AnimationUtils.
                loadAnimation(context, R.anim.receive_gift_come);
        v.startAnimation(animation);
        addView(v, params);
    }

    private void addPersonItemView(View v) {
        LayoutParams params = (LayoutParams) v.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        params.setMargins(0,
                0, 0, PixelUtil.dp2px(context, 10));
        params.gravity = Gravity.BOTTOM;
        Animation animation = AnimationUtils.
                loadAnimation(context, R.anim.anim_person_come);
        v.startAnimation(animation);
        addView(v, params);
    }

    private void startScaleAnimation(final View v) {
        float scaleMax = 1.5f;
        ScaleAnimation scaleBig = new ScaleAnimation(1f, scaleMax, 1f, scaleMax,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleBig.setDuration(200);

        final ScaleAnimation scaleSmall = new ScaleAnimation(scaleMax, 1f, scaleMax, 1f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleSmall.setDuration(200);

        scaleBig.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.startAnimation(scaleSmall);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(scaleBig);
    }

    @Override
    public void onRemoveView(View v) {
        ReceiveItem item = (ReceiveItem) v.getTag();
        showList.remove(item);
        if (item.type == TYPE_USER_COME) {
            Animation animation = AnimationUtils.
                    loadAnimation(context, R.anim.anim_person_out);
            v.startAnimation(animation);
        }
        removeView(v);
    }

    public class ReceiveItem {
        /**
         * 给View设置Tag为列表里面的下标
         */
        View view;
        CountDownTimer downTimer;
        /**
         * find key
         */
        String keyword;
        int type;

        public ReceiveItem(View v, CountDownTimer countDownTimer) {
            this.view = v;
            this.downTimer = countDownTimer;
        }
    }

    class ViewCountDownTimer extends CountDownTimer {

        private OnRemoveViewListener listener;
        private View view;

        public ViewCountDownTimer(View v, OnRemoveViewListener l) {
            super(VIEW_TIME, 1000);
            this.listener = l;
            this.view = v;
        }

        public ViewCountDownTimer(View v, long downTime, OnRemoveViewListener l) {
            super(downTime, 1000);
            this.listener = l;
            this.view = v;
        }

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public ViewCountDownTimer(View v, long millisInFuture, long countDownInterval,
                                  OnRemoveViewListener l) {
            super(millisInFuture, countDownInterval);
            this.listener = l;
            this.view = v;
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (listener != null) {
                listener.onRemoveView(view);
            }
        }
    }
}

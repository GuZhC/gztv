package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.AppManager;

import java.util.Date;

/**
 * Created by liuwb on 2016/6/30.
 */
public class LiveBottomBar extends LinearLayout implements View.OnClickListener {

    private Context context;

    private View iconView;
    private View editView;

    private TextView sendMsg;

    private ImageView share, sendGift;

    private EditText editMsg;
    private Button btnSend;

    private BarState currentState = BarState.normalState;
    /**
     * 禁言
     */
    private boolean isNoTalkTime = false;
    private NoTalkDownTimer noTalkDownTimer;

    private InputMethodManager im;

    public enum BarState {
        normalState, editState
    }

    private BarItemClickListener l;

    private OnBarStateChangeListener changeListener;

    public LiveBottomBar(Context context) {
        this(context, null);
    }

    public LiveBottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LiveBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.live_bottom_bar, this);
        iconView = findViewById(R.id.icon_layout);
        editView = findViewById(R.id.edit_layout);
        sendMsg = (TextView) findViewById(R.id.send_msg);
        share = (ImageView) findViewById(R.id.share);
        sendGift = (ImageView) findViewById(R.id.send_gift);
        editMsg = (EditText) findViewById(R.id.edit_text);
        btnSend = (Button) findViewById(R.id.btn_send);

        sendMsg.setOnClickListener(this);
        share.setOnClickListener(this);
        sendGift.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        setUpStateUI(BarState.normalState);

    }

    public void initSendBarNoTalkView(long roomId) {
        if (AppManager.getInstance().getIApp().isLogin()) {
            String keyWords = getNoTalkSaveKeywords(roomId);
            long endTime = getSavedNoTalkEndTime(keyWords);
            if (endTime >= 0) {
                setNoTalk(roomId, endTime);
            }
        }
    }

    /**
     * 设置禁言信息
     *
     * @param roomId
     * @param endNoTalkTime
     */
    public void setNoTalk(long roomId, long endNoTalkTime) {
        if (AppManager.getInstance().getIApp().isLogin()) {
            long curTime = new Date().getTime();
            long dTime = endNoTalkTime - curTime;
            if (dTime > 0) {
                isNoTalkTime = true;
                if (roomId != -1) {
                    startNoTalkDownTime(roomId, dTime);
                    saveNoTalkTimeData(getNoTalkSaveKeywords(roomId),
                            endNoTalkTime);
                }
            } else {
                setCancelNoTalk(roomId);
            }
            updateSendBtnImageView();
        }

    }

    public void setCancelNoTalk(long roomId) {
        isNoTalkTime = false;
        updateSendBtnImageView();
        if (AppManager.getInstance().getIApp().isLogin()) {
            clearNoTalkTimeData(getNoTalkSaveKeywords(roomId));
        }
    }

    private String getNoTalkSaveKeywords(long roomId) {
        return roomId + "_" +
                AppManager.getInstance().getIApp().getLoginUserId();
    }

    private void startNoTalkDownTime(long roomId, long downTime) {
        if (noTalkDownTimer != null) {
            noTalkDownTimer.cancel();
            noTalkDownTimer = null;
        }
        noTalkDownTimer = new NoTalkDownTimer(roomId, downTime);
        noTalkDownTimer.start();
    }

    private void cancelNoTalkTime() {
        if (noTalkDownTimer != null) {
            noTalkDownTimer.cancel();
        }
        noTalkDownTimer = null;
    }

    private void saveNoTalkTimeData(String keyword, long endNoTalkTime) {
        SharedPreferences sp = context.getSharedPreferences("LiveBottomBar_No_Talk_Timer", 0);
        SharedPreferences.Editor e = sp.edit();
        String endTimeKey = keyword + "_LiveBottomBar_No_Talk_Timer_EndTime";
        e.putLong(endTimeKey, endNoTalkTime);
        e.commit();
    }

    private void clearNoTalkTimeData(String keyword) {
        SharedPreferences sp = context.getSharedPreferences("LiveBottomBar_No_Talk_Timer", 0);
        SharedPreferences.Editor e = sp.edit();
        String endTimeKey = keyword + "_LiveBottomBar_No_Talk_Timer_EndTime";
        e.putLong(endTimeKey, 0);
        e.commit();
    }

    private long getSavedNoTalkEndTime(String keyword) {
        SharedPreferences sp = context.getSharedPreferences("LiveBottomBar_No_Talk_Timer", 0);
        String endTimeKey = keyword + "_LiveBottomBar_No_Talk_Timer_EndTime";
        return sp.getLong(endTimeKey, 0L);
    }

    private void updateSendBtnImageView() {
        String text = isNoTalkTime ? "禁言" : "要不要说两句?";
        sendMsg.setText(text);
    }

    /**
     * 设置送礼物的按钮是否可见
     *
     * @param isVisiable
     */
    public void setSendGiftViewVisiable(boolean isVisiable) {
        sendGift.setVisibility(isVisiable ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send_msg) {
            if (!isNoTalkTime) {
                setUpStateUI(BarState.editState);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        im.showSoftInput(editMsg, InputMethodManager.SHOW_FORCED);
                    }
                }, 300);
            }
        } else if (id == R.id.share) {
            if (l != null) {
                l.share();
            }

        } else if (id == R.id.send_gift) {
            if (l != null) {
                l.sendGift();
            }

        } else if (id == R.id.btn_send) {
            setUpStateUI(BarState.normalState);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (im.isActive()) {
                        im.hideSoftInputFromWindow(editMsg.getWindowToken(), 0);
                    }
                }
            }, 100);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (l != null) {
                        l.sendMsg(editMsg.getText().toString());
                        editMsg.setText("");
                    }
                }
            }, 200);
        }
    }

    public void setOnBarStateChangeListener(OnBarStateChangeListener l) {
        this.changeListener = l;
    }

    public void setBarItemListener(BarItemClickListener l) {
        this.l = l;
    }

    /**
     * 回到normal状态
     */
    public void backToNormal() {
        setUpStateUI(BarState.normalState);
        if (im.isActive()) {
            im.hideSoftInputFromWindow(editMsg.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.e("TAG", "bottom bar -----onDetachedFromWindow");
        super.onDetachedFromWindow();
        if (noTalkDownTimer != null) {
            noTalkDownTimer.cancel();
            noTalkDownTimer = null;
        }
    }

    public BarState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(BarState state) {
        setUpStateUI(currentState);
    }

    private void setUpStateUI(BarState state) {
        if (currentState != state) {
            if (changeListener != null) {
                changeListener.onChangeToState(state);
            }
        }
        currentState = state;
        if (state == BarState.normalState) {
            iconView.setVisibility(VISIBLE);
            editView.setVisibility(GONE);
            editMsg.clearFocus();
        } else {
            iconView.setVisibility(GONE);
            editView.setVisibility(VISIBLE);
            editMsg.requestFocus();
            editMsg.setSelection(0);
            editMsg.setFocusable(true);
            editMsg.setFocusableInTouchMode(true);
        }
    }

    public interface BarItemClickListener {
        void sendGift();

        void share();

        void sendMsg(String msg);
    }

    public interface OnBarStateChangeListener {
        void onChangeToState(BarState state);
    }

    class NoTalkDownTimer extends CountDownTimer {

        private long roomId;

        public NoTalkDownTimer(long roomId, long millisInFuture) {
            super(millisInFuture, 1000);
            this.roomId = roomId;
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            setCancelNoTalk(roomId);
        }
    }
}

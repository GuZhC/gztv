package com.dfsx.lzcms.liveroom.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.adapter.BaseViewHodler;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.LiveServiceRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.BaseListViewAdapter;
import com.dfsx.lzcms.liveroom.business.GuessRoomBet;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.dfsx.lzcms.liveroom.business.MyMoneyInfoManager;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.util.IntentUtil;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.jakewharton.rxbinding.view.RxView;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuwb on 2016/10/10.
 */
public class LiveGuessFragment extends Fragment {

    public static final int STATE_NO_START = 1;
    public static final int STATE_STARTING = 2;
    public static final int STATE_END = 3;

    private GuessListAdapter adapter;

    private Activity context;
    private Activity act;

    private RelativeLayout rootViewContainer;
    private ListView listView;
    private TextView downTimeText;
    private TextView timeSecondsText;
    private TextView guessStatusText;

    private TextView userCoinText;
    private TextView addCoinText;


    private RadioGroup betMoneyGroup;

    private RadioButton betMoney5;
    private RadioButton betMoney10;
    private RadioButton betMoney20;
    private RadioButton betMoney50;

    private GuessTimeDown guessTimeDown;

    private ImageView animImage;
    private int animLeft;
    private int animTop;
    private boolean isMove;

    private LiveChannelManager channelManager;
    private boolean isInitView;

    private GuessRoomBet beter;

    private IsLoginCheck loginCheck;

    private GuessRoomInfo guessRoomInfo;

    private int guessState = STATE_NO_START;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        act = context = getActivity();
        return inflater.inflate(R.layout.frag_guess_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        listView.setEmptyView(null);
        channelManager = new LiveChannelManager(context);
        loginCheck = new IsLoginCheck(context);
        isInitView = true;
        getGuessInfo();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (act instanceof LiveServiceRoomActivity) {
                        ((LiveServiceRoomActivity) act).resetBottomDrawerViewHeight();
                    }
                }
            });
        }
    }

    private void initView(View v) {
        rootViewContainer = (RelativeLayout) v.findViewById(R.id.bet_view_container);
        listView = (ListView) v.findViewById(R.id.list_view);
        userCoinText = (TextView) v.findViewById(R.id.user_coins_text);
        addCoinText = (TextView) v.findViewById(R.id.add_gold);
        betMoneyGroup = (RadioGroup) v.findViewById(R.id.guess_money_btn);
        betMoney5 = (RadioButton) v.findViewById(R.id.guess_5);
        betMoney10 = (RadioButton) v.findViewById(R.id.guess_10);
        betMoney20 = (RadioButton) v.findViewById(R.id.guess_20);
        betMoney50 = (RadioButton) v.findViewById(R.id.guess_50);
        setListAdapter(listView);
        updateCurrentUserMoneyInfo();

        addCoinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.goAddMoneyActivity(context);
            }
        });
    }

    public void setListAdapter(ListView listView) {
        addHeaderView();
        adapter = new GuessListAdapter(context);
        listView.setAdapter(adapter);

    }

    public void updateGuessInfo(GuessRoomInfo guessRoomInfo) {
        if (isInitView) {
            setTimeView(guessRoomInfo);
            //            getGuessBetPersonList(showId);
            if (guessRoomInfo.getBetOptionList() != null) {
                adapter.update(guessRoomInfo.getBetOptionList(), false);
            }

            setBetOptionState();
        }
    }

    private String getRoomEnterId() {
        String id = "";
        if (act instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) act).getRoomEnterId();
        }
        return id;
    }

    private void getGuessInfo() {
        channelManager.getGuessRoomInfo(getShowId(), getRoomEnterId(), new DataRequest.DataCallback<GuessRoomInfo>() {
            @Override
            public void onSuccess(boolean isAppend, GuessRoomInfo data) {
                guessRoomInfo = data;
                if (data != null) {
                    updateGuessInfo(data);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Toast.makeText(context, "获取竞猜信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public long getShowId() {
        if (act instanceof AbsChatRoomActivity) {
            return ((AbsChatRoomActivity) act).getRoomId();
        }
        return 0;
    }

    public void doReceiveBetMessage(BetGuessMessage message) {
        Log.e("TAG", "mes == " + message.getCoins());
        if (message != null) {
            double totalAmount = 0;
            try {
                //自己下注的的信息已经在下注成功之后显示了
                Map<String, Double> statMap = message.getStat();
                if (statMap != null) {
                    for (int i = 0; i < adapter.getData().size(); i++) {
                        BetOption option = adapter.getData().get(i);
                        String key = option.getId() + "";
                        totalAmount = statMap.get(key) != null ? statMap.get(key) : 0;
                        if (totalAmount != 0) {
                            option.setTotalAmount(totalAmount);
                            adapter.update(i, listView);
                        }
                    }
                    //                    handler.postDelayed(new Runnable() {
                    //                        @Override
                    //                        public void run() {
                    //                            adapter.notifyDataSetChanged();
                    //                        }
                    //                    }, 10);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BetOption getBetOptionById(long oid) {
        if (guessRoomInfo != null && guessRoomInfo.getBetOptionList() != null) {
            for (BetOption option : guessRoomInfo.getBetOptionList()) {
                if (option.getId() == oid) {
                    return option;
                }
            }
        }
        return null;
    }


    private void getGuessBetPersonList(long showId) {
        channelManager.getGuessRoomBetPersonList(showId, new DataRequest.DataCallback<List<BetPersonInfo>>() {
            @Override
            public void onSuccess(boolean isAppend, List<BetPersonInfo> data) {
                if (data != null) {
                    //                    adapter.update(data, isAppend);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Log.e("TAG", "getGuessRoomBetPersonList fail");
            }
        });
    }

    private void setTimeView(GuessRoomInfo guessRoomInfo) {
        if (guessRoomInfo != null) {
            long currentTime = new Date().getTime();
            long startTime = guessRoomInfo.getStartBetTime() * 1000;
            long endTime = guessRoomInfo.getStopBetTime() * 1000;
            if (endTime > startTime) {
                long countDownTime = 0;
                if (currentTime < startTime) {
                    guessState = STATE_NO_START;
                    countDownTime = startTime - currentTime;
                } else if (currentTime < endTime) {
                    guessState = STATE_STARTING;
                    countDownTime = endTime - currentTime;
                } else {
                    guessState = STATE_END;
                    countDownTime = 0;
                }
                setTimeViewShowText(countDownTime);
                guessStatusText.setText(getStateText());
                if (countDownTime > 0) {
                    if (guessTimeDown != null) {
                        guessTimeDown.cancel();
                        guessTimeDown = null;
                    }
                    guessTimeDown = new GuessTimeDown(countDownTime);
                    guessTimeDown.start();
                }
            }

        }
    }

    private void addHeaderView() {
        View header = LayoutInflater.from(context).
                inflate(R.layout.guess_header_layout, null);
        listView.addHeaderView(header);

        downTimeText = (TextView) header.findViewById(R.id.time_text);
        timeSecondsText = (TextView) header.findViewById(R.id.time_seconds_per_text);
        guessStatusText = (TextView) header.findViewById(R.id.guess_status);
    }

    public void reloginSuccess() {
        updateCurrentUserMoneyInfo();
    }

    /**
     * 跟新当前页面的人金币信息
     */
    private void updateCurrentUserMoneyInfo() {

        MyMoneyInfoManager.getInstance().getMoneyInfo(context, new ICallBack<UserMoneyInfo>() {
            @Override
            public void callBack(UserMoneyInfo data) {
                double userCoins = 0;
                if (data != null) {
                    userCoins = data.getCoins();
                }
                setUserCoinText(userCoins);
            }
        });
    }

    private void setUserCoinText(double coins) {
        userCoinText.setText(StringUtil.moneyToString(coins));
    }

    /**
     * 当前是否还可以押注
     *
     * @return
     */
    private boolean isCouldBetCoin() {
        if (guessRoomInfo != null) {
            boolean isBetTime = guessRoomInfo.getResult() == 0 &&
                    guessState == STATE_STARTING;
            return isBetTime;
        }
        return false;
    }

    protected void betOptionClick(BetOption betOption) {
        if (isCouldBetCoin()) {
            double coin = getSelectedBetMoney();
            if (guessRoomInfo != null) {
                betTeam(coin, betOption);
            }
        }
    }

    private void setBetOptionState() {
        if (adapter.getData() != null) {
            boolean isEnd = !isCouldBetCoin();
            for (BetOption option : adapter.getData()) {
                option.setEnableBet(!isEnd);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void betTeam(double coin, final BetOption betOption) {
        if (TextUtils.isEmpty(getRoomEnterId())) {
            Toast.makeText(context, "当前不在房间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!loginCheck.checkLogin()) {
            return;
        }
        if (beter == null) {
            beter = new GuessRoomBet(getShowId(), getRoomEnterId());
        }
        beter.betTeam(betOption, coin, new ICallBack<BetResponse>() {
            @Override
            public void callBack(BetResponse data) {
                if (data.isSuccess()) {
                    if (act != null && !act.isFinishing()) {
                        View toView = null;
                        if (data.getBetOption() != null) {
                            updateUserBetOptionView(data);
                            int pos = data.getBetOption().getListPosition();
                            int firstVisiblePosition = listView.getFirstVisiblePosition();
                            int lastVisiablePosition = listView.getLastVisiblePosition();
                            // 获取当前显示item  的个数
                            int childCount = listView.getChildCount();
                            int headerCount = listView.getHeaderViewsCount();
                            int i = pos + headerCount;
                            if (i >= firstVisiblePosition && i <= lastVisiablePosition) {
                                i = i - firstVisiblePosition;
                                while (i > childCount - 1) {
                                    i = i - firstVisiblePosition;
                                }
                                if (i >= 0) {
                                    toView = listView.getChildAt(i).findViewById(R.id.item_bet_option_view);
                                }
                            }
                        }
                        if (toView != null) {
                            animToTagOnWindows(act, getSelectedBetMoneyView(), toView);
                        }
                    }
                } else {
                    if (data.getErrorCode() == 10010 ||
                            (MyMoneyInfoManager.getInstance().getCacheMoneyInfo() != null &&
                                    MyMoneyInfoManager.getInstance().getCacheMoneyInfo().getCoins() <= 0)) {
                        showNoEnoughMoneyDialog();
                    } else {
                        String errorText = TextUtils.isEmpty(data.getErrorMsg()) ? "押注" +
                                data.getBetOption().getName() + "失败 " :
                                data.getErrorMsg();
                        String text = data.isSuccess() ? "押注" + data.getBetOption().getName() + "成功" :
                                errorText;
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("TAG", "fail msg == " + data.getErrorMsg());
            }
        });
    }

    private void updateUserBetOptionView(final BetResponse data) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int pos = data.getBetOption().getListPosition();
                if (adapter.getData() != null && pos >= 0 && pos < adapter.getCount()) {
                    double coins = adapter.getData().get(pos).getCurrentUserBetCoins();
                    coins += data.getBetCoins();
                    adapter.getData().get(pos).setCurrentUserBetCoins(coins);
                    //                    adapter.notifyDataSetChanged();
                    adapter.update(pos, listView);
                }
                if (MyMoneyInfoManager.getInstance().getCacheMoneyInfo() != null) {
                    MyMoneyInfoManager.getInstance()
                            .payMoney(data.getBetCoins());
                    double money = MyMoneyInfoManager.getInstance().getCacheMoneyInfo().getCoins();
                    setUserCoinText(money);
                }
            }
        }, 300);

    }


    public String getRoomName() {
        if (act instanceof AbsChatRoomActivity) {
            return ((AbsChatRoomActivity) act).getRoomName();
        }
        return null;
    }

    private void showNoEnoughMoneyDialog() {
        if (act != null && !act.isFinishing()) {
            LXDialog.Builder builder = new LXDialog.Builder(act);
            LXDialog dialog = builder.setMessage("账户余额不足,请充值")
                    .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, View v) {
                            IntentUtil.goAddMoneyActivity(act);
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    private int getSelectedBetMoney() {
        int m = 0;
        if (betMoneyGroup != null) {
            int checkedId = betMoneyGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.guess_5) {
                m = 5;
            } else if (checkedId == R.id.guess_10) {
                m = 10;
            } else if (checkedId == R.id.guess_20) {
                m = 20;
            } else if (checkedId == R.id.guess_50) {
                m = 50;
            }
        }
        return m;
    }

    private View getSelectedBetMoneyView() {
        View v = betMoney5;
        if (betMoneyGroup != null) {
            int checkedId = betMoneyGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.guess_5) {
                v = betMoney5;
            } else if (checkedId == R.id.guess_10) {
                v = betMoney10;
            } else if (checkedId == R.id.guess_20) {
                v = betMoney20;
            } else if (checkedId == R.id.guess_50) {
                v = betMoney50;
            }
        }
        return v;
    }

    class GuessListAdapter extends BaseListViewAdapter<BetOption> {

        public GuessListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_bet_option_layout;
        }

        public void update(int index, ListView listview) {
            //得到第一个可见item项的位置
            int visiblePosition = listview.getFirstVisiblePosition();
            //得到指定位置的视图，对listview的缓存机制不清楚的可以去了解下
            int pos = index + listview.getHeaderViewsCount();
            View view = listview.getChildAt(pos - visiblePosition);
            BaseViewHodler holder = (BaseViewHodler) view.getTag();
            if (holder != null) {
                setItemViewData(holder, index);
            }
        }

        @Override
        public void setItemViewData(final BaseViewHodler holder, final int position) {
            View itemView = holder.getView(R.id.item_bet_option_view);
            CircleButton logo = holder.getView(R.id.item_logo);
            ImageView itemEnableImage = holder.getView(R.id.item_bg_cover_img);
            TextView name = holder.getView(R.id.item_bet_name);
            TextView totalCoinText = holder.getView(R.id.bet_total_coins_text);
            TextView userCoinText = holder.getView(R.id.user_bet_coins_text);

            BetOption option = list.get(position);
            GlideImgManager.getInstance().showImg(context,
                    logo, option.getOptionLogo());
            name.setText(option.getName());

            totalCoinText.setText(((int) option.getTotalAmount()) + "");
            userCoinText.setText(((int) option.getCurrentUserBetCoins()) + "");
            itemEnableImage.setVisibility(option.isEnableBet() ? View.GONE : View.VISIBLE);
            RxView.clicks(itemView)
                    .throttleFirst(600, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            BetOption clickOption = list.get(position);
                            clickOption.setListPosition(position);
                            //                            clickOption.setClickView(holder.getView(R.id.item_bet_option_view));
                            betOptionClick(clickOption);
                        }
                    });
            itemEnableImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //什么都不做，只是拦截掉点击事件
                }
            });
        }
    }

    public void animToTagOnWindows(Activity activity, View tagView, View toView) {
        int[] toXY = new int[2];
        toView.getLocationOnScreen(toXY);

        int centerX = (int) (toXY[0] + toView.getMeasuredWidth() / 2f);
        int centerY = (int) (toXY[1] + toView.getMeasuredHeight() / 2f);
        animToTagOnWindows(activity, tagView, centerX, centerY);
    }

    public void animToTagOnWindows(Activity activity, View tagView, final int toCenterX, final int toCenterY) {
        int[] winXY = new int[2];
        tagView.getLocationOnScreen(winXY);

        int toX = toCenterX - tagView.getMeasuredWidth() / 2;
        int toY = toCenterY - tagView.getMeasuredHeight() / 2;
        final int fromX = winXY[0];
        final int fromY = winXY[1];

        AnimatorSet animaSet = new AnimatorSet();
        ValueAnimator xAnimator = ValueAnimator.ofInt(fromX, toX);
        ValueAnimator yAnimator = ValueAnimator.ofInt(fromY, toY);
        animaSet.play(xAnimator).with(yAnimator);
        animaSet.setDuration(300);
        xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animLeft = (int) animation.getAnimatedValue();
            }
        });

        yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animTop = (int) animation.getAnimatedValue();
            }
        });

        animaSet.start();
        isMove = true;

        animImage = new ImageView(activity);
        if (animImage.getParent() != null) {
            ((ViewGroup) animImage.getParent()).removeView(animImage);
        }
        animImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Bitmap tempBm = getViewBitmap(tagView);
        animImage.setImageBitmap(tempBm);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(tagView.getMeasuredWidth(), tagView.getMeasuredHeight());
        params.setMargins(winXY[0], winXY[1], 0, 0);
        animImage.setLayoutParams(params);

        final FrameLayout frameLayout = (FrameLayout) activity.getWindow().getDecorView().getRootView();
        frameLayout.addView(animImage);

        animImage.post(new Runnable() {
            @Override
            public void run() {
                if (isMove) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                            animImage.getLayoutParams();
                    if (params == null) {
                        params = new FrameLayout.LayoutParams(animImage.getMeasuredWidth(),
                                animImage.getMeasuredHeight());
                    }
                    params.leftMargin = animLeft;
                    params.topMargin = animTop;
                    animImage.setLayoutParams(params);

                    animImage.postDelayed(this, 30);
                }
            }
        });

        final AnimatorSet scaleAnim = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(animImage, "scaleX", 1f, 1.2f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(animImage, "scaleY", 1f, 1.2f, 0f);
        scaleAnim.play(scaleX).with(scaleY);
        scaleAnim.setDuration(300);

        scaleAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //移除临时显示动画的view
                frameLayout.removeView(animImage);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animaSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isMove = false;
                scaleAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isMove = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Folder", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    private String timeToString(long time) {
        long curTime = new Date().getTime();
        long dSecond = Math.abs(time - curTime / 1000);
        String text = "";
        if (dSecond / 3600 < 1) {
            int m = (int) (dSecond / 60);
            if (m < 5) {
                text = "刚刚";
            } else {
                text = m + "分钟前";
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm");
            text = sdf.format(new Date(time * 1000));
        }
        return text;
    }

    private String getStateText() {
        if (guessState == STATE_NO_START) {
            return "竞猜即将开始";
        } else if (guessState == STATE_STARTING) {
            return "正在比赛";
        } else {
            return "竞猜已结束";
        }
    }

    private void onDownTimeFinish() {
        setTimeView(guessRoomInfo);
        downTimeText.setVisibility(View.VISIBLE);
        setBetOptionState();
    }

    private void setTimeViewShowText(long time) {
        long allSeconds = time / 1000;
        if (allSeconds <= 180) {
            String timeText = allSeconds <= 0 ? "00" : allSeconds + "";
            downTimeText.setText(timeText);
            timeSecondsText.setVisibility(View.VISIBLE);
            if (guessStatusText != null) {
                String text = getStateText();
                if (allSeconds > 0 && guessState == STATE_STARTING) {
                    text = "竞猜即将结束";
                }
                guessStatusText.setText(text);
            }
        } else {
            int hour = (int) (allSeconds / (60 * 60));
            int minutes = (int) (allSeconds % (60 * 60) / 60);
            int seconds = (int) ((allSeconds % (60 * 60)) % 60);

            String textHour = String.format("%02d", hour);
            String textMinutes = String.format("%02d", minutes);
            downTimeText.setText(textHour + ":" + textMinutes);
            timeSecondsText.setVisibility(View.GONE);

        }


    }

    class GuessTimeDown extends CountDownTimer {

        public GuessTimeDown(long allTime) {
            super(allTime, 1000);
        }

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public GuessTimeDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setTimeViewShowText(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            onDownTimeFinish();

        }
    }
}

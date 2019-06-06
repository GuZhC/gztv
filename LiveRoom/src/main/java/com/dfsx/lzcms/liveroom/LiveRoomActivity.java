package com.dfsx.lzcms.liveroom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.fragment.LiveChatFragment;
import com.dfsx.lzcms.liveroom.fragment.LiveGuessFragment;
import com.dfsx.lzcms.liveroom.fragment.LiveGuessRulesFragment;
import com.dfsx.lzcms.liveroom.fragment.LivePlayerInfoFragment;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.view.BarrageBufferView;
import com.dfsx.lzcms.liveroom.view.NoOwnerLiveDialog;
import com.dfsx.videoijkplayer.VideoPlayView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

/**
 * 现在设定为竞猜的直播间
 * Created by liuwb on 2016/10/10.
 */
public class LiveRoomActivity extends AbsVideoActivity {

    private Activity act;

    private Context context;

    private View portraitLayout;
    private FrameLayout fullScreenLayout;
    private FrameLayout portraintContainer;
    private RadioGroup radioBar;
    private ViewPager viewPager;
    private View topListView;
    private BarrageBufferView videoBarrageView;

    private int currentCheckId = R.id.bar_chat;

    private LiveChatFragment chatFragment;

    private LiveGuessFragment guessFragment;

    private LivePlayerInfoFragment playerInfoFragment;

    private LiveGuessRulesFragment rulesFragment;

    private NoOwnerLiveDialog concernDalog;

    private GuessRoomInfo guessRoomInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        context = this;
        setContentView(R.layout.act_live_room);

        initView();
        initAction();

    }

    @Override
    public void addVideoPlayerToContainer(VideoPlayView videoPlayer) {
        fullScreenLayout.setVisibility(View.GONE);
        portraitLayout.setVisibility(View.VISIBLE);
        videoPlayer.removeView(videoBarrageView);
        if (portraintContainer.getChildCount() <= 0 ||
                !(portraintContainer.getChildAt(0) instanceof VideoPlayView)) {
            portraintContainer.addView(videoPlayer, 0);
        }
    }

    @Override
    public void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer) {
        fullScreenLayout.setVisibility(View.VISIBLE);
        portraitLayout.setVisibility(View.GONE);
        videoPlayer.addView(videoBarrageView);
        if (fullScreenLayout.getChildCount() <= 0 ||
                !(fullScreenLayout.getChildAt(0) instanceof VideoPlayView)) {
            fullScreenLayout.addView(videoPlayer, 0);
        }

    }

    private void initView() {
        topListView = findViewById(R.id.top_list_view);
        portraitLayout = findViewById(R.id.portrait_layout);
        fullScreenLayout = (FrameLayout) findViewById(R.id.full_screen_video_container);
        portraintContainer = (FrameLayout) findViewById(R.id.portrait_video_container);
        viewPager = (ViewPager) findViewById(R.id.pager);
        radioBar = (RadioGroup) findViewById(R.id.live_radio_bar);
        videoBarrageView = new BarrageBufferView(context);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        videoBarrageView.setLayoutParams(p);
        videoBarrageView.setTextColor(Color.WHITE);
    }

    private void initAction() {

        radioBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentCheckId = checkedId;
                viewPager.setCurrentItem(getSelectedPagerCount(checkedId));
            }
        });

        radioBar.check(currentCheckId);

        topListView.setOnTouchListener(touchListener);

        for (int i = 0; i < radioBar.getChildCount(); i++) {
            View v = radioBar.getChildAt(i);
            v.setOnTouchListener(touchListener);
        }

        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                getFragments());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioBar.check(getCheckIdByPos(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void joinChatRoomStatus(ApiException error) {
        super.joinChatRoomStatus(error);
        if (error != null) {
            getGuessRoomInfo(getRoomId());
        }
    }

    @Override
    public void onLiveStartMessage(LiveStartMessage message) {
        super.onLiveStartMessage(message);
        String url = "http://live.ysxtv.cn:8081/Ch1/playlist.m3u8";
        if (message != null && message.getLivetreamList() != null &&
                !message.getLivetreamList().isEmpty() &&
                !TextUtils.isEmpty(message.getLivetreamList().get(0).getFlvAddress())) {
            url = message.getLivetreamList().get(0).getFlvAddress();
        }
        if (videoPlayer != null && !videoPlayer.isPlay()) {
            videoPlayer.start(url);
        }
        if (concernDalog != null) {
            concernDalog.dismiss();
        }
    }

    @Override
    public void onLiveEndMessage(LiveEndMessage message) {
        super.onLiveEndMessage(message);
        videoPlayer.stop();
        if (!showConcernDialog()) {
            Toast.makeText(context, "直播已停止", Toast.LENGTH_SHORT).show();
        }
    }

    private long roomShowId;

    private void getGuessRoomInfo(final long showId) {
        if (showId == 0) {
            return;
        }
        roomShowId = showId;
        if (guessRoomInfo == null) {
            channelManager.getGuessRoomInfo(showId, getRoomEnterId(), new DataRequest.DataCallback<GuessRoomInfo>() {
                @Override
                public void onSuccess(boolean isAppend, GuessRoomInfo data) {
                    guessRoomInfo = data;
                    if (guessRoomInfo != null) {
                        playerInfoFragment.updateGuessInfo(guessRoomInfo);
                        rulesFragment.updateGuessInfo(guessRoomInfo);
                    }
                }

                @Override
                public void onFail(ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "获取竞猜信息是失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 获取当前房间的信息
     *
     * @return
     */
    public GuessRoomInfo getGuessRoomInfo() {
        return guessRoomInfo;
    }

    public long getShowId() {
        return roomShowId;
    }

    private boolean showConcernDialog() {
        if (concernDalog == null) {
            concernDalog = new NoOwnerLiveDialog(this);
            getRoomDeatils(new ICallBack<RoomDetails>() {
                @Override
                public void callBack(final RoomDetails roomInfo) {
                    if (roomInfo != null) {
                        channelManager.isConcern(
                                roomInfo.getOwner_id(),
                                new DataRequest.DataCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(boolean isAppend, Boolean data) {
                                        if (!data) {
                                            concernDalog.show(roomInfo.getOwner_id());
                                        }
                                    }

                                    @Override
                                    public void onFail(ApiException e) {
                                        concernDalog.show(roomInfo.getOwner_id());
                                    }
                                }
                        );
                    }
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onReLoginPlatformSuccess() {
        super.onReLoginPlatformSuccess();
        if (guessFragment != null) {
            guessFragment.reloginSuccess();
        }
    }

    @Override
    public void onBackPressed() {
        if (videoPlayer.isFullScreen()) {
            videoPlayer.switchScreen();
            return;
        }
        super.onBackPressed();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        chatFragment = new LiveChatFragment();
        fragments.add(chatFragment);
        guessFragment = new LiveGuessFragment();
        fragments.add(guessFragment);
        playerInfoFragment = new LivePlayerInfoFragment();
        fragments.add(playerInfoFragment);
        rulesFragment = new LiveGuessRulesFragment();
        fragments.add(rulesFragment);

        return fragments;
    }

    //    private void createTextLiveRoom() {
    //        final String nickName = AndroidUtil.getUniqueId(context);
    //        Observable.just(null).subscribeOn(Schedulers.io()).
    //                map(new Func1<Object, Boolean>() {
    //                    @Override
    //                    public Boolean call(Object o) {
    //                        return XmppRoomManager.getInstance().
    //                                joinRoomAnyway(ROOM_NAME, nickName, null, receiveMessageListener);
    //                    }
    //                }).observeOn(AndroidSchedulers.mainThread()).
    //                subscribe(new Action1<Boolean>() {
    //                    @Override
    //                    public void call(Boolean aBoolean) {
    //                        if (aBoolean) {
    //                            Toast.makeText(context, "进入房间成功", Toast.LENGTH_SHORT).show();
    //                        }
    //                    }
    //                });
    //    }


    public void hideInput() {
        if (chatFragment != null) {
            chatFragment.hideInput();
        }
    }

    private int getSelectedPagerCount(int checkedId) {
        int count = 0;
        if (checkedId == R.id.bar_guess) {
            count = 1;
        } else if (checkedId == R.id.bar_player) {
            count = 2;
        } else if (checkedId == R.id.bar_rule) {
            count = 3;
        } else {
            count = 0;
        }
        return count;
    }

    private int getCheckIdByPos(int pos) {
        int id = 0;
        if (pos == 1) {
            id = R.id.bar_guess;
        } else if (pos == 2) {
            id = R.id.bar_player;
        } else if (pos == 3) {
            id = R.id.bar_rule;
        } else {
            id = R.id.bar_chat;
        }
        return id;
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onReceiveUserChatMessage(final List<LiveMessage> message) {
        super.onReceiveUserChatMessage(message);
        //        if (message != null) {
        //            if (videoPlayer != null && videoPlayer.isFullScreen()) {
        //                handler.post(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        String messageStr = message.getBody();
        //                        boolean isGift = messageStr.contains("<gift");
        //                        if (!isGift) {
        //                            String content = message.getBody();
        //                            videoBarrageView.sendBarrage(content);
        //                        }
        //                    }
        //                });
        //            } else {
        //                if (chatFragment != null) {
        //                    chatFragment.processMessage(message);
        //                }
        //            }
        //        }
    }


    @Override
    public void onBetMessage(BetGuessMessage message) {
        super.onBetMessage(message);
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BetGuessMessage>() {
                    @Override
                    public void call(BetGuessMessage betMessage) {
                        if (guessFragment != null) {
                            guessFragment.doReceiveBetMessage(betMessage);
                        }
                    }
                });
    }

    @Override
    public void onGuessResultMessage(GuessResultMessage message) {
        super.onGuessResultMessage(message);
        doReceiveBetResultMessage(message);
    }

    /**
     * 处理押注的消息
     */
    protected void doReceiveBetMessage(BetGuessMessage betMessage) {

    }

    /**
     * 竞猜结束所受到的消息
     *
     * @param betResultMessage
     */
    protected void doReceiveBetResultMessage(GuessResultMessage betResultMessage) {

    }

    @Override
    protected void onReceiveUserGiftMessage(List<GiftMessage> message) {
        super.onReceiveUserGiftMessage(message);
        //        if (chatFragment != null && message != null) {
        //            chatFragment.doReceiveGift(message);
        //        }
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (currentCheckId == R.id.bar_chat) {
                hideInput();
            }
            return false;
        }
    };

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        //        @Override
        //        public int getItemPosition(Object object) {
        //            return POSITION_NONE;
        //        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            fm.beginTransaction().hide(fragment);
        }
    }
}

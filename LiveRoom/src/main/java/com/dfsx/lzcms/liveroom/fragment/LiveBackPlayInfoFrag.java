package com.dfsx.lzcms.liveroom.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.common.view.OnCirbuttonClickListener;
import com.dfsx.core.exception.ApiException;
import com.dfsx.lzcms.liveroom.LiveBackPlayFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.business.LiveChannelManager;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.*;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2016/12/14.
 */
public class LiveBackPlayInfoFrag extends LiveInfoFrag {

    private static final int MESSAGE_UPDATE_PROGRESS = 22;


    private InterceptTouchRelativeLayout layoutContainer;

    private LiveBackPlayBottomBar backPlayBottomBar;

    private TextView startTimeText;

    private TextView endTimeText;

    private SeekBar playProgressBar;

    private LiveFullRoomVideoPlayer videoPlayer;

    private boolean isDragging = false;

    private List<LRCRow> chatDataList;

    private GuessRoomInfo guessRoomInfo;

    private View topInfoView;

    private long lastestMessageId;
    /**
     * 获取跟多数据的ID
     */
    private long getMoreMessageId;

    private List<IChatData> chatMessageDataList;
    private int pullStateCount;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_UPDATE_PROGRESS) {
                setProgress();
                if (!isDragging) {
                    msg = obtainMessage(MESSAGE_UPDATE_PROGRESS);
                    sendMessageDelayed(msg, 1000);
                }
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        channelManager = new LiveChannelManager(activity);
        loginCheck = new IsLoginCheck(activity);
        rooView = inflater.inflate(R.layout.frag_live_back_play, null);
        initUserInfoPopupwindow();
        return rooView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAction();
        initData();
    }

    private void initView(View v) {
        layoutContainer = (InterceptTouchRelativeLayout) v.findViewById(R.id.intercept_up_view);
        heartBubbleView = (FloatHeartBubbleSurfaceView) v.findViewById(R.id.heart_view);
        liveSpecialEffectView = (LiveSpecialEffectView) v.findViewById(R.id.receive_gift_view);
        barrageListView = (BarrageListViewSimple) v.findViewById(R.id.barrage_list_view);
        liveValueView = v.findViewById(R.id.live_value);
        userNameText = (TextView) v.findViewById(R.id.user_name);
        userIdText = (TextView) v.findViewById(R.id.user_id);
        userLogo = (CircleButton) v.findViewById(R.id.room_user_logo);
        addConcernbtn = (ImageView) v.findViewById(R.id.add_concern_img);
        roomMoneyValue = (TextView) v.findViewById(R.id.total_value);
        backPlayBottomBar = (LiveBackPlayBottomBar) v.findViewById(R.id.back_play_bottom_bar);
        startTimeText = (TextView) v.findViewById(R.id.start_time_text);
        endTimeText = (TextView) v.findViewById(R.id.end_time_text);
        playProgressBar = (SeekBar) v.findViewById(R.id.back_play_progress);
        topInfoView = v.findViewById(R.id.top_view);
        topVideoSwitchToFull = (ImageView) v.findViewById(R.id.switch_top_video_to_full);

        setDraggingTouch();
        setSeekBarTouchEnable();
        autoSendFloatHeart = new AutoSendFloatHeart(heartBubbleView);

        barrageListView.setRefreshMode(PullToRefreshBase.Mode.PULL_FROM_END);
        barrageListView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<RecyclerView>() {
            @Override
            public void onPullEvent(PullToRefreshBase<RecyclerView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
                if (state == PullToRefreshBase.State.PULL_TO_REFRESH) {
                    pullStateCount = 0;
                } else {
                    ++pullStateCount;
                }
                if (pullStateCount == 1) {
                    getMoreChatMessageData();
                } else if (pullStateCount > 1) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            barrageListView.onRefreshComplete();
                        }
                    }, 200);
                }
            }
        });
    }

    private void getMoreChatMessageData() {
        long messageId = getMoreMessageId;
        if (messageId != 0 && messageId != lastestMessageId) {
            lastestMessageId = messageId;
            getChatData(messageId);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    barrageListView.onRefreshComplete();
                }
            }, 200);
        }
    }

    private void setDraggingTouch() {
        ChildInterceptTouchEventDrawerLayout drawerLayout = getDrawerLayout();
        if (drawerLayout != null) {
            drawerLayout.addInterceptTouchEventChildId(playProgressBar.getId());
        }
    }

    private void initAction() {
        layoutContainer.setInterceptTouchListener(new InterceptTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    if (backPlayBottomBar != null && backPlayBottomBar.isEditState()) {
                        backPlayBottomBar.showToolView();
                        return true;
                    }
                }
                return false;
            }
        });
        topVideoSwitchToFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayer != null) {
                    videoPlayer.switchTopVideoScreen(getActivity());
                }
            }
        });
        addConcernbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConcern();
            }
        });
        userLogo.setOnCirbuttonClickListener(new OnCirbuttonClickListener() {
            @Override
            public boolean onTouchEventSS(View v, MotionEvent event) {
                userLogoClick();
                return false;
            }
        });

        backPlayBottomBar.setOnEventClickListener(new LiveBackPlayBottomBar.OnEventClickListener() {
            @Override
            public void onShareClick(View v) {
                shareClick();
            }

            @Override
            public void onVideoPauseClick(View v) {
                videoPlayControlClick();
            }

            @Override
            public void onGiftClick(View v) {
                sendGiftClick();
            }

            @Override
            public void onChatSendClick(View v, String text) {
                sendMsgClick(text);
            }
        });

        playProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration = videoPlayer.getVideoListDuration();
                String string = generateTime((long) (duration * progress * 1.0f / 100));
                startTimeText.setText(string);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgress();
                isDragging = true;
                stopUpdateProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                int duration = videoPlayer.getVideoListDuration();
                if (duration > 0) {
                    videoPlayer.seekVideoListTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                }
                startUpdateProgress();
            }
        });

        liveValueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLiveValueAct();
            }
        });
    }

    private void videoPlayControlClick() {
        if (videoPlayer != null) {
            if (videoPlayer.isPlaying()) {
                videoPlayer.pause();
                setBtnPlayStatus(false);
            } else {
                if (videoPlayer.isOnComplete()) {
                    videoPlayer.restart();
                    startUpdateProgress();
                    if (!autoSendFloatHeart.isStarted()) {
                        autoSendFloatHeart.start(20);
                    }
                } else {
                    videoPlayer.start();
                }
                setBtnPlayStatus(true);
            }

        }
    }

    private void initData() {
        if (activity instanceof LiveBackPlayFullScreenRoomActivity) {
            LiveBackPlayFullScreenRoomActivity act = (LiveBackPlayFullScreenRoomActivity) activity;
            updateOwnerView(act.getOwnerInfo(), act.getRoomMemberSize());
            initConcernBtnStatus();
            updateRoomMoney(act.getRoomCoins());
        }
    }

    public void setChatData(List<LRCRow> chatData) {
        if (chatDataList == null) {
            chatDataList = chatData;
        } else {
            if (chatData != null) {
                chatDataList.addAll(chatData);
            }
        }
    }

    private void setChatDataShow() {
        if (chatDataList != null && chatDataList.size() > 0 && videoPlayer != null) {
            long position = videoPlayer.getCurrentVideoListTime();
            if (position != 0) {
                setChatShowTime(position);
            }
        }
    }

    protected void getChatData(long beforeMessageId) {
        if (activity instanceof LiveBackPlayFullScreenRoomActivity) {
            ((LiveBackPlayFullScreenRoomActivity) activity).getChatData(beforeMessageId);
        }
    }

    public void updateChatDataError(ApiException e) {
        barrageListView.onRefreshComplete();
    }

    public void updateChatData(boolean isAdd, List<IChatData> list) {
        barrageListView.onRefreshComplete();
        if (isAdd && chatMessageDataList != null) {
            if (list != null) {
                chatMessageDataList.addAll(list);
            }
        } else {
            chatMessageDataList = list;
        }
        /**
         * 设置加载跟多的ID
         */
        if (chatMessageDataList != null && chatMessageDataList.size() > 0) {
            IChatData data = chatMessageDataList.get(chatMessageDataList.size() - 1);
            if (data != null) {
                getMoreMessageId = data.getChatId();
            } else {
                getMoreMessageId = 0;
            }
        } else {
            getMoreMessageId = 0;
        }

        if (list != null && !list.isEmpty()) {
            ArrayList<BarrageListViewSimple.BarrageItem> itemList = new ArrayList<>();
            for (IChatData data : list) {
                BarrageListViewSimple.BarrageItem item =
                        new BarrageListViewSimple.BarrageItem(data.getChatUserNickName(),
                                data.getChatContentText(), data.getUserLevelId());
                itemList.add(item);
            }
            if (isAdd && barrageListView.getData() != null) {
                barrageListView.getData().addAll(itemList);
            } else {
                barrageListView.setData(itemList);
            }
            barrageListView.updateAdapter();
        }
    }

    public void setGuessRoomInfo(GuessRoomInfo guessRoomInfo) {
        this.guessRoomInfo = guessRoomInfo;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoSendFloatHeart.start(memberSize);
    }

    @Override
    public void onPause() {
        super.onPause();
        autoSendFloatHeart.stop();
    }

    @Override
    protected void setOnlineNumText() {
        //回放里面暂时不显示回看的信息
        //        handler.post(new Runnable() {
        //            @Override
        //            public void run() {
        //                onlineNumText.setText(memberSize + " 看过");
        //            }
        //        });
    }

    private void setChatShowTime(long time) {
        //        Observable.just(time)
        //                .observeOn(Schedulers.io())
        //                .flatMap(new Func1<Long, Observable<LRCRow>>() {
        //                    @Override
        //                    public Observable<LRCRow> call(Long aLong) {
        //                        ArrayList<LRCRow> timeList = new ArrayList<LRCRow>();
        //                        double aTime = aLong / 1000.0d;
        //                        long timeMin = (long) (Math.floor(aTime) * 1000);
        //                        long timeMax = (long) (Math.ceil(aTime) * 1000);
        //                        long atTime = timeMin == timeMax ? timeMax : 0;
        //                        for (LRCRow r : chatDataList) {
        //                            if (atTime != 0) {
        //                                if (r.getdTimeStamp() > atTime) {
        //                                    break;
        //                                } else if (r.getdTimeStamp() == atTime) {
        //                                    timeList.add(r);
        //                                }
        //                            } else {
        //                                if (r.getdTimeStamp() > timeMax) {
        //                                    break;
        //                                } else if (r.getdTimeStamp() >= timeMin && r.getdTimeStamp() < timeMax) {
        //                                    timeList.add(r);
        //                                }
        //                            }
        //                        }
        //                        return Observable.from(timeList);
        //                    }
        //                })
        //                .concatMap(new Func1<LRCRow, Observable<LRCRow>>() {
        //                    @Override
        //                    public Observable<LRCRow> call(LRCRow lrcRow) {
        //                        return Observable.just(lrcRow).delay(100, TimeUnit.MILLISECONDS);
        //                    }
        //                })
        //                .observeOn(Schedulers.io())
        //                .map(new Func1<LRCRow, BarrageListViewSimple.BarrageItem>() {
        //                    @Override
        //                    public BarrageListViewSimple.BarrageItem call(LRCRow lrcRow) {
        //                        if (lrcRow == null || TextUtils.isEmpty(lrcRow.getContent())
        //                                || lrcRow.getContent().equals(" </publish>")
        //                                || lrcRow.getContent().equals(" <publish>")) {
        //                            return null;
        //                        }
        //                        String rowContent = lrcRow.getContent();
        //                        if (rowContent.startsWith(" ")) {
        //                            rowContent = rowContent.substring(1, rowContent.length());
        //                        }
        //                        Log.e("TAG", "row content ==" + rowContent);
        //                        if (rowContent.contains("<gift")) {//是礼物
        //                            ChatGiftXmlParser giftXmlParser = new ChatGiftXmlParser(rowContent);
        //                            ReceiveGiftInfo giftInfo = giftXmlParser.parserXml();
        //                            if (giftInfo == null) {
        //                                return null;
        //                            }
        //                            return ReceiveGiftShowUtil.getBarrageItemByReceiveGiftInfo(getContext(), giftInfo);
        //                        } else if (rowContent.contains("<bet")) {//是押注的信息
        //                            ChatBetInfoParser betInfoParser = new ChatBetInfoParser(rowContent);
        //                            BetMessage betMessage = betInfoParser.parserXml();
        //                            if (betMessage == null || betMessage.getOptionId() == 0) {
        //                                return null;
        //                            }
        //                            BetOption optionInfo = getBetOptionName(betMessage.getOptionId());
        //                            if (optionInfo == null) {
        //                                return null;
        //                            }
        //                            BarrageListViewSimple.BarrageItem item = new BarrageListViewSimple.BarrageItem();
        //                            item.name = betMessage.getNickName();
        //                            item.content = "竞猜 " + optionInfo.getName() + " " + betMessage.getCoins() + "乐币";
        //                            return item;
        //                        } else {
        //                            ChatTextXmlParser textXmlParser = new ChatTextXmlParser(rowContent);
        //                            ChatText chatText = textXmlParser.parserXml();
        //                            if (chatText == null) {
        //                                return null;
        //                            }
        //                            BarrageListViewSimple.BarrageItem item = new BarrageListViewSimple.BarrageItem();
        //                            item.name = chatText.getName();
        //                            item.content = chatText.getContent();
        //                            return item;
        //                        }
        //                    }
        //                })
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(new Action1<BarrageListViewSimple.BarrageItem>() {
        //                    @Override
        //                    public void call(BarrageListViewSimple.BarrageItem barrageItem) {
        //                        if (barrageItem != null) {
        //                            barrageListView.addItemData(barrageItem, false);
        //                        }
        //                    }
        //                });
    }

    private BetOption getBetOptionName(long oid) {
        if (guessRoomInfo != null) {
            if (guessRoomInfo.getBetOptionList() != null) {
                for (BetOption betOption : guessRoomInfo.getBetOptionList()) {
                    if (betOption.getId() == oid) {
                        return betOption;
                    }
                }
            }
        }
        return null;
    }

    private void startUpdateProgress() {
        handler.removeMessages(MESSAGE_UPDATE_PROGRESS);
        handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
    }

    private void stopUpdateProgress() {
        handler.removeMessages(MESSAGE_UPDATE_PROGRESS);
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = videoPlayer.getCurrentVideoListTime();
        long duration = videoPlayer.getVideoListDuration();
        if (!generateTime(duration).equals(endTimeText.getText().toString()))
            endTimeText.setText(generateTime(duration));
        if (playProgressBar != null) {
            if (duration > 0) {
                float fpos = ((float) position) / duration * 100;
                long pos = 100L * position / duration;
                playProgressBar.setProgress((int) pos);
            }
            int percent = videoPlayer.getBufferPercentage();
            playProgressBar.setSecondaryProgress(percent);
        }
        String string = generateTime(videoPlayer.getCurrentVideoListTime());
        if (duration == 0) {
            string = generateTime(0);
        }
        startTimeText.setText(string);
        return position;
    }

    private void setSeekBarTouchEnable() {
        ChildInterceptTouchEventDrawerLayout drawerLayout = getDrawerLayout();
        if (drawerLayout != null) {
            drawerLayout.
                    addInterceptTouchEventChildId(R.id.progress_layout);
            drawerLayout.
                    addInterceptTouchEventChildId(R.id.back_play_progress);
        }
    }

    public void setVideoPlayer(LiveFullRoomVideoPlayer player) {
        this.videoPlayer = player;
        videoPlayer.setRestartOnComplete(false);
        videoPlayer.addOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                setBtnPlayStatus(true);
                startUpdateProgress();
                if (!autoSendFloatHeart.isStarted()) {
                    autoSendFloatHeart.start(20);
                }
            }
        });

        videoPlayer.addOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Log.e("TAG", "onCompletion----------");
                stopUpdateProgress();
                setBtnPlayStatus(false);
                startTimeText.setText(endTimeText.getText());
                playProgressBar.setProgress(100);
                autoSendFloatHeart.stop();
            }
        });

    }

    private void setBtnPlayStatus(boolean playing) {
        if (videoPlayer != null) {
            backPlayBottomBar.setBtnPlayStatus(playing);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoPlayer != null) {
            setBtnPlayStatus(videoPlayer.isPlaying());
        }
    }

    @Override
    protected void onGiftSendResponse(GiftResponseInfo data, GiftModel gift, final int num) {
        super.onGiftSendResponse(data, gift, num);
    }

    @Override
    public void updateOwner(LivePersonalRoomDetailsInfo roomInfo) {
        super.updateOwner(roomInfo);
    }

    @Override
    public void updateRoomMember(int memberSize, List<ChatMember> list) {
    }

    @Override
    public void processChatMessage(List<LiveMessage> messageList) {
        super.processChatMessage(messageList);
    }


    @Override
    public void doReceiveGift(List<GiftMessage> receiveGiftInfoList) {
        super.doReceiveGift(receiveGiftInfoList);
    }

    @Override
    public void doReceiveLiveStreamInfo() {
    }

    //    @Override
    //    public void onSharePlatfrom(SharePlatform platform) {
    //        ShareContent content = new ShareContent();
    //        content.title = getRoomTitle();
    //        content.disc = StringUtil.getLiveShareContent(userNameText.getText().toString(), getRoomTitle());
    //        content.thumb = getShareRoomImage();
    //        content.type = ShareContent.UrlType.WebPage;
    //        content.url = AppManager.getInstance().getIApp().getMobileWebUrl() + "/live/personal/" + getShowId();
    //        AbsShare share = ShareFactory.createShare(activity, platform);
    //        share.share(content);
    //    }

    private long getBackPlayId() {
        if (activity instanceof LiveBackPlayFullScreenRoomActivity) {
            LiveBackPlayFullScreenRoomActivity backPlayFullScreenRoomActivity = (LiveBackPlayFullScreenRoomActivity) activity;
            return backPlayFullScreenRoomActivity.getBackPlayId();
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

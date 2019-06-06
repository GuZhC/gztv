package com.dfsx.lzcms.liveroom.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.common.view.CircleButton;
import com.dfsx.core.common.view.OnCirbuttonClickListener;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.DrawerActivity;
import com.dfsx.lzcms.liveroom.LiveFullScreenRoomActivity;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.adapter.OnMessageViewClickListener;
import com.dfsx.lzcms.liveroom.business.*;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.util.*;
import com.dfsx.lzcms.liveroom.view.*;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.jakewharton.rxbinding.view.RxView;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuwb on 2016/6/30.
 */
public class LiveInfoFrag extends Fragment implements SendGiftPopupwindow.
        OnClickEventListener {

    private Handler handler = new Handler();
    protected Activity activity;
    protected View rooView;

    private VisitorLogoListView visitorListView;
    protected FloatHeartBubbleSurfaceView heartBubbleView;
    protected LiveSpecialEffectView liveSpecialEffectView;
    protected BarrageListViewSimple barrageListView;
    private View topContainerView;

    private LiveBottomBar bottomBar;
    private SendGiftPopupwindow sendGiftPopupwindow;
    private Timer timer;
    /**
     * 编辑模式时的挡板.
     * 设计它，是为了方便控制退出编辑模式
     */
    private View editCoverView;

    protected View roomOwnerInfoView;

    protected View liveValueView;

    private int latestSendGiftNum;

    protected TextView userNameText;

    protected TextView userIdText;

    protected TextView onlineNumText;
    private View onlineNumView;

    protected TextView roomMoneyValue;

    protected CircleButton userLogo;

    protected ImageView addConcernbtn;

    protected ImageView topVideoSwitchToFull;

    private SendGift giftSender;

    protected LiveChannelManager channelManager;

    protected long roomOwnerId;

    protected IsLoginCheck loginCheck;

    protected AutoSendFloatHeart autoSendFloatHeart;

    protected SharePopupwindow sharePopupwindow;

    protected int memberSize;

    private UserInfoPopupwindow userInfoPopupwindow;

    private LivePersonalRoomDetailsInfo roomInfo;

    private boolean isSwitchScreenBtnVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rooView = inflater.inflate(R.layout.frag_live_info, null);
        activity = getActivity();
        channelManager = new LiveChannelManager(activity);
        initUserInfoPopupwindow();
        loginCheck = new IsLoginCheck(activity);
        init();
        initAutoSendFloatHeart();

        return rooView;
    }

    protected String getRoomEnterId() {
        String id = "";
        if (activity instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) activity).getRoomEnterId();
        }
        return id;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isSwitchScreenBtnVisible) {
            setTopVideoSwitchToFullImageVisiable(isSwitchScreenBtnVisible);
        }
    }

    protected void initUserInfoPopupwindow() {
        userInfoPopupwindow = new UserInfoPopupwindow(activity);
        userInfoPopupwindow.setVisitorView(true);
    }

    private void init() {
        topContainerView = rooView.findViewById(R.id.top_view);
        visitorListView = (VisitorLogoListView) rooView.findViewById(R.id.visitor_list);
        heartBubbleView = (FloatHeartBubbleSurfaceView) rooView.findViewById(R.id.heart_view);
        liveSpecialEffectView = (LiveSpecialEffectView) rooView.findViewById(R.id.receive_gift_view);
        barrageListView = (BarrageListViewSimple) rooView.findViewById(R.id.barrage_list_view);
        bottomBar = (LiveBottomBar) rooView.findViewById(R.id.live_send_bar);
        editCoverView = rooView.findViewById(R.id.edit_cover_view);
        roomOwnerInfoView = rooView.findViewById(R.id.room_owner_info_container);
        liveValueView = rooView.findViewById(R.id.live_value);
        userNameText = (TextView) rooView.findViewById(R.id.user_name);
        userIdText = (TextView) rooView.findViewById(R.id.user_id);
        onlineNumText = (TextView) rooView.findViewById(R.id.online_num);
        onlineNumView = rooView.findViewById(R.id.online_num_layout);
        userLogo = (CircleButton) rooView.findViewById(R.id.room_user_logo);
        addConcernbtn = (ImageView) rooView.findViewById(R.id.add_concern_img);
        roomMoneyValue = (TextView) rooView.findViewById(R.id.total_value);
        topVideoSwitchToFull = (ImageView) rooView.findViewById(R.id.switch_top_video_to_full);
        rooView.setSoundEffectsEnabled(false);
        /**
         * 控制快速点击
         */
        RxView.clicks(rooView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        int color = HeartColorHelper.getInstance().getFloatResId(
                                AppManager.getInstance().getIApp().getUserName()
                        );
                        heartBubbleView.addAnHeart(color);
                        //                        MessageUtils.sendHeartBubbleMessage(getRoomName(),
                        //                                HeartColorHelper.getInstance().getColor(
                        //                                        XmppManager.getInstance().getUser()
                        //                                ));
                    }
                });
        setVisitorListViewTouchEnable();

        //test
        //        if(timer == null) {
        //            timer = new Timer();
        //        }else {
        //            timer.cancel();
        //        }
        //        timer.schedule(task, 2000, 1000);
        //        for (int i = 0; i < 10; i++) {
        //            visitorListView.addALogog("");
        //        }
        //        barrageListView.setOnRecyclerViewClickListener(new RecyclerItemClickListener.
        //                OnClickListener() {
        //            @Override
        //            public void onClick(View view, float x, float y) {
        //                rooView.performClick();
        //            }
        //        });
        barrageListView.setOnMessageViewClickListener(new OnMessageViewClickListener() {
            @Override
            public void onMessageClick(View v, Object message) {
                if (message != null && message instanceof BarrageListViewSimple.BarrageItem) {
                    BarrageListViewSimple.BarrageItem item = (BarrageListViewSimple.BarrageItem) message;
                    String nickName = item.name;
                    ChatMember member = findMemberByNickName(nickName);
                    if (member != null) {
                        userInfoPopupwindow.setUserInfo(member);
                        userInfoPopupwindow.show(barrageListView);
                    }
                }
            }
        });

        visitorListView.setOnRecyclerViewClickListener(new RecyclerItemClickListener.
                OnClickListener() {
            @Override
            public void onClick(View view, float x, float y) {
                rooView.performClick();
            }
        });

        visitorListView.setOnItemClickListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, float x, float y) {
                ChatMember chatMember = visitorListView.getData().get(position);
                showUserInfo(chatMember);
            }
        });
        bottomBar.setSendGiftViewVisiable(true);
        bottomBar.initSendBarNoTalkView(getRoomId());
        bottomBar.setBarItemListener(new LiveBottomBar.BarItemClickListener() {
            @Override
            public void sendGift() {
                sendGiftClick();
            }

            @Override
            public void share() {
                shareClick();
            }

            @Override
            public void sendMsg(String msg) {
                sendMsgClick(msg);
            }
        });
        editCoverView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    bottomBar.backToNormal();
                }
                return true;
            }
        });

        bottomBar.setOnBarStateChangeListener(new LiveBottomBar.OnBarStateChangeListener() {
            @Override
            public void onChangeToState(LiveBottomBar.BarState state) {
                ChildInterceptTouchEventDrawerLayout drawerLayout = getDrawerLayout();
                if (state == LiveBottomBar.BarState.normalState) {
                    editCoverView.setVisibility(View.GONE);
                    topContainerView.setVisibility(View.VISIBLE);
                    if (drawerLayout != null) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                } else {
                    topContainerView.setVisibility(View.INVISIBLE);
                    editCoverView.setVisibility(View.VISIBLE);
                    if (drawerLayout != null) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                    }
                }
            }
        });
        topVideoSwitchToFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null && activity instanceof LiveFullScreenRoomActivity) {
                    ((LiveFullScreenRoomActivity) activity).switchScreen();
                }
            }
        });
        liveValueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLiveValueAct();
            }
        });

        addConcernbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConcern();
            }
        });
        onlineNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.goRoomMember(getContext(),
                        getShowId(), getRoomEnterId(), false);
            }
        });
        userLogo.setOnCirbuttonClickListener(new OnCirbuttonClickListener() {
            @Override
            public boolean onTouchEventSS(View v, MotionEvent event) {
                userLogoClick();
                return true;
            }
        });
    }

    protected void userLogoClick() {
        if (roomInfo != null) {
            ChatMember chatMember = new ChatMember();
            chatMember.setUserName(roomInfo.getOwnerUserName());
            chatMember.setUserId(roomInfo.getOwnerId());
            chatMember.setLogo(roomInfo.getOwnerAvatarUrl());
            chatMember.setNickName(roomInfo.getOwnerNickName());
            showUserInfo(chatMember);
        }
    }

    private void showUserInfo(ChatMember chatMember) {
        if (chatMember != null && chatMember.getUserId() != 0) {
            userInfoPopupwindow.setUserInfo(chatMember);
            userInfoPopupwindow.show(rooView);
        }
    }

    public void setVideoScreenMode(boolean isLandScreen) {
        int ownerBkgRes = isLandScreen ? R.drawable.bg_room_owner_info_stroke :
                R.drawable.bg_room_owner_info;
        int valueRes = isLandScreen ? R.drawable.bg_room_coins_value :
                R.drawable.bg_room_coins_value_red;
        if (roomOwnerInfoView != null) {
            roomOwnerInfoView.setBackgroundResource(ownerBkgRes);
        }
        if (liveValueView != null) {
            liveValueView.setBackgroundResource(valueRes);
        }
    }

    private int latestVideoHeight;

    @Override
    public void onPause() {
        super.onPause();
        if (autoSendFloatHeart != null) {
            autoSendFloatHeart.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeAutoSendFloatHeart();
    }

    protected void resumeAutoSendFloatHeart() {
        if (autoSendFloatHeart != null) {
            if (!autoSendFloatHeart.isStarted()) {
                autoSendFloatHeart.stop();
            }
        }
    }

    /**
     * 动态设置切换全屏按钮的位置
     *
     * @param videoHeightPX
     */
    public void updateSwitchVideoImagePosition(int videoHeightPX) {
        if (videoHeightPX > 0 && latestVideoHeight != videoHeightPX &&
                topVideoSwitchToFull.getVisibility() == View.VISIBLE) {
            this.latestVideoHeight = videoHeightPX;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                    topVideoSwitchToFull.getLayoutParams();
            int right = PixelUtil.dp2px(activity, 10);
            int top = PixelUtil.dp2px(activity, 50) + videoHeightPX;
            if (params != null) {
                params.setMargins(0, top, right, 0);
                topVideoSwitchToFull.setLayoutParams(params);
            }

        }
    }

    public void setUserNoTalk(long noTalkEndTime) {
        if (bottomBar != null) {
            bottomBar.setNoTalk(getRoomId(), noTalkEndTime);
        }
    }

    public void setTopVideoSwitchToFullImageVisiable(boolean isVisiable) {
        if (topVideoSwitchToFull != null) {
            topVideoSwitchToFull.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
        }
        this.isSwitchScreenBtnVisible = isVisiable;
    }

    private ChatMember findMemberByNickName(String name) {
        try {
            if (getRoomMember() != null) {
                for (int i = 0; i < getRoomMember().size(); i++) {
                    ChatMember member = getRoomMember().get(i);
                    if (member != null && name.equals(member.getNickName())) {
                        return member;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ChatMember> getRoomMember() {
        if (activity instanceof AbsChatRoomActivity) {
            return ((AbsChatRoomActivity) activity).getRoomShowMemberList();
        }
        return null;
    }

    public void cancelNoTalk() {
        if (bottomBar != null) {
            bottomBar.setCancelNoTalk(getRoomId());
        }
    }

    protected void sendMsgClick(String msg) {
        if (!loginCheck.checkLogin()) {
            return;
        }
        String text = msg == null ? "" : msg;
        if (text.equals("")) {
            Toast.makeText(activity, "输入不能为空", Toast.LENGTH_SHORT).show();
        } else {
            //                    barrageListView.addItemData(new
            //                            BarrageListViewSimple.BarrageItem("Tester", text), true);
            if (activity instanceof AbsChatRoomActivity) {
                ((AbsChatRoomActivity) activity)
                        .sendChatMessageMainThread(ChatMessage.getTextMessage(text), null);
            }
        }
    }

    protected void shareClick() {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(activity);
            sharePopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    int vId = v.getId();
                    if (vId == R.id.share_qq) {
                        onSharePlatfrom(SharePlatform.QQ);
                    } else if (vId == R.id.share_wb) {
                        onSharePlatfrom(SharePlatform.WeiBo);
                    } else if (vId == R.id.share_wx) {
                        onSharePlatfrom(SharePlatform.Wechat);
                    } else if (vId == R.id.share_wxfriends) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS);
                    }
                }
            });
        }
        sharePopupwindow.show(rooView);
    }

    /**
     * 分享到各过平台上面
     *
     * @param platform
     */
    public void onSharePlatfrom(SharePlatform platform) {
        ShareContent content = new ShareContent();
        content.title = getRoomTitle();
        content.disc = StringUtil.getLiveShareContent(userNameText.getText().toString(), getRoomTitle());
        content.thumb = getShareRoomImage();
        content.type = ShareContent.UrlType.WebPage;
        content.url = AppManager.getInstance().getIApp().getMobileWebUrl() + "/live/personal/" + getShowId();
        AbsShare share = ShareFactory.createShare(activity, platform);
        share.share(content);
    }

    protected String getRoomTitle() {
        String title = "";
        if (activity instanceof AbsChatRoomActivity) {
            title = ((AbsChatRoomActivity) activity).getRoomTitle();
        }
        return title;
    }

    protected void sendGiftClick() {
        if (sendGiftPopupwindow == null) {
            sendGiftPopupwindow = new SendGiftPopupwindow(activity);
            sendGiftPopupwindow.setOnClickEventListener(LiveInfoFrag.this);
        }
        sendGiftPopupwindow.show(rooView);
    }

    protected void goLiveValueAct() {
        Intent intent = new Intent(activity, DefaultFragmentActivity.class);
        intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_NAME,
                ContributionListFrag.class.getName());
        intent.putExtra(ContributionListFrag.KEY_ROOM_SHOW_ID, getRoomId());
        startActivity(intent);
    }

    protected String getShareRoomImage() {
        if (activity instanceof LiveFullScreenRoomActivity) {
            LiveFullScreenRoomActivity fullScreenRoomActivity = (LiveFullScreenRoomActivity) activity;
            return fullScreenRoomActivity.getVideoImage();
        }
        return null;
    }

    protected long getRoomId() {
        if (activity instanceof AbsChatRoomActivity) {
            AbsChatRoomActivity chatRoomActivity = (AbsChatRoomActivity) activity;
            return chatRoomActivity.getRoomId();
        }
        return 0;
    }

    protected long getShowId() {
        if (activity instanceof AbsChatRoomActivity) {
            AbsChatRoomActivity chatRoomActivity = (AbsChatRoomActivity) activity;
            return chatRoomActivity.getShowId();
        }
        return 0;
    }

    protected void addConcern() {
        if (!loginCheck.checkLogin()) {
            return;
        }
        if (roomOwnerId == 0) {
            Toast.makeText(activity, "没有获取到当前房间的人物信息", Toast.LENGTH_SHORT).show();
            return;
        }
        channelManager.addConcern(roomOwnerId, new DataRequest.DataCallback<Boolean>() {
            @Override
            public void onSuccess(boolean isAppend, Boolean data) {
                setConcernStatus(data);
                RXBusUtil.sendConcernChangeMessage(true, 1);
            }

            @Override
            public void onFail(ApiException e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 主要处理切换屏幕时的逻辑
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            autoSendFloatHeart.stop();
            heartBubbleView.setVisibility(View.GONE);
        } else {
            autoSendFloatHeart.start(memberSize);
            heartBubbleView.clear();
            heartBubbleView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 接收到房间的流信息变化
     */
    public void doReceiveLiveStreamInfo() {
        if (!autoSendFloatHeart.isStarted()) {
            autoSendFloatHeart.start(memberSize);
        }
    }

    private void initAutoSendFloatHeart() {
        autoSendFloatHeart = new AutoSendFloatHeart(heartBubbleView);
    }

    public void updateOwner(LivePersonalRoomDetailsInfo roomInfo) {
        if (roomInfo != null) {
            this.roomInfo = roomInfo;
            ChatMember owner = new ChatMember();
            owner.setUserName(roomInfo.getOwnerUserName());
            owner.setNickName(roomInfo.getOwnerNickName());
            owner.setLogo(roomInfo.getOwnerAvatarUrl());
            owner.setUserId(roomInfo.getOwnerId());
            updateOwnerView(owner, roomInfo.getCurrentVisitorCount());
        }
    }

    /**
     * 跟新显示的房主信息
     */
    public void updateOwnerView(ChatMember owner, long roomMembersize) {
        if (owner != null) {
            String name = owner.getNickName();
            String logo = owner.getLogo();
            roomOwnerId = owner.getUserId();
            Log.e("TAG", "name + logo == " + name + "," + logo);
            userNameText.setText(name);
            userIdText.setText("ID:" + roomOwnerId);
            if (roomMembersize != 0 && roomMembersize != memberSize) {
                memberSize = (int) roomMembersize;
                setOnlineNumText();
            }
            if (!TextUtils.isEmpty(logo)) {
                if (activity != null && !activity.isFinishing()) {
                    LSLiveUtils.showUserLogoImage(activity, userLogo, logo);
                }
            } else {
                userLogo.setImageResource(R.drawable.icon_defalut_no_login_logo);
            }
            initConcernBtnStatus();
        }
    }

    /**
     * 更新当前房间的价值
     *
     * @param coin
     */
    public void updateRoomMoney(double coin) {
        roomMoneyValue.setText(StringUtil.getNumKString(coin));
    }

    private void addRoomMoney(double coin) {
        double old = 0;
        try {
            old = Double.valueOf(roomMoneyValue.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateRoomMoney(old + coin);
    }

    public void initConcernBtnStatus() {
        if (!AppManager.getInstance().getIApp().isLogin()) {
            return;
        }
        if (roomOwnerId != 0) {
            if (roomOwnerId == AppManager.getInstance().getIApp().getLoginUserId()) {
                //说明是自己
                addConcernbtn.setVisibility(View.GONE);
            } else {
                channelManager.isConcern(roomOwnerId, new DataRequest.DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(boolean isAppend, Boolean data) {
                        setConcernStatus(data);
                    }

                    @Override
                    public void onFail(ApiException e) {
                        Log.e("TAG", "e == " + e.getMessage());
                    }
                });
            }
        }
    }

    private void setConcernStatus(boolean isConcerned) {
        addConcernbtn.setVisibility(View.VISIBLE);
        addConcernbtn.setImageResource(isConcerned ? R.drawable.icon_live_has_concerned
                : R.drawable.icon_add_concern);
    }

    public void doUpdateRoomChannelInfo(LivePersonalRoomDetailsInfo roomInfo) {
        if (roomInfo != null) {
            this.roomInfo = roomInfo;
            if (roomInfo.getTotalCoins() != 0) {
                updateRoomMoney(roomInfo.getTotalCoins());
            }
        }
    }

    public void updateRoomMember(int memberSize, List<ChatMember> list) {
        this.memberSize = memberSize;
        LogUtils.e("TAG", "member size == " + memberSize);
        autoSendFloatHeart.reset(memberSize);
        setOnlineNumText();
        Observable.just(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ChatMember>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final List<ChatMember> chatMembers) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                visitorListView.updateLogList(chatMembers);
                            }
                        });

                    }
                });
    }

    protected void setOnlineNumText() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onlineNumText.setText(memberSize + "人在线");
            }
        });
    }

    private void setVisitorListViewTouchEnable() {
        ChildInterceptTouchEventDrawerLayout drawerLayout = getDrawerLayout();
        if (drawerLayout != null) {
            drawerLayout.
                    addInterceptTouchEventChildId(R.id.visitor_list);
            drawerLayout.addInterceptTouchEventChildId(bottomBar.getId());
        }
    }


    /**
     * 用户进入房间
     *
     * @param message
     */
    public void doUserJoinRoom(final UserMessage message) {
        if (liveSpecialEffectView != null) {
            UserLevelManager.getInstance().findLevel(activity,
                    message.getUserLevelId(),
                    new ICallBack<Level>() {
                        @Override
                        public void callBack(Level data) {
                            String text = data != null ? data.getName() : "";
                            liveSpecialEffectView.showUserCome(message.getUserNickName(), text);
                        }
                    });
        }
    }

    /**
     * 收到消息会走这
     *
     * @param messageList
     */
    public void processChatMessage(final List<LiveMessage> messageList) {
        if (messageList != null && messageList.size() > 0) {
            final boolean isShowBottom = messageList.size() == 1 && messageList.get(0) instanceof UserChatMessage &&
                    StringUtil.isCurrentUserName(((UserChatMessage) (messageList.get(0))).getUserName());
            Observable.from(messageList)
                    .observeOn(Schedulers.computation())
                    .flatMap(new Func1<LiveMessage, Observable<BarrageListViewSimple.BarrageItem>>() {
                        @Override
                        public Observable<BarrageListViewSimple.BarrageItem> call(LiveMessage liveMessage) {
                            if (!(liveMessage instanceof UserChatMessage)) {
                                return Observable.just(null);
                            }
                            UserChatMessage message = (UserChatMessage) liveMessage;
                            String fromUserName = message.getUserNickName();
                            CharSequence content = message.getBody();
                            if (!TextUtils.isEmpty(content)) {
                                BarrageListViewSimple.BarrageItem item = new
                                        BarrageListViewSimple.BarrageItem(fromUserName, content, message.getUserLevelId());
                                return Observable.just(item);
                            }
                            return Observable.just(null);
                        }
                    })
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<BarrageListViewSimple.BarrageItem>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(List<BarrageListViewSimple.BarrageItem> itemList) {
                            barrageListView.addItemData(itemList, isShowBottom);
                        }
                    });
        }

    }


    public void doReceiveGift(List<GiftMessage> receiveGiftInfoList) {
        if (receiveGiftInfoList != null && receiveGiftInfoList.size() > 0) {
            addRoomMoney(receiveGiftInfoList.get(receiveGiftInfoList.size() - 1)
                    .getGiftCoins());
            ReceiveGiftShowUtil.showReceiveGiftInList(activity,
                    barrageListView, receiveGiftInfoList);
            Observable.from(receiveGiftInfoList)
                    .observeOn(Schedulers.computation())
                    .delay(100, TimeUnit.MILLISECONDS)
                    .map(new Func1<GiftMessage, GiftMessage>() {
                        @Override
                        public GiftMessage call(GiftMessage receiveGiftInfo) {
                            if (ReceiveGiftShowUtil.isManySendGift(receiveGiftInfo)) {
                                latestSendGiftNum += receiveGiftInfo.getCount();
                            } else {
                                latestSendGiftNum = receiveGiftInfo.getCount();
                            }
                            return receiveGiftInfo;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<GiftMessage>() {
                        @Override
                        public void call(GiftMessage giftMessage) {
                            ReceiveGiftShowUtil.showReceiveGift(activity,
                                    liveSpecialEffectView, giftMessage,
                                    latestSendGiftNum);
                        }
                    });
        }

    }

    protected ChildInterceptTouchEventDrawerLayout getDrawerLayout() {
        if (activity instanceof DrawerActivity) {
            return ((DrawerActivity) activity).getDrawerLayout();
        }
        return null;
    }


    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (heartBubbleView != null) {
                //                heartBubbleView.addSomeHeart(200, Color.YELLOW);
                for (int i = 0; i < 10; i++) {
                    heartBubbleView.addAnHeart(Color.YELLOW);
                }
            }
        }
    };

    @Override
    public void onBuyGoldClick() {
        IntentUtil.goAddMoneyActivity(activity);
    }

    @Override
    public void onSendGiftClick(final GiftModel gift, final int num, boolean isManySend) {
        if (!loginCheck.checkLogin() || gift == null) {
            return;
        }
        if (MyMoneyInfoManager.getInstance().getCacheMoneyInfo() != null
                &&
                MyMoneyInfoManager.getInstance().getCacheMoneyInfo().getCoins() <
                        gift.getPrice() * num) {
            showNoEnoughMoneyDialog();
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("isManySend", isManySend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (giftSender == null) {
            giftSender = new LiveRoomHttpSendGift(activity, getRoomId(), getRoomEnterId());
        }
        giftSender.sendGift(gift, num, getRoomName(), new ICallBack<GiftResponseInfo>() {
            @Override
            public void callBack(GiftResponseInfo data) {
                onGiftSendResponse(data, gift, num);
            }
        });
    }

    protected void onGiftSendResponse(GiftResponseInfo data, GiftModel gift, int num) {
        boolean isSuccess = data.isSuccess();
        if (!isSuccess) {
            String giftResponseText = data.getErrorMsg();
            if (data.isNoEnoughMoney()) {
                showNoEnoughMoneyDialog();
            } else {
                Toast.makeText(getActivity(), giftResponseText,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            double payCount = gift.getPrice() * num;
            MyMoneyInfoManager.getInstance().payMoney(payCount);
            sendGiftPopupwindow.updateMoneyText();
        }
    }

    private void showNoEnoughMoneyDialog() {
        LSLiveUtils.showNoEnoughMoneyDialog(activity);
    }

    public String getRoomName() {
        String roomNae = "";
        Activity act = getActivity();
        if (act instanceof AbsChatRoomActivity) {
            roomNae = ((AbsChatRoomActivity) act).getRoomName();
        }
        return roomNae;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (autoSendFloatHeart != null) {
            autoSendFloatHeart.destory();
        }
    }
}

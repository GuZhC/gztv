package me.lake.librestreaming.sample.frag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.DrawerActivity;
import com.dfsx.lzcms.liveroom.adapter.OnMessageViewClickListener;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.business.UserLevelManager;
import com.dfsx.lzcms.liveroom.fragment.ContributionListFrag;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.util.IntentUtil;
import com.dfsx.lzcms.liveroom.util.LSLiveUtils;
import com.dfsx.lzcms.liveroom.util.ReceiveGiftShowUtil;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.dfsx.lzcms.liveroom.view.*;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.jakewharton.rxbinding.view.RxView;
import me.lake.librestreaming.sample.LiveRecordStreamingActivity;
import me.lake.librestreaming.sample.R;
import me.lake.librestreaming.sample.ui.LiveAnchorBottomToolBar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * 直播信息界面
 * Created by liuwenbo on 2016/11/1.
 */
public class LiveRecordInfoFrag extends Fragment {

    public static final String KEY_IS_LAND = "LiveRecordInfoFrag_IS_land";

    private Handler handler = new Handler();
    private Activity activity;
    private View rooView;
    private TextView mCountTimeTx;

    private VisitorLogoListView visitorListView;
    private FloatHeartBubbleSurfaceView heartBubbleView;
    private LiveSpecialEffectView liveSpecialEffectView;
    private BarrageListViewSimple barrageListView;
    private View topContainerView;
    private ImageView roomControlView;

    //    private LiveBottomBar bottomBar;
    private SendGiftPopupwindow sendGiftPopupwindow;

    private BeautyFaceSettingsWindow beautyFaceSettingsWindow;
    private Timer timer;
    /**
     * 编辑模式时的挡板.
     * 设计它，是为了方便控制退出编辑模式
     */
    private View editCoverView;

    private View liveValueView;
    private TextView roomMoneyValue;

    private TextView userIdText;
    private TextView onLineText;
    private View onLineNumView;
    private ImageView userLogo;

    private AutoSendFloatHeart autoSendFloatHeart;

    private LiveAnchorBottomToolBar anchorBottomToolBar;

    private SharePopupwindow sharePopupwindow;

    private String userName;

    private int memberSize;

    private UserInfoPopupwindow userInfoPopupwindow;

    public static LiveRecordInfoFrag newInstance(boolean isLandLayout) {
        LiveRecordInfoFrag infoFrag = new LiveRecordInfoFrag();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_LAND, isLandLayout);
        infoFrag.setArguments(bundle);
        return infoFrag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        boolean isLand = bundle.getBoolean(KEY_IS_LAND, false);
        int layout = isLand ? R.layout.frag_live_stream_info_record_land_layout
                : R.layout.frag_live_stream_info_record_port_layout;
        rooView = inflater.inflate(layout, null);
        activity = getActivity();
        init();
        return rooView;
    }

    private void init() {
        mCountTimeTx = (TextView) rooView.findViewById(R.id.live_reinfro_time);
        roomControlView = (ImageView) rooView.findViewById(R.id.live_room_control);
        heartBubbleView = (FloatHeartBubbleSurfaceView) rooView.
                findViewById(R.id.float_heart_view);
        topContainerView = rooView.findViewById(R.id.top_view);
        visitorListView = (VisitorLogoListView) rooView.findViewById(R.id.visitor_list);
        liveSpecialEffectView = (LiveSpecialEffectView) rooView.findViewById(R.id.receive_gift_view);
        barrageListView = (BarrageListViewSimple) rooView.findViewById(R.id.barrage_list_view);
        editCoverView = rooView.findViewById(R.id.edit_cover_view);
        liveValueView = rooView.findViewById(R.id.live_value);
        roomMoneyValue = (TextView) rooView.findViewById(R.id.value);
        onLineText = (TextView) rooView.findViewById(R.id.online_nums);
        onLineNumView = rooView.findViewById(R.id.online_num_view);
        userLogo = (ImageView) rooView.findViewById(R.id.room_user_logos);
        userIdText = (TextView) rooView.findViewById(R.id.user_id_text);
        anchorBottomToolBar = (LiveAnchorBottomToolBar) rooView.findViewById(R.id.anchor_bottom_bar);

        rooView.setSoundEffectsEnabled(false);

        autoSendFloatHeart = new AutoSendFloatHeart(heartBubbleView);

        userInfoPopupwindow = new UserInfoPopupwindow(getContext());


        beautyFaceSettingsWindow = new
                BeautyFaceSettingsWindow(getContext());

        beautyFaceSettingsWindow.setOnLevelChangeListener(new BeautyFaceSettingsWindow.OnLevelChangeListener() {
            @Override
            public void onLevelChange(int level) {
                setBeautyLevel(level);
            }
        });
        /**
         * 控制快速点击
         */
        RxView.clicks(rooView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                    }
                });

        setVisitorListViewTouchEnable();

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
                    if (member != null && member.getUserId() != 0) {
                        userInfoPopupwindow.setUserRoomId(getRoomId(), getRoomEnterId());
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
                if (chatMember != null && chatMember.getUserId() != 0) {
                    userInfoPopupwindow.setUserRoomId(getRoomId(), getRoomEnterId());
                    userInfoPopupwindow.setUserInfo(chatMember);
                    userInfoPopupwindow.show(visitorListView);
                }
            }
        });

        editCoverView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    anchorBottomToolBar.showToolView();
                }
                return true;
            }
        });

        liveValueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLiveValueAct();
            }
        });

        anchorBottomToolBar.setOnEventClickListener(new LiveAnchorBottomToolBar.OnEventClickListener() {
            @Override
            public void onClickShare(View v) {
                shareClick();
            }

            @Override
            public void onClickBeauty(View v) {
                beautyFaceSettingsWindow.setCheckedLevel(getCurrentBeautyLevel());
                beautyFaceSettingsWindow.showAtViewTop(v);
            }

            @Override
            public void onClickSwitch(View v) {
                switchCamera();
            }

            @Override
            public void onClickSend(View v, String text) {
                sendMsgClick(text);
            }
        });

        onLineNumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.goRoomMember(getContext(),
                        getshowId(), getRoomEnterId(), true);
            }
        });
        roomControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.goRoomControl(getContext(),
                        getshowId(), getRoomEnterId());
            }
        });
        anchorBottomToolBar.setOnBarViewShowChangeListener(new LiveAnchorBottomToolBar.OnBarViewShowChangeListener() {
            @Override
            public void onShowChange(boolean isEditViewShow) {
                if (isEditViewShow) {
                    editCoverView.setVisibility(View.VISIBLE);
                    if (getDrawerLayout() != null) {
                        getDrawerLayout().
                                setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                } else {
                    editCoverView.setVisibility(View.GONE);
                    if (getDrawerLayout() != null) {
                        getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                    }
                }
            }
        });

    }

    protected String getRoomEnterId() {
        String id = "";
        if (activity instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) activity).getRoomEnterId();
        }
        return id;
    }

    private ChatMember findMemberByNickName(String name) {
        try {
            List<ChatMember> list = getRoomMember();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    ChatMember member = list.get(i);
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

    private int getCurrentBeautyLevel() {
        if (activity instanceof LiveRecordStreamingActivity) {
            return ((LiveRecordStreamingActivity) activity).getCurrentLevel();
        }
        return 0;
    }

    public boolean onBackPress() {
        if (anchorBottomToolBar.isEditViewShow()) {
            anchorBottomToolBar.showToolView();
            return true;
        }
        return false;
    }

    private void setBeautyLevel(int level) {
        if (activity instanceof LiveRecordStreamingActivity) {
            ((LiveRecordStreamingActivity) activity).setBeautyLevel(level);
        }
    }

    protected void sendMsgClick(String msg) {
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(activity, "输入不能为空", Toast.LENGTH_SHORT).show();
        } else {
            ChatMessage message = ChatMessage.getTextMessage(msg);
            if (activity instanceof LiveRecordStreamingActivity) {
                ((LiveRecordStreamingActivity) activity).sendChatMessageMainThread(message, null);
            }
        }
    }

    protected void goLiveValueAct() {
        Intent intent = new Intent(activity, DefaultFragmentActivity.class);
        intent.putExtra(DefaultFragmentActivity.KEY_FRAGMENT_NAME,
                ContributionListFrag.class.getName());
        intent.putExtra(ContributionListFrag.KEY_ROOM_SHOW_ID, getRoomId());
        startActivity(intent);
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
     * 接收到房间的流信息变化
     */
    public void doReceiveLiveStreamInfo() {
        if (!autoSendFloatHeart.isStarted()) {
            autoSendFloatHeart.start(memberSize);
        }
    }

    public void doReceiveLiveStop() {
        autoSendFloatHeart.stop();
    }

    /**
     * 分享到各过平台上面
     *
     * @param platform
     */
    public void onSharePlatfrom(SharePlatform platform) {
        ShareContent content = new ShareContent();
        content.title = getRoomTitle();
        content.disc = StringUtil.getLiveShareContent(userName, getRoomTitle());
        content.thumb = getShareRoomImage();
        content.type = ShareContent.UrlType.WebPage;
        content.url = getShareUrl();
        AbsShare share = ShareFactory.createShare(activity, platform);
        share.share(content);
    }

    private String getShareUrl() {
        String url = "";
        if (activity instanceof LiveRecordStreamingActivity) {
            url = ((LiveRecordStreamingActivity) activity).getSharedWebUrl();
        }
        return url;
    }

    private String getShareRoomImage() {
        String path = "";
        if (activity instanceof LiveRecordStreamingActivity) {
            path = ((LiveRecordStreamingActivity) activity).getCoverImagePath();
        }
        return path;
    }

    private long getRoomId() {
        long id = 0;
        if (activity instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) activity).getRoomId();
        }
        return id;
    }

    private long getshowId() {
        long id = 0;
        if (activity instanceof AbsChatRoomActivity) {
            id = ((AbsChatRoomActivity) activity).getShowId();
        }
        return id;
    }

    private String getRoomTitle() {
        String title = "";
        if (activity instanceof AbsChatRoomActivity) {
            title = ((AbsChatRoomActivity) activity).getRoomTitle();
        }
        return title;
    }

    private void setVisitorListViewTouchEnable() {
        ChildInterceptTouchEventDrawerLayout drawerLayout = getDrawerLayout();
        if (drawerLayout != null) {
            drawerLayout.
                    addInterceptTouchEventChildId(R.id.visitor_list);
            drawerLayout.addInterceptTouchEventChildId(anchorBottomToolBar.getId());
        }
    }

    public void doUpdateRoomChannelInfo(LivePersonalRoomDetailsInfo roomDetailsInfo) {
        if (roomDetailsInfo != null) {
            if (roomDetailsInfo.getTotalCoins() != 0) {
                updateRoomMoney(roomDetailsInfo.getTotalCoins());
            }
        }
    }

    private void switchCamera() {
        if (activity instanceof LiveRecordStreamingActivity) {
            ((LiveRecordStreamingActivity) activity).switchCamera();
        }
    }

    /**
     * 收到消息会走这
     *
     * @param messageList
     */
    public void processMessage(final List<LiveMessage> messageList) {
        Observable.from(messageList)
                .observeOn(Schedulers.newThread())
                .flatMap(new Func1<LiveMessage, Observable<BarrageListViewSimple.BarrageItem>>() {
                    @Override
                    public Observable<BarrageListViewSimple.BarrageItem> call(LiveMessage liveMessage) {
                        if (!(liveMessage instanceof UserChatMessage)) {
                            return Observable.just(null);
                        }
                        UserChatMessage message = (UserChatMessage) liveMessage;
                        String fromUserName = message.getUserNickName();
                        boolean isShowBottom = StringUtil.isCurrentUserName(message.getUserName());
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
                .subscribe(new Action1<List<BarrageListViewSimple.BarrageItem>>() {
                    @Override
                    public void call(List<BarrageListViewSimple.BarrageItem> itemList) {
                        barrageListView.addItemData(itemList, false);
                    }
                });
    }

    private String getRoomName() {
        if (getActivity() instanceof AbsChatRoomActivity) {
            AbsChatRoomActivity act = (AbsChatRoomActivity) getActivity();
            return act.getRoomName();
        }
        return "@@";
    }

    private int latestGiftNum;

    public void doReceiveGift(List<GiftMessage> receiveGiftInfoList) {
        if (receiveGiftInfoList != null && !receiveGiftInfoList.isEmpty()) {
            int size = receiveGiftInfoList.size();
            addRoomMoney(receiveGiftInfoList.get(size - 1).getGiftCoins());
            ReceiveGiftShowUtil.showReceiveGiftInList(activity,
                    barrageListView, receiveGiftInfoList);
            Observable.from(receiveGiftInfoList)
                    .observeOn(Schedulers.newThread())
                    .delay(100, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<GiftMessage>() {
                        @Override
                        public void call(GiftMessage giftMessage) {
                            latestGiftNum = ReceiveGiftShowUtil.isManySendGift(giftMessage) ?
                                    latestGiftNum + giftMessage.getCount() :
                                    giftMessage.getCount();
                            ReceiveGiftShowUtil.showReceiveGift(activity, liveSpecialEffectView,
                                    giftMessage, latestGiftNum);
                        }
                    });

        }
    }

    public void doUserJoinRoom(final UserMessage message) {
        if (liveSpecialEffectView != null && message != null) {
            UserLevelManager.getInstance().findLevel(activity,
                    message.getUserLevelId(), new ICallBack<Level>() {
                        @Override
                        public void callBack(Level data) {
                            String gradeText = data != null ? data.getName() : "";
                            liveSpecialEffectView.showUserCome(message.getUserNickName(), gradeText);
                        }
                    });
        }
    }


    public void updateRoomMember(int memberSize, List<ChatMember> memberList) {
        Log.e("TAG", "member size == " + memberSize);
        this.memberSize = memberSize;
        autoSendFloatHeart.reset(memberSize);
        onLineText.setText(memberSize + "人在线");
        Observable.just(memberList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ChatMember>>() {
                    @Override
                    public void call(final List<ChatMember> members) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                visitorListView.updateLogList(members);
                            }
                        });
                    }
                });
    }

    public void updateOwner(LivePersonalRoomDetailsInfo roomDetailsInfo) {
        autoSendFloatHeart.start(1);
        if (roomDetailsInfo != null) {
            userName = roomDetailsInfo.getOwnerUserName();
            String logo = roomDetailsInfo.getOwnerAvatarUrl();
            userIdText.setText("ID:" + roomDetailsInfo.getOwnerId());
            if (!TextUtils.isEmpty(logo)) {
                LSLiveUtils.showUserLogoImage(activity, userLogo, logo);
            } else {
                userLogo.setImageResource(R.drawable.icon_defalut_no_login_logo);
            }
        }
    }

    private ChildInterceptTouchEventDrawerLayout getDrawerLayout() {
        if (activity instanceof DrawerActivity) {
            return ((DrawerActivity) activity).getDrawerLayout();
        }
        return null;
    }


    public void updateTime(String time) {
        mCountTimeTx.setText(time);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (autoSendFloatHeart != null) {
            autoSendFloatHeart.stop();
        }
    }

}

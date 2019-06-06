package com.dfsx.lzcms.liveroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.dfsx.core.common.Util.IntentUtil;
import com.dfsx.core.common.act.BaseActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.log.LogUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.rx.RxBus;
import com.dfsx.lzcms.liveroom.business.*;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.mqtt.Connection;
import com.dfsx.lzcms.liveroom.mqtt.MQTTMessageReceiveListener;
import com.dfsx.lzcms.liveroom.mqtt.MqttManager;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwb on 2016/10/17.
 */
public class AbsChatRoomActivity extends BaseActivity implements LiveMessageTypeCallBack {

    private static final String DEFAULT_ROOM_NAME = "live1";

    public static final String KEY_CHAT_ROOM_INTENT_DATA = "abschatroomactivity_key_chat_room_data";

    protected String roomName;
    protected String roomPassword;
    protected String loginPassword;
    protected String roomTitle;
    protected long roomUserId;
    private boolean isAutoEnterRoom;//是否自动进入房间

    protected Activity activity;
    protected long roomId = 1;
    /**
     * 在进入直播间成功并获取到直播流地址的情况下得到showId
     */
    protected long showId;
    protected LiveChannelManager channelManager;
    private Subscription loginSubscription;

    private RoomDetails roomDetails;
    private String roomOwnerUserName;
    private IChatRoom chatRoom;
    private ILiveRoomMessageParser liveMessageParser;

    /**
     * 房间列表显示的成员数据缓存
     */
    private List<ChatMember> roomShowMemberList = new ArrayList<>();
    /**
     * 房间成员在线信息
     */
    private OnLineMember roomOnlineMember;

    private LiveMessageQueue<LiveMessage> userChatMessageQueue;

    private LiveMessageQueue<GiftMessage> userGiftMessageQueue;

    /**
     * enter room 接口返回的roomID
     */
    private String roomEnterId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        ChatRoomIntentData intentData = (ChatRoomIntentData) getIntent().getSerializableExtra(KEY_CHAT_ROOM_INTENT_DATA);
        if (intentData != null) {
            roomUserId = intentData.getRoomOwnerId();
            roomId = intentData.getRoomId();
            roomName = intentData.getRoomName();
            roomPassword = intentData.getRoomPassword();
            roomTitle = intentData.getRoomTitle();
            isAutoEnterRoom = intentData.isAutoJoinRoomAtOnce();
            //init 加入房间之后再做初始化
            this.roomEnterId = roomId + "";
        }
        if (TextUtils.isEmpty(roomName)) {
            roomName = DEFAULT_ROOM_NAME;
        }
        if (TextUtils.isEmpty(roomPassword)) {
            roomPassword = "";
        }
        channelManager = new LiveChannelManager(activity);
        liveMessageParser = new MqttMessageParser();
        liveMessageParser.setMessageTypeCallBack(this);
        initRegister();
        if (isAutoEnterRoom) {
            asyncJoinRoom(roomName, roomId);
        }
    }

    protected Serializable getIntentSerializableData() {
        return getIntent().getSerializableExtra(KEY_CHAT_ROOM_INTENT_DATA);
    }

    private void initRegister() {
        loginSubscription = RxBus.getInstance().
                toObserverable(Intent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        if (IntentUtil.ACTION_LOGIN_OK.
                                equals(intent.getAction())) {
                            onReLoginPlatformSuccess();
                        }
                    }
                });
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    /**
     * 接收直播间的消息
     */
    private MQTTMessageReceiveListener receiveMessageListener = new MQTTMessageReceiveListener() {

        @Override
        public void onProcessMessage(String topic, MqttMessage mqttMessage) {
            if (liveMessageParser != null) {
                liveMessageParser.parserMessage(topic, mqttMessage);
            }
        }
    };


    public final void asyncJoinRoom(String roomName, long roomId) {
        this.roomName = roomName;
        this.roomId = roomId;
        httpEnterRoom(roomId);
    }

    public void httpEnterRoom(long id) {
        channelManager.enterRoom(id + "", roomPassword, new ICallBack<EnterRoomInfo>() {
            @Override
            public void callBack(EnterRoomInfo serviceInfo) {
                if (serviceInfo != null && !serviceInfo.isError()) {
                    String serviceUri = serviceInfo.getServerAddress();
                    String roomIdAddress = serviceInfo.getRoomIdAddress();
                    String userId = serviceInfo.getUserId() + "";
                    String clientId = serviceInfo.getClientId();
                    roomEnterId = roomIdAddress;
                    Log.e("TAG", "serviceUri == " + serviceUri);
                    Log.e("TAG", "roomId == " + roomIdAddress);
                    if (isLogin()) {
                        loginEnterRoom(serviceUri, roomIdAddress, userId, clientId);
                    } else {
                        noLoginEnterRoom(serviceUri, roomIdAddress, userId, clientId);
                    }
                } else if (serviceInfo != null && serviceInfo.isError()) {
                    String text = serviceInfo.getErrorMessage();
                    if (TextUtils.isEmpty(text)) {
                        text = "没有获取到房间信息";
                    }
                    joinChatRoomStatus(new ApiException(text));
                } else {
                    joinChatRoomStatus(new ApiException("没有获取到房间信息"));
                    LogUtils.e("TAG", "返回null");
                }
            }
        });
    }

    public String getRoomEnterId() {
        return roomEnterId;
    }

    protected boolean isLogin() {
        return AppManager.getInstance().getIApp().isLogin();
    }

    private void linkChatSever(String serverUri, String roomIdAddress, String userId, String userName, String loginToken) {
        Connection connection = new Connection(serverUri, loginToken, userId, userName, roomIdAddress);
        MqttManager.getInstance().connect(connection, new ICallBack<ApiException>() {
            @Override
            public void callBack(ApiException data) {
                joinChatRoomStatus(data);
            }
        });
    }

    /**
     * 进入需要的房间
     */
    public void loginEnterRoom(String serverUri, String roomIdAddress, String userId, String clientId) {
        if (AppManager.getInstance().getIApp().isLogin()) {
            linkChatSever(serverUri, roomIdAddress, userId, AppManager.getInstance().getIApp().getUserName(),
                    clientId);
        }
    }

    private void noLoginEnterRoom(String serverUri, String roomIdAddress, String userId, String clientId) {
        linkChatSever(serverUri, roomIdAddress, userId, "",
                clientId);
    }

    protected void toast(String text) {
        Observable.just(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(AbsChatRoomActivity.this, s,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 在未登录的情况下 再次登录后进入
     * 重新登录成功之后 进入界面后将调用此方法
     */
    public void onReLoginPlatformSuccess() {
        MqttManager.getInstance().disconnect();
        //登录之后重新登录
        asyncJoinRoom(roomName, roomId);
    }

    public void joinChatRoomStatus(ApiException error) {
        if (error != null) {
            Toast.makeText(activity, error.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } else {
            //            Toast.makeText(AbsChatRoomActivity.this,
            //                    "进入房间成功", Toast.LENGTH_SHORT).show();
            MqttManager.getInstance().setMessageListener(receiveMessageListener);
            updateRoomMember();
            getRoomNoTalkUserList();
        }
    }


    protected final void updateRoomMember() {
        if (chatRoom == null) {
            chatRoom = new HttpChatRoomHelper(this, roomId, roomEnterId);
        }
        Observable.just(null)
                .observeOn(Schedulers.io())
                .map(new Func1<Object, OnLineMember>() {
                    @Override
                    public OnLineMember call(Object o) {
                        OnLineMember onLineMember = chatRoom.getRoomMember(1, 10);
                        return onLineMember;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OnLineMember>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(OnLineMember onLineMember) {
                        roomOnlineMember = onLineMember;
                        if (roomOnlineMember == null) {
                            roomOnlineMember = new OnLineMember();
                        }
                        onUpdateMemberInfo(onLineMember);
                    }
                });
    }

    /**
     * 聊天室发送消息
     *
     * @param message
     */
    public boolean sendChatMessageChildThread(ChatMessage message) {
        if (chatRoom == null) {
            chatRoom = new HttpChatRoomHelper(this, roomId, roomEnterId);
        }
        return chatRoom.sendChatMessage(message);
    }

    public void sendChatMessageMainThread(ChatMessage message, final ICallBack<Boolean> callBack) {
        Observable.just(message)
                .observeOn(Schedulers.io())
                .map(new Func1<ChatMessage, Boolean>() {
                    @Override
                    public Boolean call(ChatMessage message) {
                        return sendChatMessageChildThread(message);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callBack != null) {
                            callBack.callBack(false);
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (callBack != null) {
                            callBack.callBack(aBoolean);
                        }
                    }
                });
    }

    protected final void onUpdateMemberInfo(OnLineMember onLineMember) {
        int memberSize = onLineMember != null ? onLineMember.getTotalNum() :
                0;
        List<ChatMember> list = onLineMember != null ? onLineMember.getChatMemberList() :
                null;
        if (list != null) {
            roomShowMemberList = list;
        }
        onUpdateMemberView(memberSize, list);
    }

    public void onUpdateMemberView(int roomMemberCount, List<ChatMember> chatMemberList) {

    }

    public void onUpdateOwnerInfo(LivePersonalRoomDetailsInfo roomDetailsInfo) {
    }

    /**
     * 根据当前房间的ID号获取所有的信息
     *
     * @return
     */
    protected final RoomDetails getRoomDeatilsFormNetSync() {
        String url = AppManager.getInstance().getIApp().getHttpBaseUrl() + "/public/channels/" + roomId;
        String res = HttpUtil.executeGet(url, new HttpParameters(), null);
        try {
            StringUtil.checkHttpResponseError(res);
            RoomDetails details = new Gson().fromJson(res, RoomDetails.class);
            roomDetails = details;
            return details;
        } catch (ApiException e) {
            e.printStackTrace();
            roomDetails = null;
        }
        return null;
    }

    public long getRoomId() {
        return roomId;
    }

    /**
     * 只有在进入直播间成功才能成功获取有效的showid
     *
     * @return
     */
    public long getShowId() {
        return roomId;
    }

    /**
     * 取当前房间详情信息
     *
     * @param callBack
     */
    protected final void getRoomDeatils(final ICallBack<RoomDetails> callBack) {
        if (callBack == null) {
            return;
        }
        if (roomDetails != null) {
            callBack.callBack(roomDetails);
        } else {
            Observable.just(null)
                    .observeOn(Schedulers.io())
                    .map(new Func1<Object, RoomDetails>() {
                        @Override
                        public RoomDetails call(Object o) {
                            return getRoomDeatilsFormNetSync();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<RoomDetails>() {
                        @Override
                        public void call(RoomDetails roomDetails) {
                            callBack.callBack(roomDetails);
                        }
                    });
        }
    }

    protected void getLiveRtmpUrl(DataRequest.DataCallback<String> callback) {
        channelManager.getLiveRtmpUrl(roomId, callback);
    }

    public void disconnectChatRoom() {
        MqttManager.getInstance().close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userChatMessageQueue != null) {
            userChatMessageQueue.stop();
            userChatMessageQueue.release();
        }
        if (userGiftMessageQueue != null) {
            userGiftMessageQueue.stop();
            userGiftMessageQueue.release();
        }
        if (loginSubscription != null) {
            loginSubscription.unsubscribe();
        }
        boolean isExitRoom = MqttManager.getInstance().close();
        if (isExitRoom) {
            LogUtils.e("TAG", "onDestroy exit room ---------");
        }
    }

    @Override
    public void finish() {
        super.finish();
        boolean isExitRoom = MqttManager.getInstance().close();
        if (isExitRoom) {
            LogUtils.e("TAG", "finish exit room ---------");
        }
    }

    protected void getPersonalRoomInfo() {
        getPersonalRoomInfo(false);
    }

    /**
     * 获取当前房间的频道信息
     * #onChannelInfoUpdate回调
     */
    protected void getPersonalRoomInfo(boolean isCurrentUser) {
        if (roomId == -1) {
            return;
        }
        channelManager.gerPersonalRoomInfo(isCurrentUser, roomId, new DataRequest.DataCallback<LivePersonalRoomDetailsInfo>() {
            @Override
            public void onSuccess(boolean isAppend, LivePersonalRoomDetailsInfo data) {
                if (data != null && data.getId() == roomId) {
                    onPersonalRoomInfoUpdate(data);
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 获取所有的成员
     *
     * @return
     */
    public List<ChatMember> getRoomShowMemberList() {
        return roomShowMemberList;
    }

    /**
     * 移除成员
     *
     * @param memberUserName
     */
    public final void removeMember(String memberUserName) {
        ChatMember member = findChatMemberByUserName(memberUserName);
        if (member != null && roomShowMemberList != null) {
            roomShowMemberList.remove(member);
        }
        if (roomOnlineMember != null) {
            roomOnlineMember.setChatMemberList(roomShowMemberList);
            onUpdateMemberView(roomOnlineMember.getTotalNum(), roomShowMemberList);
        }
    }

    public final void removeMember(ChatMember member) {
        if (member != null) {
            removeMember(member.getUserName());
        }
    }

    public final ChatMember findChatMemberByUserName(String memberUserName) {
        if (!TextUtils.isEmpty(memberUserName) &&
                roomShowMemberList != null) {
            for (ChatMember chatMember : roomShowMemberList) {
                if (chatMember != null && memberUserName.equals(chatMember.getUserName())) {
                    return chatMember;
                }
            }
        }
        return null;
    }

    public final void addMember(ChatMember member) {
        ChatMember findMember = findChatMemberByUserName(member.getUserName());
        if (findMember == null && member != null) {
            if (roomShowMemberList == null) {
                roomShowMemberList = new ArrayList<>();
            }
            roomShowMemberList.add(member);
        }
        if (roomOnlineMember != null) {
            roomOnlineMember.setChatMemberList(roomShowMemberList);
            onUpdateMemberView(roomOnlineMember.getTotalNum(), roomShowMemberList);
        }
    }

    public void getRoomNoTalkUserList() {
        if (chatRoom == null) {
            chatRoom = new HttpChatRoomHelper(this, roomId, roomEnterId);
        }
        chatRoom.getNoTalkUserList(new ICallBack<List<NoTalkUser>>() {
            @Override
            public void callBack(List<NoTalkUser> data) {
                onGetRoomNoTalkMember(data);
            }
        });
    }

    /**
     * 房间禁言列表
     *
     * @param noTalkList
     */
    protected void onGetRoomNoTalkMember(List<NoTalkUser> noTalkList) {
        if (AppManager.getInstance().getIApp().isLogin() && noTalkList != null) {
            long cureId = AppManager.getInstance().getIApp().getLoginUserId();
            for (NoTalkUser user : noTalkList) {
                if (user != null && user.getUserId() == cureId) {
                    onCurrentUserNoTalk(user.getExpiredTime() * 1000);
                    break;
                }
            }
        }
    }

    /**
     * 当前用户不能发言
     */
    protected void onCurrentUserNoTalk(long expiredTime) {

    }

    /**
     * 在获取成功当前房间的频道信息之后调用
     */
    protected void onPersonalRoomInfoUpdate(LivePersonalRoomDetailsInfo roomDetailsInfo) {
        if (roomDetailsInfo != null) {
            roomTitle = roomDetailsInfo.getTitle();
            roomUserId = roomDetailsInfo.getOwnerId();
        }
        onUpdateOwnerInfo(roomDetailsInfo);
    }

    @Override
    public void onUserJoinRoomMessage(UserMessage message) {
        if (message != null && message.getUserId() != 0) {
            ChatMember member = new ChatMember();
            member.setLogo(message.getUserAvatarUrl());
            member.setNickName(message.getUserNickName());
            member.setUserName(message.getUserName());
            member.setUserId(message.getUserId());
            int totalNum = message.getCurrentUserCount();
            if (roomOnlineMember != null) {
                roomOnlineMember.setTotalNum(totalNum);
            }
            addMember(member);
        }
    }

    @Override
    public void onUserExitRoomMessage(UserMessage message) {
        if (message != null && message.getUserId() != 0) {
            ChatMember member = new ChatMember();
            member.setLogo(message.getUserAvatarUrl());
            member.setNickName(message.getUserNickName());
            member.setUserName(message.getUserName());
            member.setUserId(message.getUserId());
            int totalNum = message.getCurrentUserCount();
            if (roomOnlineMember != null) {
                roomOnlineMember.setTotalNum(totalNum);
            }
            removeMember(member);
        }
    }

    @Override
    public void onExitMessage(ExitMessage message) {

    }

    @Override
    public void onLiveStartMessage(LiveStartMessage message) {

    }

    @Override
    public void onLiveEndMessage(LiveEndMessage message) {

    }

    @Override
    public void onLiveInputStreamMessage(LiveInputStreamMessage message) {

    }

    @Override
    public void onLiveInputStreamEndMessage(LiveInputStreamEndMessage message) {

    }

    @Override
    public void onLiveOutputStreamStartMessage(LiveOutputStreamStartMessage message) {

    }

    @Override
    public void onLiveOutputStreamEndMessage(LiveOutputStreamEndMessage message) {

    }

    @Override
    public final void onUserChatMessage(UserChatMessage message) {
        pushChatMessageQueue(message);
    }

    protected void pushChatMessageQueue(LiveMessage message) {
        if (userChatMessageQueue == null) {
            userChatMessageQueue = new LiveMessageQueue<>();
            userChatMessageQueue.setLiveMessageCallback(new LiveMessageQueue.LiveMessageCallback<LiveMessage>() {
                @Override
                public void onLiveMessageCallback(List<LiveMessage> messageList) {
                    onReceiveUserChatMessage(messageList);
                }
            });
            userChatMessageQueue.start();
        }
        userChatMessageQueue.push(message);
    }

    @Override
    public final void onReceiveGiftMessage(GiftMessage message) {
        if (userGiftMessageQueue == null) {
            userGiftMessageQueue = new LiveMessageQueue<>();
            userGiftMessageQueue.setLiveMessageCallback(new LiveMessageQueue.LiveMessageCallback<GiftMessage>() {

                @Override
                public void onLiveMessageCallback(List<GiftMessage> messageList) {
                    onReceiveUserGiftMessage(messageList);
                }
            });
            userGiftMessageQueue.start();
        }
        userGiftMessageQueue.push(message);
    }

    @Override
    public void onImageAndTextMessage(ImageTextMessage message) {

    }

    @Override
    public void onBetMessage(BetGuessMessage message) {

    }

    @Override
    public void onGuessResultMessage(GuessResultMessage message) {

    }

    @Override
    public void onBanUserMessage(LiveBanUserMessage message) {

    }

    @Override
    public void onNoTalkUserMessage(LiveNoTalkMessage message) {

    }

    @Override
    public void onAllowUserTalkMessage(LiveUserAllowTalkMessage message) {

    }

    @Override
    public void onRoomNoTalkMessage(LiveRoomNoTalkMessage message) {

    }

    @Override
    public void onRoomAllowTalkMessage(LiveRoomCancelNoTalkMessage message) {

    }

    /**
     * 接收到用户聊天信息
     */
    protected void onReceiveUserChatMessage(List<LiveMessage> messageList) {

    }

    protected void onReceiveUserGiftMessage(List<GiftMessage> messageList) {

    }


}

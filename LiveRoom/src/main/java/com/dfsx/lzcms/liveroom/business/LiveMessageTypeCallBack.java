package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.*;

/**
 * Created by liuwb on 2017/7/7.
 */
public interface LiveMessageTypeCallBack extends IRoomMessageType {

    void onUserJoinRoomMessage(UserMessage message);

    void onUserExitRoomMessage(UserMessage message);

    /**
     * 强制退出消息
     *
     * @param message
     */
    void onExitMessage(ExitMessage message);

    void onLiveStartMessage(LiveStartMessage message);

    void onLiveEndMessage(LiveEndMessage message);

    /**
     * 输入流开始消息
     *
     * @param message
     */
    void onLiveInputStreamMessage(LiveInputStreamMessage message);

    /**
     * 活动直播输入流结束消息
     *
     * @param message
     */
    void onLiveInputStreamEndMessage(LiveInputStreamEndMessage message);

    /**
     * 输出流开始消息
     * 用于设置线路播放等功能
     *
     * @param message
     */
    void onLiveOutputStreamStartMessage(LiveOutputStreamStartMessage message);

    void onLiveOutputStreamEndMessage(LiveOutputStreamEndMessage message);

    void onUserChatMessage(UserChatMessage message);

    void onReceiveGiftMessage(GiftMessage message);

    void onImageAndTextMessage(ImageTextMessage message);

    void onBetMessage(BetGuessMessage message);

    void onGuessResultMessage(GuessResultMessage message);

    void onBanUserMessage(LiveBanUserMessage message);

    void onNoTalkUserMessage(LiveNoTalkMessage message);

    void onAllowUserTalkMessage(LiveUserAllowTalkMessage message);

    void onRoomNoTalkMessage(LiveRoomNoTalkMessage message);

    void onRoomAllowTalkMessage(LiveRoomCancelNoTalkMessage message);
}
package com.dfsx.lzcms.liveroom.model;

/**
 * 直播间不允许说话
 */
public class LiveRoomNoTalkMessage extends LiveMessage {
    @Override
    public CharSequence getChatContentText() {
        return "管理员禁止聊天";
    }

    @Override
    public ChatViewType getChatViewType() {
        return ChatViewType.NOTE_MESSAGE;
    }
}

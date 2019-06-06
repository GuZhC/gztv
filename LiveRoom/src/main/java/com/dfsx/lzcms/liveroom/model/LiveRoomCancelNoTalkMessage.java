package com.dfsx.lzcms.liveroom.model;

public class LiveRoomCancelNoTalkMessage extends LiveMessage {

    @Override
    public CharSequence getChatContentText() {
        return "管理员允许聊天";
    }

    @Override
    public ChatViewType getChatViewType() {
        return ChatViewType.NOTE_MESSAGE;
    }
}

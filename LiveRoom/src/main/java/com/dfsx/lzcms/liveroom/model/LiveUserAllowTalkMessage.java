package com.dfsx.lzcms.liveroom.model;

/**
 * 用户被解除禁言消息
 */
public class LiveUserAllowTalkMessage extends UserMessage {
    @Override
    public CharSequence getChatContentText() {
        return "你已经被解除禁言了";
    }

    @Override
    public ChatViewType getChatViewType() {
        return ChatViewType.NOTE_MESSAGE;
    }
}

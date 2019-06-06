package com.dfsx.lzcms.liveroom.model;

/**
 * 用户被禁言消息
 */
public class LiveNoTalkMessage extends UserMessage {

    /**
     * 禁言时长(单位:分钟)
     */
    private int expired;

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    @Override
    public CharSequence getChatContentText() {
        String minute = expired == 0 ? "" : expired + "分钟";
        return "你被禁言" + minute;
    }

    @Override
    public ChatViewType getChatViewType() {
        return ChatViewType.NOTE_MESSAGE;
    }
}

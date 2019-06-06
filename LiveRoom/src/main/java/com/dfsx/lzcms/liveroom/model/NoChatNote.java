package com.dfsx.lzcms.liveroom.model;

/**
 * 禁止聊天数据
 */
public class NoChatNote implements IChatData {

    @Override
    public long getChatId() {
        return 0;
    }

    @Override
    public long getChatUserId() {
        return 0;
    }

    @Override
    public long getUserLevelId() {
        return 0;
    }

    @Override
    public long getChatTime() {
        return 0;
    }

    @Override
    public String getChatUserNickName() {
        return null;
    }

    @Override
    public String getChatUserLogo() {
        return null;
    }

    @Override
    public String getChatTimeText() {
        return "";
    }

    @Override
    public CharSequence getChatContentText() {
        return "直播禁止聊天";
    }

    @Override
    public ChatViewType getChatViewType() {
        return ChatViewType.NOTE_MESSAGE;
    }
}

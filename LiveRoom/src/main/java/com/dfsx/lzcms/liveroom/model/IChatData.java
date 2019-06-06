package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2017/6/20.
 */
public interface IChatData extends Serializable {
    enum ChatViewType {
        CURRENT_USER,
        NO_CURRENT_USER,
        NOTE_MESSAGE
    }

    long getChatId();

    long getChatUserId();

    long getUserLevelId();

    long getChatTime();

    String getChatUserNickName();

    String getChatUserLogo();

    String getChatTimeText();

    CharSequence getChatContentText();

    ChatViewType getChatViewType();
}

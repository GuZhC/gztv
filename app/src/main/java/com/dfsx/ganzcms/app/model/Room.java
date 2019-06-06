package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * 直播页面列表的数据类型
 * Created by liuwb on 2016/12/13.
 */
public interface Room extends Serializable {
    public static final int FLAG_PRIVACY = 1000;
    public static final int FLAG_YUGAO = 1001;
    public static final int FLAG_LIVE_ROOM = 1002;
    public static final int FLAG_BACK_PLAY = 1003;

    String getRoomTitle();

    String getRoomStatus();

    String getRoomImagePath();

    String getRoomOwnerName();

    long getRoomOwnerId();

    long getRoomOwnerLevelId();

    String getRoomOwnerLogo();

    long getRoomMessageCount();

    long getRoomTime();

    long getRoomId();

    LiveType getRoomLivetype();

    int getRoomFlag();

    boolean isNeedRoomPassword();

    /**
     * 是不是只是一个人的房间
     *
     * @return
     */
    boolean isOnlyUserRoom();

    String[] getRoomLabel();
}

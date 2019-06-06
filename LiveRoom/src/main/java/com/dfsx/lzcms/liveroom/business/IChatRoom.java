package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.*;

import java.util.List;

/**
 * Created by liuwb on 2017/7/3.
 */
public interface IChatRoom {
    boolean sendChatMessage(ChatMessage message);

    OnLineMember getRoomMember(int page, int pageSize);

    /**
     * @param userId
     * @param minutes 被禁言时长(单位:分钟), 0-解除禁言
     */
    void noTalk(long userId, int minutes, ICallBack<Boolean> callBack);

    void banUser(long userId, boolean isBanUser, ICallBack<Boolean> callBack);

    void getNoTalkUserList(ICallBack<List<NoTalkUser>> callBack);

    List<NoTalkUser> getNoTalkMember();

    void getBanUserList(ICallBack<List<RoomPerson>> callBack);

    List<RoomPerson> getBanUserListSync();
}

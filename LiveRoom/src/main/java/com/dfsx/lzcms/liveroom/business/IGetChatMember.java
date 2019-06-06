package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.ChatMember;

import java.util.List;

/**
 * Created by liuwb on 2017/6/9.
 */
public interface IGetChatMember {

    ChatMember getChatMember(String userName);


    List<ChatMember> getChatMemberList(String... userNames);
}

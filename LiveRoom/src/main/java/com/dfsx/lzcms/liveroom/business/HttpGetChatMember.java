package com.dfsx.lzcms.liveroom.business;

import android.content.Context;
import com.dfsx.lzcms.liveroom.model.ChatMember;
import com.dfsx.lzcms.liveroom.model.UserDetailsInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuwb on 2017/6/9.
 */
public class HttpGetChatMember implements IGetChatMember {

    private Context context;
    private IGetUserInfo userInfoGetter;

    public HttpGetChatMember(Context context) {
        this.context = context;
        userInfoGetter = new HttpGetUserInfo(context);
    }


    @Override
    public ChatMember getChatMember(String userName) {
        UserDetailsInfo userInfo = userInfoGetter.getUserInfo(userName);
        return toChatMember(userName, userInfo);
    }

    @Override
    public List<ChatMember> getChatMemberList(String... userNames) {
        List<UserDetailsInfo> list = userInfoGetter.getUserInfoList(userNames);
        if (list != null) {
            List<ChatMember> members = new ArrayList<>();
            HashMap<String, UserDetailsInfo> nameMap = toUserNameMap(list);
            for (String name : userNames) {
                members.add(toChatMember(name,
                        nameMap.get(name)));
            }
            return members;
        }
        return null;
    }

    private HashMap<String, UserDetailsInfo> toUserNameMap(List<UserDetailsInfo> list) {
        HashMap<String, UserDetailsInfo> map = new HashMap<>();
        for (UserDetailsInfo info : list) {
            map.put(info.getUsername(), info);
        }
        return map;
    }

    private ChatMember toChatMember(String userName, UserDetailsInfo userInfo) {
        ChatMember chatMember = new ChatMember();
        if (userInfo != null) {
            chatMember.setNickName(userInfo.getNickname());
            chatMember.setUserName(userInfo.getUsername());
            chatMember.setUserId(userInfo.getId());
            chatMember.setLogo(userInfo.getAvatar_url());
            chatMember.setUserDetailsInfo(userInfo);
        } else {
            chatMember.setUserName(userName);
        }
        return chatMember;
    }
}

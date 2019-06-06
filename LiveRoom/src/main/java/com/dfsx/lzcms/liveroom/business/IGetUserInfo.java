package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.UserDetailsInfo;

import java.util.List;

/**
 * Created by liuwb on 2017/6/9.
 */
public interface IGetUserInfo {

    UserDetailsInfo getUserInfo(String userName);

    List<UserDetailsInfo> getUserInfoList(String... userNames);
}

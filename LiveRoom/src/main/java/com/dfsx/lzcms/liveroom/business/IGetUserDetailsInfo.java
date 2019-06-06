package com.dfsx.lzcms.liveroom.business;

import com.dfsx.lzcms.liveroom.model.UserDetailsInfo;

/**
 * Created by liuwb on 2017/3/13.
 */
public interface IGetUserDetailsInfo {

    UserDetailsInfo getUserDetailsSync(long userId);

    void getUserDetailsAsync(long userId, ICallBack<UserDetailsInfo> iCallBack);
}

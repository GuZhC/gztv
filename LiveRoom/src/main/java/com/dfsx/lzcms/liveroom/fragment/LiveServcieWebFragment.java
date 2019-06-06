package com.dfsx.lzcms.liveroom.fragment;

import com.dfsx.lzcms.liveroom.LiveServiceRoomActivity;

public class LiveServcieWebFragment extends BaseAndroidWebFragment {

    @Override
    protected String getWebUrl() {
        if (activity != null && activity instanceof LiveServiceRoomActivity) {
            LiveServiceRoomActivity serviceRoomActivity = (LiveServiceRoomActivity) activity;
            serviceRoomActivity.getRoomId();
        }
        return super.getWebUrl();
    }
}

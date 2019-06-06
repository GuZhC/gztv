package com.dfsx.lzcms.liveroom.fragment;

import android.util.Log;
import com.dfsx.lzcms.liveroom.business.AppManager;

public class SignupWebFragment extends VoteWebFragment {

    @Override
    protected String getWebUrl() {
        String urlScheme = AppManager.getInstance().getIApp().getMobileWebUrl();

        String webUrl = urlScheme + "/live/signup/" + getRoomId() + "/" + getRoomEnterId() + getWebUrlParams();
        Log.e("TAG", "web url == " + webUrl);
        return webUrl;
    }
}

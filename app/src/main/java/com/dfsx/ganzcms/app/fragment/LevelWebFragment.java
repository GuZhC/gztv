package com.dfsx.ganzcms.app.fragment;

import android.util.Log;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.fragment.VoteWebFragment;

public class LevelWebFragment extends VoteWebFragment {

    @Override
    protected String getWebUrl() {
        String urlScheme = AppManager.getInstance().getIApp().getMobileWebUrl();

        String webUrl = urlScheme + "/user/level" + getWebUrlParams();
        Log.e("TAG", "web url == " + webUrl);
        return webUrl;
    }
}

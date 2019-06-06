package com.dfsx.lzcms.liveroom.fragment;

import android.util.Log;
import com.dfsx.lzcms.liveroom.business.AppManager;

public class QuestionnaireFragment extends VoteWebFragment {

    @Override
    protected String getWebUrl() {
        //http://192.168.6.85:8080
        String urlScheme = AppManager.getInstance().getIApp().getMobileWebUrl();

        String webUrl = urlScheme + "/live/questionnaire/" + getRoomId() + "/" + getRoomEnterId() + getWebUrlParams();
        Log.e("TAG", "web url == " + webUrl);
        return webUrl;
    }
}

package com.dfsx.ganzcms.app.fragment;

import android.os.Bundle;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.fragment.VoteWebFragment;

public class LotteryDrawnWebFragment extends VoteWebFragment {

    public static final String INTENT_DRAWN_ID = "LotteryDrawnWebFragment_id";

    @Override
    protected String getWebUrl() {
        String urlScheme = AppManager.getInstance().getIApp().getMobileWebUrl();
        String webUrl = urlScheme + "/user/message/" + getDrawnID() + getWebUrlParams();
        return webUrl;
    }

    private long getDrawnID() {
        long id = 0;
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getLong(INTENT_DRAWN_ID, 0L);
        }
        return id;
    }
}

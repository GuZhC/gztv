package com.dfsx.ganzcms.app.business;

import com.dfsx.ganzcms.app.App;
import com.dfsx.searchlibaray.ISearchConfig;

public class SearchConfig implements ISearchConfig {
    @Override
    public String getHttpBaseUrl() {
        return App.getInstance().getmSession().getPortalServerUrl();
    }

    @Override
    public String getCmsHttpUrl() {
        return App.getInstance().getmSession().getContentcmsServerUrl();
    }

    @Override
    public String getLiveHttpUrl() {
        return App.getInstance().getmSession().getLiveServerUrl();
    }

    @Override
    public String getQuanZiHttpUrl() {
        return App.getInstance().getmSession().getCommunityServerUrl();
    }

    @Override
    public boolean isLogin() {
        return App.getInstance().getUser() != null && App.getInstance().getUser().getUser() != null;
    }

    @Override
    public String getCurrentToken() {
        return App.getInstance().getCurrentToken();
    }
}

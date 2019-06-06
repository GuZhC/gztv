package com.dfsx.searchlibaray;

public interface ISearchConfig {

    String getHttpBaseUrl();

    String getCmsHttpUrl();

    String getLiveHttpUrl();

    String getQuanZiHttpUrl();

    boolean isLogin();

    String getCurrentToken();
}

package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2017/6/16.
 */
public interface ITabUserView extends Serializable {

    String getTabViewText();

    String getTabViewImageUrl();

    int getTabViewImageRes();

    boolean isShowRightTopPoint();

    String getTabImageContent();

    String getTabTextContent();

    boolean isShowDivideView();
}

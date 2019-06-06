package com.dfsx.ganzcms.app.view.zoom;

/**
 * Created by Administrator on 2015/12/2.
 */
public interface IPullZoom {

    boolean isReadyForPullStart();

    void onPullZooming(int newScrollValue);

    void onPullZoomEnd();

}

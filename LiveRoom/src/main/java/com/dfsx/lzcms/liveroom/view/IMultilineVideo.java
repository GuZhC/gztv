package com.dfsx.lzcms.liveroom.view;

import java.util.List;

/**
 * Created by liuwb on 2017/7/6.
 */
public interface IMultilineVideo {
    long getId();

    String getName();

    String getLineTitle();

    List<String> getVideoUrlList();

    List<Long> getVideoDurationList();

    void setVideoUrlList(List<String> urlList);

    boolean isSelected();

    void setSelected(boolean isSelected);
}

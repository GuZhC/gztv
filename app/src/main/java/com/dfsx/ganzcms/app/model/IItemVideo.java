package com.dfsx.ganzcms.app.model;

import rx.Observable;

/**
 * 电视剧的每一集
 */
public interface IItemVideo {
    long getId();

    int getItemIndex();

    String getTitle();

    Observable<VideoContent> getVideoInfo();

    boolean isPlaying();

    void setPlaying(boolean isPlaying);
}

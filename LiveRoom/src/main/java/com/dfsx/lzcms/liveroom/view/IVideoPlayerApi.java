package com.dfsx.lzcms.liveroom.view;

/**
 * Created by liuwb on 2017/7/6.
 */
public interface IVideoPlayerApi extends IVideoPlayer {
    boolean isPlaying();

    void switchScreen(boolean isFull);

    boolean isInPlayBackStatus();

    long getCurrentPosition();

    long getDuration();

    int getBufferPercentage();

    void seekTo(int time);
}

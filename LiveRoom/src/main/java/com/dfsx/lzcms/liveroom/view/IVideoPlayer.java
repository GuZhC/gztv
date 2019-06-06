package com.dfsx.lzcms.liveroom.view;

/**
 * 播放器的实现类。实现对MediaPlayer的控制
 * Created by liuwb on 2017/3/16.
 */
public interface IVideoPlayer {
    void start();

    /**
     * 停止之后重新播放
     */
    void restart();

    void pause();

    void stop();

    void release();
}

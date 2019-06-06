package com.dfsx.lzcms.liveroom;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import com.dfsx.lzcms.liveroom.view.IMultilineVideo;
import com.dfsx.lzcms.liveroom.view.LiveServiceVideoPlayer;
import com.dfsx.videoijkplayer.VideoPlayView;

import java.util.ArrayList;

/**
 * Created by liuwb on 2017/7/6.
 */
public abstract class AbsLiveServiceVideoActivity extends AbsChatRoomActivity {

    protected LiveServiceVideoPlayer videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createVideoPlayer();

    }

    private void createVideoPlayer() {
        videoPlayer = new LiveServiceVideoPlayer(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        videoPlayer.setLayoutParams(params);
    }

    public void startVideo(String path) {
        if (videoPlayer != null) {
            videoPlayer.start(path);
        }
    }

    public void startVideo(IMultilineVideo multilineVideo) {
        if (videoPlayer != null) {
            videoPlayer.start(multilineVideo);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged -- ");
        if (videoPlayer != null) {
            onVideoSwitchScreen(videoPlayer, newConfig.orientation != Configuration.ORIENTATION_PORTRAIT);
            videoPlayer.onActivityConfigChange(newConfig);
        }
    }

    /**
     * 向现实Video的容器里添加VideoPlayer
     *
     * @param videoPlayer
     */
    public abstract void onVideoSwitchScreen(LiveServiceVideoPlayer videoPlayer, boolean isFullScreen);

    /**
     * 向现实Video的容器里添加VideoPlayer
     *
     * @param videoPlayer
     */
    public abstract void addVideoPlayerToContainer(LiveServiceVideoPlayer videoPlayer);

    public void removeVideoPlayerFromContainer(LiveServiceVideoPlayer videoPlayer) {
        if (videoPlayer != null) {
            ViewGroup view = (ViewGroup) videoPlayer.getParent();
            if (view != null) {
                view.removeView(videoPlayer);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        removeVideoPlayerFromContainer(videoPlayer);
        addVideoPlayerToContainer(videoPlayer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getResumePlayerStatus()) {
            videoPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearVideoPlayStatus();
        if (videoPlayer.isPlaying()) {
            saveVideoPlayStatus();
            videoPlayer.pause();
        } else if (!videoPlayer.isInPlayBackStatus()) {
            videoPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (videoPlayer.isFullScreen()) {
            videoPlayer.switchScreen();
            return;
        }
        super.onBackPressed();
    }

    private boolean getResumePlayerStatus() {
        String key = "ABsVideo_play_" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        boolean isPaly = sp.getBoolean(key, false);
        sp.edit().clear().commit();
        return isPaly;
    }

    private void saveVideoPlayStatus() {
        String key = "ABsVideo_play_" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        sp.edit().putBoolean(key, videoPlayer.isPlaying()).commit();
    }

    private void clearVideoPlayStatus() {
        String key = "ABsVideo_play_" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        sp.edit().clear().commit();
    }

    private void releaseVideo() {
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.release();
            videoPlayer = null;
            clearVideoPlayStatus();
            removeVideoPlayerFromContainer(videoPlayer);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseVideo();
    }
}

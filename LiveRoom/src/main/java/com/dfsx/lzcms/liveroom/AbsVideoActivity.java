package com.dfsx.lzcms.liveroom;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import com.dfsx.videoijkplayer.VideoPlayView;

/**
 * Created by liuwb on 2016/8/19.
 */
public abstract class AbsVideoActivity extends AbsChatRoomActivity {
    protected VideoPlayView videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createVideoPlayer();

    }

    private void createVideoPlayer() {
        videoPlayer = new VideoPlayView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        videoPlayer.setLayoutParams(params);
    }

    public void startVideo(String path) {
        if (videoPlayer != null) {
            videoPlayer.start(path);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged -- ");
        if (videoPlayer != null) {
            videoPlayer.onChanged(newConfig);
            removeVideoPlayerFromContainer(videoPlayer);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                addVideoPlayerToContainer(videoPlayer);
            } else {
                addVideoPlayerToFullScreenContainer(videoPlayer);

            }
        }
    }

    /**
     * 向现实Video的容器里添加VideoPlayer
     *
     * @param videoPlayer
     */
    public abstract void addVideoPlayerToContainer(VideoPlayView videoPlayer);


    public void removeVideoPlayerFromContainer(VideoPlayView videoPlayer) {
        if (videoPlayer != null) {
            ViewGroup view = (ViewGroup) videoPlayer.getParent();
            if (view != null) {
                view.removeView(videoPlayer);
            }
        }
    }

    /**
     * 想全屏的video容器中添加VideoPlayer
     *
     * @param videoPlayer
     */
    public abstract void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer);

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
        if (videoPlayer.isPlay()) {
            saveVideoPlayStatus();
            videoPlayer.pause();
        } else if (!videoPlayer.isInPlaybackState()) {
            videoPlayer.release();
        }
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
        sp.edit().putBoolean(key, videoPlayer.isPlay()).commit();
    }

    private void clearVideoPlayStatus() {
        String key = "ABsVideo_play_" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        sp.edit().clear().commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.stop();
        videoPlayer.release();
        videoPlayer.onDestroy();
        videoPlayer = null;
        clearVideoPlayStatus();
        removeVideoPlayerFromContainer(videoPlayer);
    }

}

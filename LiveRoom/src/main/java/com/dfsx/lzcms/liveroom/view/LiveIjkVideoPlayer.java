package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import com.dfsx.videoijkplayer.media.Settings;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/8/16.
 */
public class LiveIjkVideoPlayer extends FrameLayout implements IVideoPlayer {
    private static final String TAG = "LiveIjkVideoPlayer";
    private IjkVideoView ijkVideoView;
    private LiveMediaController controller;
    private Uri videouri;
    private ArrayList<IMediaPlayer.OnCompletionListener> onCompletionListenerList = new ArrayList<>();
    private int restartCount;
    private Settings settings;

    private boolean isRestartOnComplete = true;

    public LiveIjkVideoPlayer(Context context) {
        this(context, null);
    }

    public LiveIjkVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveIjkVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveIjkVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        settings = new Settings(context.getApplicationContext());
        ijkVideoView = new IjkVideoView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ijkVideoView.setLayoutParams(params);
        addView(ijkVideoView, 0);

        initSettings();
        controller = new LiveMediaController(context, this);
        controller.setLiveVideoPlayer(this);
        controller.setVideoPlayer(ijkVideoView);
        ijkVideoView.setMediaController(controller);

        ijkVideoView.setVideoAspectRatio(3);


        ijkVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Log.e(TAG, "onCompletion error---");
                if (restartCount < 1 && isRestartOnComplete) {
                    restartCount++;
                    restart();
                }
                for (IMediaPlayer.OnCompletionListener listener : onCompletionListenerList) {
                    listener.onCompletion(iMediaPlayer);
                }
            }
        });


    }

    private void initSettings() {
        settings.setUsingMediaCodec(true);
    }

    public void setVideoThumbImage(String image) {
        if (controller != null) {
            controller.setVideoThumbImage(image);
        }
    }

    public int getDuration() {
        return ijkVideoView.getDuration();
    }

    public int getCurrentPosition() {
        return ijkVideoView.getCurrentPosition();
    }

    public int getBufferPercentage() {
        return ijkVideoView.getBufferPercentage();
    }

    public void seekTo(int mesc) {
        ijkVideoView.seekTo(mesc);
    }

    public void setVideoRotation(int rotation, boolean isForce) {
        ijkVideoView.setVideoRotation(rotation, isForce);
    }

    public void showNoteText(String text) {
        if (controller != null) {
            controller.showNoteText(text);
        }
    }

    public void start(String path) {
        restartCount = 0;
        videouri = Uri.parse(path);
        if (controller != null) {
            controller.start();
        }
        setMediaPlayerByPath(path);
        if (!ijkVideoView.isPlaying()) {
            ijkVideoView.setVideoURI(videouri);
            ijkVideoView.start();
        } else {
            ijkVideoView.stopPlayback();
            ijkVideoView.setVideoURI(videouri);
            ijkVideoView.start();
        }
    }

    private void setMediaPlayerByPath(String path) {
        if (!TextUtils.isEmpty(path) && path.endsWith(".m3u8")) {
            settings.setPlayer(Settings.PV_PLAYER__IjkExoMediaPlayer);
        } else {
            settings.setPlayer(Settings.PV_PLAYER__IjkMediaPlayer);
        }
    }

    /**
     * 开始播放，从以前暂停的位置开始
     */
    public void start() {
        if (!ijkVideoView.isPlaying()) {
            ijkVideoView.start();
        }
    }

    public void pause() {
        ijkVideoView.pause();
    }

    public boolean isPlaying() {
        return ijkVideoView.isPlaying();
    }

    public boolean isOnComplete() {
        return ijkVideoView.getCurrentStatue() == IjkVideoView.STATE_PLAYBACK_COMPLETED;
    }

    public void stop() {
        post(new Runnable() {
            @Override
            public void run() {
                if (ijkVideoView.isPlaying()) {
                    ijkVideoView.stopPlayback();
                }
                ijkVideoView.release(true);
            }
        });
        removeView(ijkVideoView);
    }

    @Override
    public void release() {
        ijkVideoView.release(true);
    }

    public boolean isInPlaybackState() {
        return ijkVideoView.isInPlaybackState();
    }

    /**
     * 重新开始从初始状态开始播放
     */
    public void restart() {
        if (getChildAt(0) != null && getChildAt(0) instanceof IjkVideoView) {
            removeView(ijkVideoView);
        }
        addView(ijkVideoView, 0);
        if (!ijkVideoView.isPlaying()) {
            ijkVideoView.setVideoURI(videouri);
            ijkVideoView.start();
        } else {
            ijkVideoView.stopPlayback();
            ijkVideoView.setVideoURI(videouri);
            ijkVideoView.start();
        }
        if (controller != null) {
            controller.showLoading();
        }
    }


    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        onCompletionListenerList.clear();
        onCompletionListenerList.add(l);
    }

    public void addOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        onCompletionListenerList.add(l);
    }

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        if (controller != null) {
            controller.setOnPreparedListenerCallback(l);
        }
    }

    public void addOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        if (controller != null) {
            controller.addOnPreparedListenerCallback(l);
        }
    }

    public void setRestartOnComplete(boolean restartOnComplete) {
        isRestartOnComplete = restartOnComplete;
    }
}

package com.dfsx.ganzcms.app.business;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.dfsx.ganzcms.app.App;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class RadioPlayManager {
    private static RadioPlayManager instance = new RadioPlayManager();

    public static final int STATE_INIT = 0;
    public static final int STATE_PREPARE = 1;
    public static final int STATE_PLAYING = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_COMPLETE = 4;
    public static final int STATE_PAUSE = 5;
    public static final int STATE_LOADING = 6;

    private AndroidMediaPlayer mediaPlayer;
    private MediaCallBack mediaCallBack;

    private int currentState;
    private int userTagState;

    private Handler handler = new Handler(Looper.getMainLooper());

    private RadioPlayManager() {
        currentState = STATE_INIT;
        userTagState = STATE_INIT;
    }


    public static RadioPlayManager getInstance() {
        return instance;
    }

    private void initRadio() {
        if (mediaPlayer == null) {
            mediaPlayer = new AndroidMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (mediaCallBack == null) {
            mediaCallBack = new MediaCallBack();
        }
        mediaPlayer.setOnPreparedListener(mediaCallBack);
        mediaPlayer.setOnCompletionListener(mediaCallBack);
        mediaPlayer.setOnErrorListener(mediaCallBack);
        mediaPlayer.setOnInfoListener(mediaCallBack);
        mediaPlayer.setOnBufferingUpdateListener(mediaCallBack);
    }

    public boolean play(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            initRadio();
            userTagState = STATE_PLAYING;
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            currentState = STATE_PREPARE;
            notifyStateChange();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void pause() {
        userTagState = STATE_PAUSE;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            currentState = STATE_PAUSE;
            notifyStateChange();
        }
    }

    public boolean start() {
        if (isBackPlayState() && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            currentState = STATE_PLAYING;
            notifyStateChange();
            return true;
        }
        return false;
    }

    public boolean isBackPlayState() {
        if (currentState == STATE_INIT ||
                currentState != STATE_COMPLETE ||
                currentState != STATE_PREPARE ||
                currentState != STATE_ERROR) {
            return false;
        }
        return true;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void stop() {
        userTagState = STATE_INIT;
        if (currentState != STATE_INIT) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            //            mediaPlayer.release();
            currentState = STATE_INIT;
            notifyStateChange();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        userTagState = STATE_INIT;
        currentState = STATE_INIT;
    }

    class MediaCallBack implements IMediaPlayer.OnBufferingUpdateListener,
            IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
            IMediaPlayer.OnInfoListener, IMediaPlayer.OnPreparedListener {

        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {

        }

        @Override
        public void onCompletion(IMediaPlayer mp) {
            currentState = STATE_COMPLETE;
            notifyStateChange();
        }

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            currentState = STATE_ERROR;
            notifyStateChange();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(App.getInstance(),
                            "播放失败", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }

        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                currentState = STATE_LOADING;
                notifyStateChange();
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                currentState = STATE_PLAYING;
                notifyStateChange();
            }
            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (userTagState == STATE_PLAYING) {
                mediaPlayer.start();
                currentState = STATE_PLAYING;
                notifyStateChange();
            }
        }
    }

    private void notifyStateChange() {
        if (stateChangeListener != null) {
            stateChangeListener.onStateChange(currentState);
        }
    }

    private OnMediaPlayStateChangeListener stateChangeListener;

    public void setOnMediaPlayStateChangeListener(OnMediaPlayStateChangeListener listener) {
        this.stateChangeListener = listener;
    }

    public interface OnMediaPlayStateChangeListener {
        void onStateChange(int currentState);
    }
}

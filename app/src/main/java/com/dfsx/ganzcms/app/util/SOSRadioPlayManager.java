package com.dfsx.ganzcms.app.util;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.dfsx.ganzcms.app.App;


public class SOSRadioPlayManager {
    private static SOSRadioPlayManager instance = new SOSRadioPlayManager();

    public static final int STATE_INIT = 0;
    public static final int STATE_PREPARE = 1;
    public static final int STATE_PLAYING = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_COMPLETE = 4;
    public static final int STATE_PAUSE = 5;
    public static final int STATE_LOADING = 6;

    private MediaPlayer mediaPlayer;
    private MediaCallBack mediaCallBack;

    private int currentState;
    private int userTagState;

    private Handler handler = new Handler(Looper.getMainLooper());

    private SOSRadioPlayManager() {
        currentState = STATE_INIT;
        userTagState = STATE_INIT;
    }


    public static SOSRadioPlayManager getInstance() {
        return instance;
    }

    private void initRadio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
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
        return play(url, false);
    }

    public boolean playAssetFile(AssetFileDescriptor afd, boolean isLooping) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            initRadio();
            userTagState = STATE_PLAYING;
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mediaPlayer.setLooping(isLooping);
            mediaPlayer.prepareAsync();

            currentState = STATE_PREPARE;
            notifyStateChange();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean play(String url, boolean isLooping) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            initRadio();
            userTagState = STATE_PLAYING;
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(isLooping);
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
        if (mediaPlayer.isPlaying()) {
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

    private boolean isBackPlayState() {
        if (currentState == STATE_INIT ||
                currentState != STATE_COMPLETE ||
                currentState != STATE_PREPARE ||
                currentState != STATE_ERROR) {
            return false;
        }
        return true;
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

    class MediaCallBack implements MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
            MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            currentState = STATE_COMPLETE;
            notifyStateChange();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
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
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
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
        public void onPrepared(MediaPlayer mp) {
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

package com.dfsx.videoijkplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import com.dfsx.videoijkplayer.media.Settings;
import com.dfsx.videoijkplayer.util.NetworkChangeManager;
import com.dfsx.videoijkplayer.util.NetworkChangeReceiver;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Description 播放view
 */
public class VideoPlayView extends RelativeLayout implements MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, NetworkChangeReceiver.OnNetworkChangeListener {


    private CustomMediaContoller mediaController;
    private View player_btn, view;
    private IjkVideoView mVideoView;
    private Handler handler = new Handler();
    private boolean isPause;

    private View rView;
    private Context mContext;
    private boolean portrait;
    private Settings settings;
    private boolean isLive;
    private boolean isUseHardCodec = true;
    //    private OrientationEventListener orientationEventListener;

    public VideoPlayView(Context context) {
        super(context);
        mContext = context;
        initData();
        initViews();
        initActions();
    }

    private void initData() {
        settings = new Settings(mContext.getApplicationContext());
    }

    private void initViews() {

        rView = LayoutInflater.from(mContext).inflate(R.layout.view_video_item, this, true);
        view = findViewById(R.id.media_contoller);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        mediaController = new CustomMediaContoller(mContext, rView);
        mVideoView.setMediaController(mediaController);

        //heyang  2017/12/12  加载完监听
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                if (preparedListener != null)
                    preparedListener.preparedlistener(iMediaPlayer);
            }
        });

        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                view.setVisibility(View.GONE);
                mediaController.onCompletion();
                if (mediaController.getScreenOrientation((Activity) mContext)
                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //横屏播放完毕，重置
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    setLayoutParams(layoutParams);
                }
                if (completionListener != null)
                    completionListener.completion(mp);
            }
        });

        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.VideoView_error_text_unknown),
                        Toast.LENGTH_SHORT).show();
                if (mediaController != null) {
                    mediaController.onPlayError();
                }
                if (errorListener != null)
                    errorListener.errorListener(iMediaPlayer);
                return true;
            }
        });
    }

    private void initActions() {
        //注册网络变化的监听器
        NetworkChangeManager.getInstance().addOnNetworkChangeListener(this);
        /*orientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int orientation) {
                Log.e("onOrientationChanged", "orientation");
                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    //竖屏
                    if (portrait) {
                        ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!portrait) {
                        ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };*/
    }

    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }

    //  heyang 2017-12-5
    public int getVideoDuration() {
        return mVideoView.getDuration();
    }

    public int getVideoCurrentPostion() {
        return mVideoView.getCurrentPosition();
    }

    private NetChecker netChecker;

    private String playPath;

    public void start(String path) {
        start(path, false);
    }

    public void start(String path, boolean isLive) {
        playPath = path;
        this.isLive = isLive;
        if (mediaController != null) {
            mediaController.setLive(isLive);
        }
        if (netChecker == null) {
            netChecker = new NetChecker(mContext, new NetChecker.CheckCallBack() {
                @Override
                public void callBack(boolean isCouldPlay, Object tag) {
                    if (isCouldPlay) {
                        if (tag != null && tag instanceof String) {
                            String tagPath = (String) tag;
                            if (!TextUtils.isEmpty(tagPath)) {
                                startPlayPath(tagPath);
                            }
                        } else {
                            if (isInPlaybackState()) {
                                start();
                            }
                        }
                    }
                }
            });
        }
        netChecker.checkNet(path);
    }

    private void startPlayPath(String path) {
        setMediaPlayerByPath(path);
        Uri uri = Uri.parse(path);
        if (mediaController != null)
            mediaController.start();
        if (!mVideoView.isPlaying()) {
            mVideoView.release(true);
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
    }

    /**
     * m3u8和MP4的格式的视屏用IjkExoMediaPlayer
     *
     * @param path
     */
    private void setMediaPlayerByPath(String path) {
        if (!TextUtils.isEmpty(path) &&
                (path.endsWith(".m3u8") || path.endsWith(".mp4"))
                &&
                isLive) {
            settings.setPlayer(Settings.PV_PLAYER__IjkExoMediaPlayer);
        } else {
            settings.setUsingMediaCodec(isUseHardCodec);
            settings.setPlayer(Settings.PV_PLAYER__IjkKsyMediaPlayer);
        }
    }


    public void start() {
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }

    public void setContorllerVisiable() {
        mediaController.setVisiable();
    }

    public void seekTo(int msec) {
        mVideoView.seekTo(msec);
    }

    public boolean isInPlaybackState() {
        return mVideoView.isInPlaybackState();
    }

    public void onChanged(Configuration configuration) {
        portrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }

    public void switchScreen() {
        if (mediaController != null) {
            mediaController.switchScreen();
        }
    }

    public void doOnConfigurationChanged(final boolean portrait) {
        if (mVideoView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        Log.e("handler", "400");
                        setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
                        Log.e("handler", "height==" + heightPixels + "\nwidth==" + widthPixels);
                        setLayoutParams(layoutParams);
                    }
                }
            });
            //            orientationEventListener.enable();
        }
    }

    public void stop() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //        orientationEventListener.disable();
    }

    private void setFullScreen(boolean fullScreen) {
        if (mContext != null && mContext instanceof Activity) {
            WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    /**
     * 全屏隐藏
     */
    public void setFullGone(){
        mediaController.setFullGone();
    }

    /**
     * 静音
     */
    public void setMuteAudioe(){
        mediaController.setMuteAudioe();
    }
    /**
     * 设置是否使用硬件编码
     *
     * @param isHardCodeC
     */
    public void setIsUserHardCodec(boolean isHardCodeC) {
        this.isUseHardCodec = isHardCodeC;
    }

    public void setShowContoller(boolean isShowContoller) {
        mediaController.setShowContoller(isShowContoller);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public long getPalyPostion() {
        return mVideoView.getCurrentPosition();
    }

    public void release() {
        mVideoView.release(true);
        NetworkChangeManager.getInstance().removeOnNetworkChangeListener(this);
    }

    public boolean isFullScreen() {
        return mediaController.isFullScreen();
    }

    public int VideoStatus() {
        return mVideoView.getCurrentStatue();
    }

    private CompletionListener completionListener;

    public void setErrorListener(OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    private OnErrorListener errorListener;

    public void setPreparedListener(OnPreparedListener preparedListener) {
        this.preparedListener = preparedListener;
    }

    private OnPreparedListener preparedListener;

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    @Override
    public void onChange(int networkType) {
        if (networkType == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toast.makeText(getContext(), "网络连接已断开", Toast.LENGTH_SHORT).show();
        } else if (networkType == NetworkUtil.TYPE_MOBILE) {
            if (isPlay()) {
                pause();
            }
            if (netChecker != null) {
                netChecker.checkNet(null);
            }
        }
    }

    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }

    /**
     * heyang  2017-9-1  增加报错外放
     */
    public interface OnErrorListener {
        void errorListener(IMediaPlayer mp);
    }

    /**
     * heyang  2017-12-12  增加播放加载完开始接口
     */
    public interface OnPreparedListener {
        void preparedlistener(IMediaPlayer mp);
    }

}

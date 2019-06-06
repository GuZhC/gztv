package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.lzcms.liveroom.util.PixelUtil;
import com.dfsx.videoijkplayer.NetChecker;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import com.dfsx.videoijkplayer.media.Settings;
import com.fivehundredpx.android.blur.BlurringView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 竖直方向全屏的视频播放器
 * 主要支持视频列表播放， 支持根据视频的宽高选择合适的位置
 * Created by liuwb on 2017/3/16.
 */
public class LiveFullRoomVideoPlayer extends FrameLayout implements IVideoPlayer {

    public static final int DP_TOP_VIDEO_HEIGHT = 240;
    public static final int DP_TOP_VIDEO_MARGIN = 100;

    private Context context;

    private FrameLayout fullScreenVideoView;
    private FrameLayout topScreenVideoView;

    private FrameLayout videoBackgroundView;
    private ImageView bkgImageView;
    private BlurringView bkgBlurView;
    private IjkVideoView ijkVideoView;
    private LiveMediaController controller;
    private Uri videouri;
    private ArrayList<IMediaPlayer.OnCompletionListener> onCompletionListenerList = new ArrayList<>();
    private int restartCount;
    private Settings settings;

    private ArrayList<String> videoUrlList = new ArrayList<>();

    private HashMap<Integer, Long> videoPositionTimeMap = new HashMap<>();

    private int currentPlayCount;

    private boolean isRestartOnComplete = true;

    private boolean isForceUserFullScreenVideo;

    private OnVideoPositionListener videoPositionListener;

    /**
     * 设置是否是直播
     */
    private boolean isLiving = true;

    private FrameLayout latestVideoView;

    /**
     * 是横屏显示Video么
     */
    private boolean isLandscapeVideo;

    private NetChecker netChecker;

    private ArrayList<IMediaPlayer.OnPreparedListener> onPreparedListenerList = new ArrayList<>();

    public LiveFullRoomVideoPlayer(Context context) {
        this(context, null);
    }

    public LiveFullRoomVideoPlayer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveFullRoomVideoPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveFullRoomVideoPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).
                inflate(R.layout.live_full_video_layout, this);
        fullScreenVideoView = (FrameLayout) findViewById(R.id.full_screen_video);
        topScreenVideoView = (FrameLayout) findViewById(R.id.top_screen_video);
        videoBackgroundView = (FrameLayout) findViewById(R.id.video_background);
        bkgImageView = (ImageView) findViewById(R.id.background_img);
        bkgBlurView = (BlurringView) findViewById(R.id.background_img_blur);

        bkgBlurView.setBlurredView(bkgImageView);

        ijkVideoView = new IjkVideoView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ijkVideoView.setLayoutParams(params);

        latestVideoView = fullScreenVideoView;
        fullScreenVideoView.addView(ijkVideoView, params);
        videoBackgroundView.setVisibility(VISIBLE);

        fullScreenVideoView.setVisibility(VISIBLE);
        topScreenVideoView.setVisibility(VISIBLE);

        isLandscapeVideo = false;
        settings = new Settings(context.getApplicationContext());
        initSettings();

        controller = new LiveMediaController(context, this);
        controller.setLiveVideoPlayer(this);
        controller.setVideoPlayer(ijkVideoView);
        ijkVideoView.setMediaController(controller);

        controller.setOnPreparedListenerCallback(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                Log.e("TAG", "onPrepared -----------------LiveFullRoomVideoPlayer");
                int height = iMediaPlayer.getVideoHeight();
                int width = iMediaPlayer.getVideoWidth();
                boolean isShowVideoTop = !isForceUserFullScreenVideo && width > height;
                if (isShowVideoTop) {
                    if (latestVideoView != topScreenVideoView && !isLandscapeVideo) {
                        latestVideoView = topScreenVideoView;
                        removeVideoView();
                        FrameLayout.LayoutParams paramsTop = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        topScreenVideoView.addView(ijkVideoView, paramsTop);
                        topScreenVideoView.setVisibility(VISIBLE);
                        ijkVideoView.setVideoAspectRatio(1);
                        videoBackgroundView.setVisibility(VISIBLE);
                        if (videoPositionListener != null) {
                            videoPositionListener.onScreenTop();
                        }
                    }
                } else {
                    if (latestVideoView != fullScreenVideoView) {
                        latestVideoView = fullScreenVideoView;
                        removeVideoView();
                        FrameLayout.LayoutParams paramsFull = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                        fullScreenVideoView.addView(ijkVideoView, paramsFull);
                        fullScreenVideoView.setVisibility(VISIBLE);
                        ijkVideoView.setVideoAspectRatio(3);
                        videoBackgroundView.setVisibility(VISIBLE);
                        if (videoPositionListener != null) {
                            videoPositionListener.onFullScreen();
                        }
                    }
                }
                videoPositionTimeMap.put(currentPlayCount, iMediaPlayer.getDuration());
                for (IMediaPlayer.OnPreparedListener onPreparedListener : onPreparedListenerList) {
                    onPreparedListener.onPrepared(iMediaPlayer);
                }
            }
        });

        ijkVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (currentPlayCount >= videoUrlList.size() - 1) {
                    if (restartCount < 1 && isRestartOnComplete) {
                        restartCount++;
                        restart();
                    }
                    for (IMediaPlayer.OnCompletionListener listener : onCompletionListenerList) {
                        listener.onCompletion(iMediaPlayer);
                    }
                } else {
                    currentPlayCount++;
                    if (currentPlayCount > videoUrlList.size() - 1) {
                        currentPlayCount = videoUrlList.size() - 1;
                    }
                    if (currentPlayCount >= 0 && currentPlayCount < videoUrlList.size()) {
                        startPath(videoUrlList.get(currentPlayCount));
                    }
                }
            }
        });
    }

    /**
     * 设置Video横屏显示
     */
    public void setVideoShowLand() {
        isLandscapeVideo = true;
        if (latestVideoView != topScreenVideoView) {
            latestVideoView = topScreenVideoView;
            removeVideoView();
            FrameLayout.LayoutParams paramsTop = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            topScreenVideoView.addView(ijkVideoView, paramsTop);
            topScreenVideoView.setVisibility(VISIBLE);
            ijkVideoView.setVideoAspectRatio(1);
            videoBackgroundView.setVisibility(VISIBLE);
            if (videoPositionListener != null) {//设置监听
                videoPositionListener.onScreenTop();
            }
        }
    }

    /**
     * 给这个播放器设置控制界面
     */
    public void setLiveFullRoomVideoControllerView(ILiveVideoController videoController) {
        if (videoController != null) {
            videoController.setLiveVideoPlayerView(this);
        }
        if (controller != null) {
            controller.setILiveController(videoController);
        }
    }

    public void switchTopVideoScreen() {
        Activity act = (Activity) context;
        switchTopVideoScreen(act);
    }

    public void setVideoYuGaoView(boolean isYuGao, long startTimestamp) {
        long dtime = startTimestamp * 1000 - new Date().getTime();
        if (isYuGao && controller != null) {
            controller.setVideoYuGaoViewVisible(dtime);
        }
    }

    public void switchTopVideoScreen(Activity act) {
        if (getScreenOrientation(act) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            (act).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void topVideoToLandscape() {
        if (ijkVideoView.getParent() == topScreenVideoView) {
            setTopScreenVideoViewLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            if (videoPositionListener != null) {
                videoPositionListener.onSwitchFullScreen();
            }
        }
    }

    private void setTopScreenVideoViewLayoutParams(int heightDp, int marginTopDp) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topScreenVideoView.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    heightDp == ViewGroup.LayoutParams.MATCH_PARENT ?
                            ViewGroup.LayoutParams.MATCH_PARENT :
                            PixelUtil.dp2px(context, heightDp));
        }

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = heightDp == ViewGroup.LayoutParams.MATCH_PARENT ?
                ViewGroup.LayoutParams.MATCH_PARENT :
                PixelUtil.dp2px(context, heightDp);
        params.topMargin = PixelUtil.dp2px(context, marginTopDp);
        topScreenVideoView.setLayoutParams(params);
    }

    private void topVideoBackPortrait() {
        setTopScreenVideoViewLayoutParams(DP_TOP_VIDEO_HEIGHT, DP_TOP_VIDEO_MARGIN);
        if (videoPositionListener != null) {
            videoPositionListener.onScreenTop();
        }
    }

    /**
     * 在activity横竖屏切换的时候调用。主要是设置视频的显示
     *
     * @param newConfig
     */
    public void onConfigChange(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscapeVideo = true;
            topVideoToLandscape();
            setFullScreen(true);
        } else {
            setFullScreen(false);
            isLandscapeVideo = false;
            topVideoBackPortrait();
        }
    }

    private void setFullScreen(boolean fullScreen) {
        if (context != null && context instanceof Activity) {
            WindowManager.LayoutParams attrs = ((Activity) context).getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                ((Activity) context).getWindow().setAttributes(attrs);
                ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) context).getWindow().setAttributes(attrs);
                ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    public int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public int getCurrentPlayCount() {
        return currentPlayCount;
    }

    public ArrayList<String> getVideoUrlList() {
        return videoUrlList;
    }

    public Uri getCurrentPlayingVideoUri() {
        return videouri;
    }

    /**
     * 获取在整个列表的Video的播放时间
     *
     * @return
     */
    public long getCurrentVideoListTime() {
        long time = getCurrentPosition();
        for (int i = 0; i < currentPlayCount; i++) {
            long oldTime = 0;
            if (videoPositionTimeMap.get(i) != null) {
                oldTime = videoPositionTimeMap.get(i);
            }
            time += oldTime;
        }
        return time;
    }

    public void setIsForceUseFullScreenVideo(boolean isForceUseFullScreenVideo) {
        this.isForceUserFullScreenVideo = isForceUseFullScreenVideo;
    }


    private void removeVideoView() {
        ViewGroup viewGroup = (ViewGroup) ijkVideoView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(ijkVideoView);
        }
    }

    private void initSettings() {
        settings.setUsingMediaCodec(true);
    }

    public void setVideoThumbImage(String image) {
        //        if (controller != null) {
        //            controller.setVideoThumbImage(image);
        //        }
        if (TextUtils.isEmpty(image)) {
            bkgImageView.setImageResource(R.drawable.bg_video_no_thumb);
            bkgBlurView.setVisibility(GONE);
        } else {
            bkgBlurView.setVisibility(VISIBLE);
            Glide.with(context)
                    .load(image)
                    .error(R.drawable.glide_default_image)
                    .into(new GlideDrawableImageViewTarget(bkgImageView) {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bkgBlurView.invalidate();
                                }
                            }, 100);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bkgBlurView.invalidate();
                                }
                            }, 100);
                        }
                    });
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

    public int getVideoListDuration() {
        if (videoUrlList.size() > 0) {
            int d = 0;
            for (int i = 0; i < videoUrlList.size(); i++) {
                d += videoPositionTimeMap.get(i) != null ? videoPositionTimeMap.get(i) : 0;
            }
            return d;
        }
        return getCurrentPosition();
    }

    /**
     * seek to video url list time
     *
     * @param mesc
     */
    public void seekVideoListTo(int mesc) {
        int toMesc = -1;
        long time = mesc;
        int seekPlayCount = 0;
        for (int i = 0; i < videoUrlList.size(); i++) {
            long posTime = videoPositionTimeMap.get(i) != null ? videoPositionTimeMap.get(i) : 0;
            if (time < posTime) {
                toMesc = (int) time;
                seekPlayCount = i;
                break;
            } else {
                time = time - posTime;
            }
        }
        if (toMesc != -1) {
            if (seekPlayCount != currentPlayCount) {
                startPath(videoUrlList.get(seekPlayCount));
                currentPlayCount = seekPlayCount;
            }
            seekTo(toMesc);
        }
    }

    /**
     * 设置播放列表的每个URI的播放时长
     * 这个时长必须和设置的列表对应
     *
     * @param urlListDuration
     */
    public void setVideoUrlListDuration(Long... urlListDuration) {
        if (urlListDuration != null && urlListDuration.length > 0) {
            for (int i = 0; i < urlListDuration.length; i++) {
                videoPositionTimeMap.put(i, urlListDuration[i]);
            }
        }
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
        if (videoUrlList != null) {
            videoUrlList.clear();
        } else {
            videoUrlList = new ArrayList<>();
        }
        videoUrlList.add(path);
        currentPlayCount = 0;
        if (isLegalUrlList()) {
            startPath(videoUrlList.get(0));
        }
    }

    public void start(ArrayList<String> pathList) {
        videoUrlList = pathList;
        currentPlayCount = 0;
        if (isLegalUrlList()) {
            startPath(videoUrlList.get(0));
        }
    }

    private void startPath(String path) {
        if (netChecker == null) {
            netChecker = new NetChecker(context, new NetChecker.CheckCallBack() {
                @Override
                public void callBack(boolean isCouldPlay, Object tag) {
                    if (isCouldPlay) {
                        if (tag != null && tag instanceof String) {
                            String path = (String) tag;
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
                    }
                }
            });
        }
        netChecker.checkNet(path);

    }

    private boolean isLegalUrlList() {
        return videoUrlList != null && !videoUrlList.isEmpty();
    }

    public void setIsUseLiveStyle(boolean isLive) {
        this.isLiving = isLive;
        if (controller != null) {
            controller.setIsLiveController(isLive);
        }
    }

    private void setMediaPlayerByPath(String path) {
        settings.setUsingLiveStyle(isLiving);
        if (!TextUtils.isEmpty(path) && path.endsWith(".m3u8")) {
            settings.setPlayer(Settings.PV_PLAYER__IjkExoMediaPlayer);
        } else {
            settings.setPlayer(Settings.PV_PLAYER__IjkKsyMediaPlayer);
        }
    }

    private boolean isIJKMediaPlayer() {
        return settings.getPlayer() == Settings.PV_PLAYER__IjkMediaPlayer;
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
        if (controller != null) {
            controller.stopYuGaoTimer();
            controller.removeNetChangeListener();
        }
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
        if (!isLegalUrlList()) {
            Log.e("TAG", "restart error --------");
            return;
        }
        currentPlayCount = 0;
        if (isIJKMediaPlayer()) {
            removeVideoView();
            addVideoToLatestViewView();
        }
        videouri = Uri.parse(videoUrlList.get(currentPlayCount));
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

    private void addVideoToLatestViewView() {
        if (latestVideoView != null) {
            Log.e("TAG", "count == " + latestVideoView.getChildCount());
            FrameLayout.LayoutParams params = null;
            if (latestVideoView == topScreenVideoView) {
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
            latestVideoView.addView(ijkVideoView, params);
            latestVideoView.setVisibility(VISIBLE);
        }
    }


    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        onCompletionListenerList.clear();
        onCompletionListenerList.add(l);
    }

    public void addOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        onCompletionListenerList.add(l);
    }

    public void removeOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        onCompletionListenerList.remove(l);
    }

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        onPreparedListenerList.clear();
        onPreparedListenerList.add(l);
    }

    public void setOnVideoViewSizeSetListener(IjkVideoView.OnVideoSurfaceViewSizeSetListener l) {
        if (ijkVideoView != null) {
            ijkVideoView.setOnVideoSurfaceViewSizeSetListener(l);
        }
    }

    public void addOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        onPreparedListenerList.add(l);
    }

    public void setRestartOnComplete(boolean restartOnComplete) {
        isRestartOnComplete = restartOnComplete;
    }

    public void setOnVideoPositionListener(OnVideoPositionListener l) {
        this.videoPositionListener = l;
    }

    public interface OnVideoPositionListener {
        void onScreenTop();

        void onFullScreen();

        void onSwitchFullScreen();
    }
}

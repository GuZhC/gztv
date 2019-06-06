package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.videoijkplayer.NetChecker;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import com.dfsx.videoijkplayer.media.Settings;
import com.dfsx.videoijkplayer.util.NetworkChangeManager;
import com.dfsx.videoijkplayer.util.NetworkChangeReceiver;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动直播的播放器（包括直播和回放的播放逻辑）
 * Created by liuwb on 2017/7/6.
 */
public class LiveServiceVideoPlayer extends FrameLayout implements IVideoMultilineVideoPlayerApi,
        NetworkChangeReceiver.OnNetworkChangeListener {

    private Context context;
    private FrameLayout videoContainerView;
    private FrameLayout videoControllerView;
    private LoadingView loadingProgress;
    private TextView videoStateTextView;

    private IjkVideoView ijkVideoView;

    private LiveServiceVideoController controller;

    private List<VideoData> videoPlayDataList;

    private int currentPlayCount;
    private boolean isLiving;

    private Settings settings;

    private VideoData currentPlayingVideo;

    private OnLiveServiceVideoEventClickListener eventClickListener;
    private OnVideoMultilinePlayChangeListener multilinePlayChangeListener;

    private NetChecker netChecker;

    private long allVideoDuration;

    private String videoTitle;

    public LiveServiceVideoPlayer(Context context) {
        this(context, null);
    }

    public LiveServiceVideoPlayer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveServiceVideoPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveServiceVideoPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        settings = new Settings(context.getApplicationContext());
        NetworkChangeManager.getInstance().addOnNetworkChangeListener(this);
        initSettings();
        LayoutInflater.from(context)
                .inflate(R.layout.live_service_video_layout, this);
        videoContainerView = (FrameLayout) findViewById(R.id.video_container_view);
        videoControllerView = (FrameLayout) findViewById(R.id.video_controller_view);
        loadingProgress = (LoadingView) findViewById(R.id.loading);
        videoStateTextView = (TextView) findViewById(R.id.video_state_text);

        ijkVideoView = new IjkVideoView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ijkVideoView.setLayoutParams(params);
        videoContainerView.addView(ijkVideoView);

        videoControllerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        videoControllerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (controller != null) {
                        if (controller.isShowing()) {
                            controller.hide();
                        } else {
                            controller.show();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        ijkVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                controller.start();
                hideVideoStateText();
                if (!isLiving && videoPlayDataList != null && videoPlayDataList.size() > 1) {
                    //has more video data, update videoData duration
                    if (currentPlayCount >= 0 && currentPlayCount < videoPlayDataList.size()) {
                        long duration = iMediaPlayer.getDuration();
                        videoPlayDataList.get(currentPlayCount).duration = duration;
                    }
                }
            }
        });
        ijkVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (isLiving && currentPlayingVideo != null) {
                    startPlayVideo(currentPlayingVideo);
                } else {
                    showVideoStateText("解码播放失败");
                }
                return true;
            }
        });

        ijkVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (isLiving) {
                    if (currentPlayingVideo != null) {
                        startPlayVideo(currentPlayingVideo);
                    } else {
                        showVideoStateText("直播视频播放失败");
                    }
                } else {
                    if (currentPlayCount < videoPlayDataList.size() - 1) {
                        currentPlayCount++;
                        startPlayVideo(videoPlayDataList.get(currentPlayCount));
                    } else {
                        showVideoStateText("播放结束");
                    }
                }
            }
        });

        ijkVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        startLoading();
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        hideLoading();
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        hideLoading();
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        hideLoading();
                        break;
                }
                return false;
            }
        });
    }

    private void initSettings() {
        settings.setUsingMediaCodec(true);
    }

    public void startLoading() {
        loadingProgress.setVisibility(View.VISIBLE);
    }

    public void showVideoStateText(String text) {
        hideLoading();
        videoStateTextView.setVisibility(VISIBLE);
        videoStateTextView.setText(text);
    }

    public void hideVideoStateText() {
        videoStateTextView.setVisibility(GONE);
    }

    public void hideLoading() {
        loadingProgress.setVisibility(View.GONE);
    }

    public void setMultilineClickable(boolean isCouldClick) {
        if (controller != null) {
            controller.setMultilineClickable(isCouldClick);
        }
    }

    /**
     * 最好不要重复调用
     *
     * @param isHasTimeLabel
     */
    public void setVideoController(boolean isHasTimeLabel) {
        if (controller == null) {
            controller = isHasTimeLabel ?
                    new LiveServiceVideoTimeBarController(context, this, videoControllerView)
                    :
                    new LiveServiceVideoController(context, this, videoControllerView);
        } else {
            boolean isChange = false;

            if (isHasTimeLabel && controller instanceof LiveServiceVideoTimeBarController) {
                isChange = false;
            } else if (!isHasTimeLabel && controller instanceof LiveServiceVideoController) {
                isChange = false;
            } else {
                isChange = true;
            }
            if (isChange) {
                controller = isHasTimeLabel ?
                        new LiveServiceVideoTimeBarController(context, this, videoControllerView)
                        :
                        new LiveServiceVideoController(context, this, videoControllerView);
            }
        }
        //初始化已经设置过的监听器,回调
        if (eventClickListener != null) {
            controller.setOnLiveServiceVideoEventClickListener(eventClickListener);
        }
        if (multilinePlayChangeListener != null) {
            controller.setOnVideoMultilinePlayChangeListener(multilinePlayChangeListener);
        }
        if (!TextUtils.isEmpty(videoTitle)) {
            controller.setVideoTitle(videoTitle);
        }
        ijkVideoView.setMediaController(controller);
    }

    /**
     * 设置线路切换的回调
     *
     * @param l
     */
    public void setOnVideoMultilinePlayChangeListener(OnVideoMultilinePlayChangeListener l) {
        this.multilinePlayChangeListener = l;
        if (controller != null) {
            controller.setOnVideoMultilinePlayChangeListener(l);
        }
    }

    public void setVideoTitle(String title) {
        this.videoTitle = title;
        if (controller != null) {
            controller.setVideoTitle(title);
        }
    }

    public void start(IMultilineVideo multilineVideo) {
        List<String> pathList = multilineVideo.getVideoUrlList();
        List<Long> durationList = multilineVideo.getVideoDurationList();
        boolean isHasDurationData = durationList != null && durationList.size() ==
                pathList.size();
        ArrayList<VideoData> tempList = new ArrayList<>();
        for (int i = 0; i < pathList.size(); i++) {
            String path = pathList.get(i);
            VideoData data = new VideoData(path);
            if (isHasDurationData) {
                data.duration = durationList.get(i);
            }
            tempList.add(data);
        }
        start(tempList);
        controller.selectedMultiline(multilineVideo);
    }

    public void start(String path) {
        if (videoPlayDataList != null) {
            videoPlayDataList.clear();
        } else {
            videoPlayDataList = new ArrayList<>();
        }
        videoPlayDataList.add(new VideoData(path));
        currentPlayCount = 0;
        if (isLegalUrlList()) {
            startPlayVideo(videoPlayDataList.get(0));
        }
    }

    public void start(List<VideoData> dataList) {
        videoPlayDataList = dataList;
        currentPlayCount = 0;
        if (isLegalUrlList()) {
            startPlayVideo(videoPlayDataList.get(0));
        }
    }

    private void startPlayVideo(VideoData path) {
        if (netChecker == null) {
            netChecker = new NetChecker(context, new NetChecker.CheckCallBack() {
                @Override
                public void callBack(boolean isCouldPlay, Object tag) {
                    if (isCouldPlay) {
                        if (tag != null && tag instanceof VideoData) {
                            VideoData videoData = (VideoData) tag;
                            if (videoData.isLegale()) {
                                currentPlayingVideo = videoData;
                                startLoading();
                                Uri pathUri = Uri.parse(videoData.videoUrl);
                                setMediaPlayerByPath(videoData.videoUrl);
                                if (!ijkVideoView.isPlaying()) {
                                    ijkVideoView.setVideoURI(pathUri);
                                    ijkVideoView.start();
                                } else {
                                    ijkVideoView.stopPlayback();
                                    ijkVideoView.setVideoURI(pathUri);
                                    ijkVideoView.start();
                                }
                            }
                        } else if (isInPlayBackStatus()) {
                            start();
                        }
                    }
                }
            });
        }
        netChecker.checkNet(path);
    }

    private void setMediaPlayerByPath(String path) {
        settings.setUsingLiveStyle(isLiving);
        if (!TextUtils.isEmpty(path) && path.endsWith(".m3u8")) {
            settings.setPlayer(Settings.PV_PLAYER__IjkExoMediaPlayer);
        } else {
            settings.setPlayer(Settings.PV_PLAYER__IjkKsyMediaPlayer);
        }
    }

    private boolean isLegalUrlList() {
        return videoPlayDataList != null && !videoPlayDataList.isEmpty();
    }

    @Override
    public boolean isPlaying() {
        return ijkVideoView.isPlaying();
    }

    @Override
    public void switchScreen(boolean isFull) {
        switchScreen();
    }

    @Override
    public boolean isInPlayBackStatus() {
        return ijkVideoView.isInPlaybackState();
    }

    @Override
    public long getCurrentPosition() {
        long realPos = ijkVideoView.getCurrentPosition();
        long cTime = realPos;
        if (videoPlayDataList != null && videoPlayDataList.size() > 1) {
            for (int i = 0; i < currentPlayCount; i++) {
                VideoData data = videoPlayDataList.get(i);
                cTime += data.duration;
            }
        }
        return cTime;
    }

    @Override
    public long getDuration() {
        if (videoPlayDataList != null && videoPlayDataList.size() > 1) {
            if (allVideoDuration != 0) {
                return allVideoDuration;
            }
            long allDuration = 0;
            for (VideoData data : videoPlayDataList) {
                allDuration += data.duration;
            }
            if (allDuration != 0) {
                allVideoDuration = allDuration;
                return allDuration;
            }
        }
        return ijkVideoView.getDuration();
    }

    @Override
    public int getBufferPercentage() {
        int videoPercent = ijkVideoView.getBufferPercentage();
        //        if (videoPlayDataList != null && videoPlayDataList.size() > 1) {
        //            long time = 0;
        //            for (int i = 0; i < currentPlayCount; i++) {
        //                time += videoPlayDataList.get(i).duration;
        //            }
        //            float allTime = getDuration();
        //            float percentTime = (videoPercent / 100f) *
        //                    videoPlayDataList.get(currentPlayCount).duration + time;
        //            float percent = percentTime / allTime;
        //            Log.e("TAG", "percentTime == " + percentTime);
        //            videoPercent = (int) (percent * 100);
        //        }
        return videoPercent;
    }

    @Override
    public void seekTo(int time) {
        int realVideoTime = time;
        if (videoPlayDataList != null && videoPlayDataList.size() > 1) {
            int toPlayCount = 0;
            long indexTime = 0;
            for (int i = 0; i < videoPlayDataList.size(); i++) {
                VideoData data = videoPlayDataList.get(i);
                indexTime += data.duration;
                if (realVideoTime < indexTime) {
                    toPlayCount = i;
                    realVideoTime -= indexTime - data.duration;
                    break;
                }
            }
            if (currentPlayCount != toPlayCount) {
                currentPlayCount = toPlayCount;
                startPlayVideo(videoPlayDataList.get(toPlayCount));
            }
        }
        ijkVideoView.seekTo(realVideoTime);
    }

    @Override
    public void start() {
        if (!isPlaying()) {
            ijkVideoView.start();
        }
        if (controller != null) {
            controller.start();
        }
    }

    @Override
    public void restart() {
        if (!isLegalUrlList()) {
            Log.e("TAG", "restart error --------");
            return;
        }
        currentPlayCount = 0;
        if (isIJKMediaPlayer()) {
            videoContainerView.removeAllViews();
            videoContainerView.addView(ijkVideoView);
        }
        currentPlayingVideo = videoPlayDataList.get(currentPlayCount);
        if (currentPlayingVideo == null || !currentPlayingVideo.isLegale()) {
            currentPlayingVideo = null;
            return;
        }
        Uri playUri = Uri.parse(currentPlayingVideo.videoUrl);
        if (!ijkVideoView.isPlaying()) {
            ijkVideoView.setVideoURI(playUri);
            ijkVideoView.start();
        } else {
            ijkVideoView.stopPlayback();
            ijkVideoView.setVideoURI(playUri);
            ijkVideoView.start();
        }
        controller.restart();
    }

    private boolean isIJKMediaPlayer() {
        return settings.getPlayer() == Settings.PV_PLAYER__IjkMediaPlayer;
    }

    @Override
    public void pause() {
        ijkVideoView.pause();
        if (controller != null) {
            controller.pause();
        }
    }

    @Override
    public void stop() {
        ijkVideoView.stopPlayback();
        if (controller != null) {
            controller.stop();
        }
        showVideoStateText("直播已停止");
    }

    @Override
    public void release() {
        ijkVideoView.release(true);
        if (controller != null) {
            controller.release();
        }
        showVideoStateText("直播已停止");
        NetworkChangeManager.getInstance().removeOnNetworkChangeListener(this);
    }

    @Override
    public void selectedMultiline(IMultilineVideo multilineVideo) {
        start(multilineVideo);
    }

    @Override
    public void setMultilineList(List<IMultilineVideo> multilineVideoList) {
        if (controller != null) {
            controller.setMultilineList(multilineVideoList);
        }
    }

    public List<IMultilineVideo> getMultilineVideoList() {
        return controller.getMultilineList();
    }

    public void updateMultilineView() {
        controller.updateMultilineView();
    }

    public void switchScreen() {
        if (context instanceof Activity) {
            Activity act = (Activity) context;
            if (getScreenOrientation(act) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                controller.switchScreen(false);
            } else {
                (act).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                controller.switchScreen(true);
            }
        }
    }

    public void setOnLiveServiceVideoEventClickListener(OnLiveServiceVideoEventClickListener l) {
        this.eventClickListener = l;
        if (controller != null) {
            controller.setOnLiveServiceVideoEventClickListener(l);
        }
    }

    public boolean isFullScreen() {
        return getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    /**
     * 在activity横竖屏切换的时候调用。主要是设置视频的显示
     *
     * @param newConfig
     */
    public void onActivityConfigChange(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullScreen(true);
        } else {
            setFullScreen(false);
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

    public void setLiving(boolean living) {
        isLiving = living;
    }

    /**
     * 是因为网络的原因停止播放
     */
    private boolean isNetworkStopPlay;

    @Override
    public void onChange(int networkType) {
        if (networkType == NetworkUtil.TYPE_NOT_CONNECTED) {
            if (isLiving) {
                stop();
                isNetworkStopPlay = true;
                showVideoStateText("网络连接已断开");
            } else {
                Toast.makeText(context, "网络连接已断开", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (networkType == NetworkUtil.TYPE_MOBILE) {
                if (netChecker != null) {
                    if (isPlaying()) {
                        pause();
                    }
                    netChecker.checkNet(null);
                }
            } else {
                if (isNetworkStopPlay) {
                    if (isInPlayBackStatus()) {
                        start();
                    } else {
                        restart();
                    }
                }
            }
            isNetworkStopPlay = false;
        }

    }

    class VideoData {
        public long id;
        public long duration;
        public String videoUrl;

        public VideoData() {

        }

        public VideoData(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public VideoData(long id, long duration, String videoUrl) {
            this.id = id;
            this.duration = duration;
            this.videoUrl = videoUrl;
        }

        public boolean isLegale() {
            return !TextUtils.isEmpty(videoUrl);
        }
    }
}

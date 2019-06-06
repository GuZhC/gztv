package com.dfsx.lzcms.liveroom.view.adwareVIew;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dfsx.core.common.Util.Util;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.videoijkplayer.NetChecker;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import com.dfsx.videoijkplayer.media.MediaItem;
import com.dfsx.videoijkplayer.media.Settings;
import com.dfsx.videoijkplayer.util.NetworkChangeManager;
import com.dfsx.videoijkplayer.util.NetworkChangeReceiver;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import com.ksyun.media.player.KSYMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.List;

/**
 * Description 播放view   heyang
 */
public class VideoAdwarePlayView extends RelativeLayout implements MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, NetworkChangeReceiver.OnNetworkChangeListener {

    private final static String TAG = "TAG";

    private CustomMediaNewContoller mediaController;
    private View player_btn, view;
    private IjkVideoView mVideoView;
    private boolean isPause;
    private View rView, bottomSharetBtn;
    private Context mContext;
    private boolean portrait;
    private Settings settings;
    private boolean isLive;
    private boolean isUseHardCodec = false;
    //    private OrientationEventListener orientationEventListener;
    private NetChecker netChecker;
    private List<MediaItem> videoPlayDataList;
    private int currentPlayCount;
    private boolean isPlayAdware = false;
    private CountDownTimer mTimer, mTimerSkip;
    private int durtaion = 5, skinTime;   // 播放时长
    private ImageView filmImage, scriptImage;
    private TextView topadTimeTx;
    private MediaItem scriptPath, filmParh;
    private boolean _isCanJump = true, isAdOver = true;
    private boolean _isLive = false;

    private NetChecker.CheckCallBack _checkCallBack;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (isPlayAdware) {
                    if (skinTime > 0) {
                        int len = --skinTime;
                        createAdwareTime(len);
                        Log.e(TAG, "skinTime====" + skinTime);
                    } else {
//                        if (timerTaskSkip != null) {
//                            timerTaskSkip.cancel();
//                            timerTaskSkip = null;
//                        }
                        isAdOver = true;
                        Log.e(TAG, "skinTime====" + skinTime);
                        createAdwareTime(0);
                    }
                }
            }
            if (msg.what == 2) {
                if (isPlayAdware) {
                    if (durtaion > 0) {
                        --durtaion;
                        Log.e(TAG, "duration====" + durtaion);
                    } else {
//                        if (timerTask != null) {
//                            timerTask.cancel();
//                            timerTask = null;
//                        }
                        Log.e(TAG, "duration====" + durtaion);
                        filmImage.setVisibility(View.GONE);
                        playNext(false);
                    }
                }
            }
        }
    };


    public void createAdwareTime(int len) {
        String strText = "关闭广告";
        int index = 0;
        if (len > 0) {
            strText = durtionoString(len) + "\b后关闭广告";
            index = strText.indexOf("\b");
        }
        SpannableString sb = new SpannableString(strText);
        if (index > 0) {
            sb.setSpan(new AbsoluteSizeSpan(sp2px(mContext, 14)), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new AbsoluteSizeSpan(sp2px(mContext, 12)), index, strText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffffff")), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#80ffffff")), index, strText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            index = strText.length();
            sb.setSpan(new AbsoluteSizeSpan(sp2px(mContext, 12)), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#80ffffff")), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        topadTimeTx.setText(sb);
    }

    private int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public VideoAdwarePlayView(Context context) {
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

        rView = LayoutInflater.from(mContext).inflate(R.layout.view_video_ad, this, true);
        view = findViewById(R.id.media_contoller);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        //视频大小测量模式
        mVideoView.setVideoAspectRatio(3);
        mediaController = new CustomMediaNewContoller(mContext, rView, new CustomMediaNewContoller.OnPauseImagListener() {
            @Override
            public void onClick(MediaItem mediaItem, int adType) {
                if (!(mediaItem == null || TextUtils.isEmpty(mediaItem.getLinkUrl()))) {
                    if (onAdwareListener != null)
                        onAdwareListener.onClick(mediaItem, adType);
                }
            }
        });
        mVideoView.setMediaController(mediaController);
        topadTimeTx = (TextView) findViewById(R.id.top_ad_time_txt);
//        pauseAdImage = (ImageView) findViewById(R.id.pause_image);
        rView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay()) {
                    if (isPlayAdware) {
                        mediaController.setShowContoller(false);
//                    mediaController.showBottomControl(false);
                        if (!(videoPlayDataList == null || currentPlayCount >= videoPlayDataList.size())) {
                            MediaItem item = videoPlayDataList.get(currentPlayCount);
                            if (!(item == null || TextUtils.isEmpty(item.getLinkUrl()))) {
                                if (onAdwareListener != null)
                                    onAdwareListener.onClick(item, 1);
                            }
                        }
                    } else {
//                        if (onVideoClickListener != null) {
//                            //针对列表行 点击视频  跳转广告链接地址
//                            mediaController.setShowContoller(false);
//                            onVideoClickListener.onClick();
//                        }
                    }
                }
                if (onVideoClickListener != null) {
                    //针对列表行 点击视频  跳转广告链接地址
                    mediaController.setShowContoller(false);
                    onVideoClickListener.onClick();
                }
            }
        });
        scriptImage = (ImageView) findViewById(R.id.script_image);
        scriptImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(scriptPath == null || TextUtils.isEmpty(scriptPath.getLinkUrl()))) {
                    if (onAdwareListener != null)
                        onAdwareListener.onClick(scriptPath, 3);
                }
            }
        });
        filmImage = (ImageView) findViewById(R.id.film_image);
        filmImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(filmParh == null || TextUtils.isEmpty(filmParh.getLinkUrl()))) {
                    if (onAdwareListener != null)
                        onAdwareListener.onClick(filmParh, 1);
                }
            }
        });
        bottomSharetBtn = (View) findViewById(R.id.bottom_share_btn);
        bottomSharetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBottomShareListener != null)
                    onBottomShareListener.onShareBtn();
            }
        });

        topadTimeTx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_isCanJump && isAdOver) {
                    isPlayAdware = false;
                    filmImage.setVisibility(GONE);
                    topadTimeTx.setVisibility(GONE);
                    if (currentPlayCount == videoPlayDataList.size() - 1) {
                        clear();
                        endVew(null);
                        return;
                    }
                    playNext(true);
                }
            }
        });

        //heyang  2017/12/12  加载完监听
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                if (preparedListener != null)
                    preparedListener.preparedlistener(iMediaPlayer);
                if (!isPlayAdware) {
                    mediaController.setShowContoller(true);
//                    mediaController.showBottomControl(true);
                    loadScriptImge();
                }
                if (!(videoPlayDataList == null || currentPlayCount >= videoPlayDataList.size())) {
                    MediaItem item = videoPlayDataList.get(currentPlayCount);
                    if (item != null) {
                        if (!item.isAdware()) {
                            stopTimer();
                        } else {
                            showTimeCountView(true);
                        }
                    }
                }
            }
        });

        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                if (!(videoPlayDataList == null || videoPlayDataList.isEmpty())) {
                    if (currentPlayCount >= videoPlayDataList.size() - 1) {
                        clear();
                        endVew(mp);
                    }
                } else {
                    view.setVisibility(View.GONE);
                    endVew(mp);
                }

//                if (!(videoPlayDataList == null || currentPlayCount >= videoPlayDataList.size())) {
////                    MediaItem item = videoPlayDataList.get(currentPlayCount);
////                    if (item != null) {
////                        if (item.isAdware() && currentPlayCount == videoPlayDataList.size() - 2)
////                            stopTimer();
////                    }
//                    // playNext(false);
//                } else {
//                    if (!(videoPlayDataList == null || videoPlayDataList.isEmpty())) {
//                        if (currentPlayCount >= videoPlayDataList.size() - 1) {
//                            clear();
//                            endVew(mp);
//                        }
//                    } else {
//                        view.setVisibility(View.GONE);
//                        endVew(mp);
//                    }
//                }
            }
        });

        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                Toast.makeText(mContext, "视频播放失败", Toast.LENGTH_SHORT).show();
                if (mediaController != null) {
                    mediaController.onPlayError();
                }
                clear();
                if (errorListener != null)
                    errorListener.errorListener(iMediaPlayer);
                return true;
            }
        });
    }

    public void endVew(IMediaPlayer mp) {
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

    public void playNext(boolean isCanJump) {
        if (videoPlayDataList == null) return;
        if (currentPlayCount == videoPlayDataList.size() - 1) {
            endVew(null);  // 针对开机视频，时间到关闭广告
            return;
        }
        MediaItem mediaItem = null;
        currentPlayCount++;
        if (currentPlayCount >= videoPlayDataList.size()) {
            return;
        }
        if (isCanJump) {
            do {
                mediaItem = videoPlayDataList.get(currentPlayCount);
                Log.e(TAG, "mediaItem====" + mediaItem.getUrl() + ",currentPlayCount==" + currentPlayCount);
                if (mediaItem != null) {
                    if (!mediaItem.isAdware()) {
                        break;
                    }
                }
                currentPlayCount++;
            } while (!(videoPlayDataList == null || currentPlayCount >= videoPlayDataList.size()));
        } else {
            if (currentPlayCount >= videoPlayDataList.size())
                currentPlayCount = videoPlayDataList.size() - 1;
            mediaItem = videoPlayDataList.get(currentPlayCount);
            Log.e(TAG, "mediaItem====" + mediaItem.getUrl() + ",currentPlayCount==" + currentPlayCount);
        }
        if (mediaItem != null) {
            if (mediaItem.isAdware()) {
                isPlayAdware = true;
                durtaion = mediaItem.getDuration();
                if (mediaItem.getType() == 2) {
                    filmParh = mediaItem;
                    loadFilmImge();
                    showTimeCountView(false);
                } else {
                    //如果是视频加载时间 停止倒计时
                    if (mTimerSkip != null) {
                        mTimerSkip.cancel();
                        mTimerSkip = null;
                    }
                    start(mediaItem.getUrl(), false, _checkCallBack);
                }
            } else {
                topadTimeTx.setVisibility(GONE);
                isPlayAdware = false;
                start(mediaItem.getUrl(), _isLive, _checkCallBack);
            }
        }
    }

    public void clear() {
        if (mediaController != null)
            mediaController.setPaseuAdPath(null);
        scriptPath = null;
        filmParh = null;
//        _isCanJump = false;
        isAdOver = true;
        topadTimeTx.setVisibility(GONE);
        scriptImage.setVisibility(View.GONE);
        filmImage.setVisibility(View.GONE);
    }

    public void showShareButton(boolean isShow, OnBottomShareListener onBottomShareListener) {
        this.onBottomShareListener = onBottomShareListener;
        if (isShow) {
            bottomSharetBtn.setVisibility(VISIBLE);
        } else {
            bottomSharetBtn.setVisibility(GONE);
        }
    }

    public int getVideoCurrentPostion() {
        return mVideoView.getCurrentPosition();
    }

    public int getVideoDuration() {
        return mVideoView.getDuration();
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

    public void setPauseImagePath(MediaItem pauseFile) {
        mediaController.setPaseuAdPath(pauseFile);
    }

    public void loadScriptImge() {
        if (scriptPath == null) return;
        if (!TextUtils.isEmpty(scriptPath.getUrl())) {
            scriptImage.setVisibility(View.VISIBLE);
            Util.LoadThumebImage(scriptImage, scriptPath.getUrl(), null);
//            Glide
//                    .with(mContext)
//                    .load(scriptPath.getUrl())
//                    .asBitmap()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
//                    .error(R.drawable.glide_default_image)
//                    .placeholder(R.color.transparent)
//                    .into(scriptImage);
        } else {
            scriptImage.setVisibility(View.GONE);
        }
    }

    public void showTimeCountView(boolean isRestart) {
        if (!isPlayAdware) return;
//        setShowContoller(false);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new CountDownTimer(durtaion * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                durtaion--;
            }

            @Override
            public void onFinish() {
                if (isPlayAdware) {
                    filmImage.setVisibility(View.GONE);
                    playNext(false);
                }
            }
        }.start();
        if (currentPlayCount == 0 || isRestart) {
            if (_isCanJump)
                topadTimeTx.setVisibility(VISIBLE);
            if (mTimerSkip != null) {
                mTimerSkip.cancel();
                mTimerSkip = null;
            }
            topadTimeTx.setVisibility(VISIBLE);
            mTimerSkip = new CountDownTimer(skinTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    skinTime--;
                    createAdwareTime((int) (millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    if (isPlayAdware) {
                        isAdOver = true;
                        createAdwareTime(0);
                    }
//                    if (!_isCanJump) {
//                        topadTimeTx.setVisibility(GONE);
//                    } else {
//                        topadTimeTx.setVisibility(VISIBLE);
//                    }
                }
            }.start();
        }
    }

    public void loadFilmImge() {
        if (filmParh == null) return;
        if (!TextUtils.isEmpty(filmParh.getUrl())) {
            filmImage.setVisibility(View.VISIBLE);
//            Util.LoadThumebImage(filmImage, filmParh.getUrl(), null);
            if (!(filmParh == null || TextUtils.isEmpty(filmParh.getUrl()))) {
                if (Util.isGif(filmParh.getUrl())) {
                    Glide
                            .with(mContext)
                            .load(filmParh.getUrl())
                            .asGif()
                            .error(R.drawable.glide_default_image)
                            .placeholder(R.color.transparent)
                            .into(filmImage);
                } else {
                    Glide
                            .with(mContext)
                            .load(filmParh.getUrl())
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
                            .error(R.drawable.glide_default_image)
                            .placeholder(R.color.transparent)
                            .into(filmImage);
                }
            }
        } else {
            filmImage.setVisibility(View.GONE);
        }
    }

    public void setScriptImagePath(MediaItem scriptFile) {
        //  scriptPath = scriptFile;
    }

    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    private void onSavePlayStatus() {
        clearVideoPlayStatus();
        saveVideoPlayStatus(durtaion, skinTime);
        stopTimer();
    }

    private void onStartPlayStatus() {
        if (isPlayAdware) {
            int arr[] = getResumePlayerStatus();
            //保存计时
            durtaion = arr[0];
            skinTime = arr[1];
            showTimeCountView(true);
        }
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            if (isPlayAdware) {
                //保存计时
                clearVideoPlayStatus();
                saveVideoPlayStatus(durtaion, skinTime);
                stopTimer();
            }
        } else {
            mVideoView.start();
        }
    }

    private boolean isLegalUrlList() {
        return videoPlayDataList != null && !videoPlayDataList.isEmpty();
    }

    public void onStartInit() {
        mVideoView.stopBackgroundPlay();
        mVideoView.stopPlayback();
        mVideoView.setVideoURI(null);
        currentPlayCount = 0;
        isPlayAdware = false;
        _isCanJump = true;
        isAdOver = true;
        skinTime = 0;
        durtaion = 0;
        filmParh = null;
        scriptPath = null;
        onVideoClickListener = null;
        topadTimeTx.setVisibility(GONE);
        scriptImage.setVisibility(GONE);
        filmImage.setVisibility(GONE);
        bottomSharetBtn.setVisibility(GONE);
    }

    public void start(String path) {
        start(path, false, null);
    }

    public void start(List<MediaItem> dataList, boolean issLIve, NetChecker.CheckCallBack checker) {
        onStartInit();
        _isLive = issLIve;
        videoPlayDataList = dataList;
        _checkCallBack = checker;
        if (isLegalUrlList()) {
            /**
             *  片头也可能是图片
             */
            MediaItem adware = videoPlayDataList.get(currentPlayCount);
            if (adware != null) {
                if (adware.isAdware()) {
                    if (!TextUtils.isEmpty(adware.getUrl())) {
                        durtaion = adware.getDuration();
                        isPlayAdware = true;
                        mediaController.setShowContoller(false);
                        skinTime = adware.getSkinTime();
                        int adConut = 0;
                        if (!(videoPlayDataList == null || videoPlayDataList.isEmpty())) {
                            for (int i = 0; i < videoPlayDataList.size(); i++) {
                                MediaItem it = videoPlayDataList.get(i);
                                if (it == null) continue;
//                                if (it.getType() == 1)
//                                adConut += it.getVideoDurtaion();
                                adConut += it.getDuration();
                            }
                        }
                        if (skinTime == 0) {
                            isAdOver = true;
                        } else if (skinTime < 0) {
                            _isCanJump = false;
                            skinTime = adConut;
                        }
                        if (skinTime > 0) {
                            isAdOver = false;
                            skinTime = skinTime > adConut ? Math.min(skinTime, adConut) : skinTime;
                            skinTime++;
                        }
                        if (adware.getType() == 1) {
                            start(adware.getUrl(), false, _checkCallBack);
                        } else {
                            //图片
                            filmParh = adware;
                            loadFilmImge();
                            showTimeCountView(false);
                        }
                    }
                } else {
                    start(videoPlayDataList.get(currentPlayCount).getUrl(), _isLive, _checkCallBack);
                }
            }
        }
    }

    //heyang  2018-7-6 监听网络对话框  回调
    public void start(String path, boolean isLive, NetChecker.CheckCallBack callBack) {
        this.isLive = isLive;
//        mediaController.setShowContoller(true);
        if (mediaController != null) {
            mediaController.setLive(isLive);
        }
        if (netChecker == null) {
            netChecker = new NetChecker(mContext, new PlayCallBack(callBack));
        }
        netChecker.checkNet(path);
    }

    //heyang  2018-7-6 监听网络对话框  回调
    public void startplay(String path, boolean isLive, NetChecker.CheckCallBack callBack) {
        onStartInit();
        if (mediaController != null)
            mediaController.setPaseuAdPath(null);
        this.isLive = isLive;
        mediaController.setShowContoller(true);
        if (mediaController != null) {
            mediaController.setLive(isLive);
        }
        if (netChecker == null) {
            netChecker = new NetChecker(mContext, new PlayCallBack(callBack));
        }
        netChecker.checkNet(path);
    }

    protected class PlayCallBack implements NetChecker.CheckCallBack {
        private NetChecker.CheckCallBack callBack;

        public PlayCallBack(NetChecker.CheckCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public void callBack(boolean isCouldPlay, Object tag) {
            if (callBack != null) {
                callBack.callBack(isCouldPlay, tag);
            }
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
    }

    private void startPlayPath(String path) {
        setMediaPlayerByPath(path);
        Uri uri = Uri.parse(path);
        if (mediaController != null)
            mediaController.start();
        if (!mVideoView.isPlaying()) {
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
        if (mediaController != null)
            mediaController.setPauseImageHide();
        if (isPlayAdware) {
            int arr[] = getResumePlayerStatus();
            //保存计时
            skinTime = arr[1];
            durtaion = arr[0];
            showTimeCountView(true);
        }
    }

    public void hidePauseImge() {
        if (mediaController != null)
            mediaController.setPauseImageHide();
    }

    private void clearVideoPlayStatus() {
        String key = "VIdeoware_play_" + getClass().getName();
        SharedPreferences sp = mContext.getSharedPreferences(key, 0);
        sp.edit().clear().commit();
    }

    private void saveVideoPlayStatus(int durtaion, int skipdur) {
        String key = "VIdeoware_play_" + getClass().getName();
        String dur = getClass().getName() + "_duration";
        String skip = getClass().getName() + "_skip";
        SharedPreferences sp = mContext.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(dur, durtaion);
        editor.putInt(skip, skipdur);
        editor.commit();
    }

    private int[] getResumePlayerStatus() {
        String key = "VIdeoware_play_" + getClass().getName();
        SharedPreferences sp = mContext.getSharedPreferences(key, 0);
        String dur = getClass().getName() + "_duration";
        String skip = getClass().getName() + "_skip";
        int a[] = new int[2];
        a[0] = sp.getInt(dur, 0);
        a[1] = sp.getInt(skip, 0);
        sp.edit().clear().commit();
        return a;
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
        stopTimer();
    }

    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //        orientationEventListener.disable();
        stopTimer();
        clear();
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

    public void setOnBottomShareListener(OnBottomShareListener onBottomShareListener) {
        this.onBottomShareListener = onBottomShareListener;
    }

    private OnBottomShareListener onBottomShareListener;

    public void setOnTimeCounDwonlister(OnTimeCountDownListener onTimeCounDwonlister) {
        this.onTimeCounDwonlister = onTimeCounDwonlister;
    }

    private OnTimeCountDownListener onTimeCounDwonlister;

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public void setOnAdwareListener(OnAdwareListener onAdwareListener) {
        this.onAdwareListener = onAdwareListener;
    }

    private OnAdwareListener onAdwareListener;

    public void setOnVideoClickListener(VideoAdwarePlayView.OnVideoClickListener onVideoClickListener) {
        this.onVideoClickListener = onVideoClickListener;
    }

    private OnVideoClickListener onVideoClickListener;

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

    public interface OnBottomShareListener {
        void onShareBtn();
    }

    public interface OnTimeCountDownListener {
        void onTimeCounDown();
    }

    public String durtionoString(int coins) {
        String coinsDescribeText;
        if (coins >= 10) {
            coinsDescribeText = String.format("%d", coins) + "秒";
        } else {
            coinsDescribeText = String.format("%2d", coins) + "秒";
        }
        return coinsDescribeText;
    }

    public void stopTimer() {
        topadTimeTx.setVisibility(GONE);
//        if (timerTask != null) {
//            timerTask.cancel();
//            timerTask = null;
//        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
//        if (timerTaskSkip != null) {
//            timerTaskSkip.cancel();
//            timerTaskSkip = null;
//        }
        if (mTimerSkip != null) {
            mTimerSkip.cancel();
            mTimerSkip = null;
        }
    }

    public interface OnAdwareListener {
        public void onClick(MediaItem mediaItem, int adType);
    }

    public interface OnVideoClickListener {
        public void onClick();
    }


}

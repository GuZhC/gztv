package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.videoijkplayer.NetChecker;
import com.dfsx.videoijkplayer.util.NetworkChangeManager;
import com.dfsx.videoijkplayer.util.NetworkChangeReceiver;
import com.dfsx.videoijkplayer.util.NetworkUtil;
import com.dfsx.videoijkplayer.media.IMediaController;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import com.fivehundredpx.android.blur.BlurringView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;

/**
 * Created by liuwb on 2016/8/16.
 */
public class LiveMediaController implements IMediaController, NetworkChangeReceiver.OnNetworkChangeListener {

    protected Context context;
    protected View controllerView;

    private LoadingView loading;
    private TextView centerText;
    private View centerView;

    private ImageView videoThumb;
    private BlurringView thumbImageBlurView;

    protected IjkVideoView player;
    protected IVideoPlayer playerView;

    private String videoThumbImage;

    private FrameLayout videoControllerLayout;

    private ILiveVideoController liveVideoController;
    private boolean isLive = true;

    private boolean isNetNotConnected = false;

    private View yuGaoDownTimeView;
    private TextView yuGaoDownTimeTextView;

    private YuGaoTimeDown yuGaoCountDownTimer;

    //    protected IMediaPlayer.OnPreparedListener onPreparedListener;

    private NetChecker netChecker;

    private ArrayList<IMediaPlayer.OnPreparedListener> onPreparedListenerList = new ArrayList<>();


    public LiveMediaController(Context context, ViewGroup containerView) {
        this.context = context;
        NetworkChangeManager.getInstance().addOnNetworkChangeListener(this);
        controllerView = LayoutInflater.from(context).
                inflate(R.layout.live_video_control, null);
        containerView.addView(controllerView);
        initView();
    }

    protected void initView() {
        videoThumb = (ImageView) findViewById(R.id.video_img);
        thumbImageBlurView = (BlurringView) findViewById(R.id.video_img_blur);
        centerView = findViewById(R.id.center_view);
        loading = (LoadingView) findViewById(R.id.loading);
        centerText = (TextView) findViewById(R.id.center_txt);
        yuGaoDownTimeView = findViewById(R.id.yugao_down_time_view);
        yuGaoDownTimeTextView = (TextView) findViewById(R.id.text_down_time);
        videoControllerLayout = (FrameLayout) findViewById(R.id.video_controller_layout);
        showVideoThumbImage();
        thumbImageBlurView.setBlurredView(videoThumb);
    }

    /**
     * 默认值是true
     *
     * @param isLive
     */
    public void setIsLiveController(boolean isLive) {
        this.isLive = isLive;
    }

    private void showVideoThumbImage() {
        if (TextUtils.isEmpty(videoThumbImage)) {
            videoThumb.setImageResource(R.color.black);
            thumbImageBlurView.setVisibility(View.GONE);
        } else {
            //            GlideImgManager.getInstance().showImg(context, videoThumb, videoThumbImage);
            thumbImageBlurView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(videoThumbImage)
                    .error(R.drawable.glide_default_image)
                    .into(new GlideDrawableImageViewTarget(videoThumb) {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);

                            videoThumb.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    thumbImageBlurView.invalidate();
                                }
                            }, 10);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            videoThumb.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    thumbImageBlurView.invalidate();
                                }
                            }, 10);
                        }
                    });
        }
    }

    public void setVideoThumbImage(String image) {
        this.videoThumbImage = image;
        if (videoThumb != null) {
            showVideoThumbImage();
        }
    }

    public void setVideoYuGaoViewVisible(long allDownTime) {
        centerView.setVisibility(View.VISIBLE);
        yuGaoDownTimeView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        centerText.setVisibility(View.GONE);
        if (allDownTime > 0) {
            if (yuGaoCountDownTimer != null) {
                yuGaoCountDownTimer.cancel();
                yuGaoCountDownTimer = null;
            }
            yuGaoCountDownTimer = new YuGaoTimeDown(allDownTime, 1000);
            yuGaoCountDownTimer.start();
        } else {
            yuGaoDownTimeTextView.setText("预告已经过期");
        }

    }

    public void stopYuGaoTimer() {
        yuGaoDownTimeView.setVisibility(View.GONE);
        if (yuGaoCountDownTimer != null) {
            yuGaoCountDownTimer.cancel();
            yuGaoCountDownTimer = null;
        }
    }

    public void removeNetChangeListener() {
        NetworkChangeManager.getInstance().removeOnNetworkChangeListener(this);
    }

    public void start() {
        centerText.setVisibility(View.INVISIBLE);
        centerText.setText("欢迎进入房间");
        centerView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        centerText.setVisibility(View.GONE);
    }

    public void setOnPreparedListenerCallback(IMediaPlayer.OnPreparedListener l) {
        onPreparedListenerList.clear();
        onPreparedListenerList.add(l);
    }

    public void addOnPreparedListenerCallback(IMediaPlayer.OnPreparedListener l) {
        onPreparedListenerList.add(l);
    }

    protected View findViewById(int id) {
        return controllerView.findViewById(id);
    }

    public void setLiveVideoPlayer(IVideoPlayer playerView) {
        this.playerView = playerView;
    }

    public void setVideoPlayer(IjkVideoView videoPlayer) {
        this.player = videoPlayer;
        videoPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                Log.e("LiveIJK", "onPrepared -----");
                centerView.setVisibility(View.GONE);
                videoThumb.setVisibility(View.GONE);
                yuGaoDownTimeView.setVisibility(View.GONE);
                thumbImageBlurView.setVisibility(View.GONE);
                for (IMediaPlayer.OnPreparedListener listener : onPreparedListenerList) {
                    listener.onPrepared(iMediaPlayer);
                }
            }
        });

        videoPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        showLoading();
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        centerView.setVisibility(View.GONE);
                        videoThumb.setVisibility(View.GONE);
                        thumbImageBlurView.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        centerView.setVisibility(View.GONE);
                        videoThumb.setVisibility(View.GONE);
                        thumbImageBlurView.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        centerView.setVisibility(View.GONE);
                        videoThumb.setVisibility(View.GONE);
                        thumbImageBlurView.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        videoPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                centerView.setVisibility(View.VISIBLE);
                videoThumb.setVisibility(View.INVISIBLE);
                thumbImageBlurView.setVisibility(View.GONE);
                centerText.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                String errorText = "直播加载失败!";
                if (isNetNotConnected) {
                    errorText = "网络连接已断开！" + errorText;
                }
                centerText.setText(errorText);
                return true;
            }
        });
    }

    public void hideCenterNoteView() {
        centerView.setVisibility(View.GONE);
        videoThumb.setVisibility(View.GONE);
        thumbImageBlurView.setVisibility(View.GONE);
    }

    public void showNoteText(String text) {
        centerView.setVisibility(View.VISIBLE);
        videoThumb.setVisibility(View.INVISIBLE);
        thumbImageBlurView.setVisibility(View.GONE);
        centerText.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        centerText.setText(text);
    }

    /**
     * 设置直播播放器的自定义控制界面. 为null 表示清除控制界面
     *
     * @param liveController
     */
    public void setILiveController(ILiveVideoController liveController) {
        this.liveVideoController = liveController;
        if (liveVideoController != null && liveVideoController.getControllerView() != null) {
            videoControllerLayout.removeAllViews();
            liveVideoController.setMediaPlayer(player);
            videoControllerLayout.addView(liveVideoController.getControllerView());
        } else {
            videoControllerLayout.removeAllViews();
        }
    }


    @Override
    public void hide() {
        if (liveVideoController != null) {
            liveVideoController.hide();
        }
    }

    @Override
    public boolean isShowing() {
        return liveVideoController != null && liveVideoController.isShowing();
    }

    @Override
    public void setAnchorView(View view) {
        if (liveVideoController != null) {
            liveVideoController.setAnchorView(view);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (liveVideoController != null) {
            liveVideoController.setEnabled(enabled);
        }
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
        if (liveVideoController != null) {
            liveVideoController.setMediaPlayer(player);
        }
    }

    @Override
    public void show(int timeout) {
        if (liveVideoController != null) {
            liveVideoController.show(timeout);
        }
    }

    @Override
    public void show() {
        if (liveVideoController != null) {
            liveVideoController.show();
        }
    }

    @Override
    public void showOnce(View view) {
        if (liveVideoController != null) {
            liveVideoController.showOnce(view);
        }
    }

    public void showLoading() {
        centerView.setVisibility(View.VISIBLE);
        videoThumb.setVisibility(View.INVISIBLE);
        thumbImageBlurView.setVisibility(View.GONE);
        centerText.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }


    @Override
    public void onChange(int networkType) {
        if (networkType != NetworkUtil.TYPE_NOT_CONNECTED) {
            isNetNotConnected = false;
            if (networkType == NetworkUtil.TYPE_MOBILE) {
                if (player.isPlaying()) {
                    playerView.pause();
                }
                if (netChecker == null) {
                    netChecker = new NetChecker(context, new NetChecker.CheckCallBack() {
                        @Override
                        public void callBack(boolean isCouldPlay, Object tag) {
                            if (isCouldPlay) {
                                autoRestart();
                            }
                        }
                    });
                }
                netChecker.checkNet(null);
            } else {
                if (!player.isPlaying()) {
                    autoRestart();
                }
            }
        } else {
            isNetNotConnected = true;
            if (playerView != null) {
                if (isLive) {
                    playerView.stop();
                } else {
                    playerView.pause();
                }
                centerView.setVisibility(View.VISIBLE);
                videoThumb.setVisibility(View.GONE);
                thumbImageBlurView.setVisibility(View.GONE);
                centerText.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                centerText.setText("网络连接已断开！");
            }
        }
    }

    /**
     * 根据是否是直播判断来决定怎么开始播放
     */
    private void autoRestart() {
        if (playerView != null && player != null) {
            if (isLive) {
                playerView.restart();
                showLoading();
            } else {
                if (player.isInPlaybackState()) {
                    playerView.start();
                } else {
                    playerView.restart();
                }
                hideCenterNoteView();
            }
        }
    }

    class YuGaoTimeDown extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public YuGaoTimeDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (yuGaoDownTimeTextView != null) {
                long seconds = millisUntilFinished / 1000;
                int hour = (int) (seconds / 3600);
                int minute = (int) ((seconds % 3600) / 60);
                int second = (int) ((seconds % 3600) % 60);
                String text = "";
                if (hour > 0) {
                    text = hour + " : " + minute + " : " + second;
                } else if (minute > 0) {
                    text = minute + " : " + second;
                } else {
                    text = second + "s";
                }
                yuGaoDownTimeTextView.setText(text);
            }
        }

        @Override
        public void onFinish() {
            if (yuGaoDownTimeTextView != null) {
                yuGaoDownTimeTextView.setText("直播即将开始");
            }
        }
    }
}

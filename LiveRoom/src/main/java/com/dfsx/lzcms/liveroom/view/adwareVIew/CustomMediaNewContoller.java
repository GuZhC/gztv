package com.dfsx.lzcms.liveroom.view.adwareVIew;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dfsx.core.common.Util.Util;
import com.dfsx.lzcms.liveroom.R;
import com.dfsx.videoijkplayer.VSeekBar;
import com.dfsx.videoijkplayer.media.IMediaController;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import com.dfsx.videoijkplayer.media.MediaItem;
import com.dfsx.videoijkplayer.media.VideoVoiceManager;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @Description ${控制器}
 */
public class CustomMediaNewContoller implements IMediaController {

    private static final int SET_VIEW_HIDE = 1;
    private static final int TIME_OUT = 5000;
    private static final int MESSAGE_SHOW_PROGRESS = 2;
    private static final int PAUSE_IMAGE_HIDE = 3;
    private static final int MESSAGE_SEEK_NEW_POSITION = 4;
    private static final int MESSAGE_HIDE_CONTOLL = 5;
    private View itemView;
    private View view;
    private boolean isShow;
    private IjkVideoView videoView;
    private boolean isScroll;

    private SeekBar seekBar;
    //    AudioManager audioManager;
    private ProgressBar progressBar;

    private boolean isSound;
    private boolean isDragging;

    private boolean isPause;

    private boolean isShowContoller;
    private ImageView sound, full, play;
    private TextView time, allTime, seekTxt;
    private PointF lastPoint;
    private Context context;
    private ImageView pauseImage;
    private ImageView pauseClosedImage;
    private Bitmap bitmap;
    private GestureDetector detector;

    private boolean isLive;
    private MediaItem paseuAdPath;
    private OnPauseImagListener onPauseImagListener;

    private RelativeLayout show;
    private VSeekBar brightness_seek, sound_seek;
    private LinearLayout brightness_layout, sound_layout;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_VIEW_HIDE:
                    isShow = false;
                    itemView.setVisibility(View.GONE);
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShow) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
                case PAUSE_IMAGE_HIDE:
                    ((View) pauseImage.getParent()).setVisibility(View.GONE);
                    break;
                case MESSAGE_SEEK_NEW_POSITION:
                    if (newPosition >= 0) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case MESSAGE_HIDE_CONTOLL:
                    seekTxt.setVisibility(View.GONE);
                    brightness_layout.setVisibility(View.GONE);
                    sound_layout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public CustomMediaNewContoller(Context context, View view, OnPauseImagListener listener) {
        this.onPauseImagListener = listener;
        this.view = view;
        itemView = view.findViewById(R.id.media_contoller);
        this.videoView = (IjkVideoView) view.findViewById(R.id.main_video);
        itemView.setVisibility(View.GONE);
        isShow = false;
        isDragging = false;

        isShowContoller = true;
        this.context = context;
        //        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initView();
        initAction();
    }

    public void initView() {
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        seekBar = (SeekBar) itemView.findViewById(R.id.seekbar);
        allTime = (TextView) itemView.findViewById(R.id.all_time);
        time = (TextView) itemView.findViewById(R.id.time);
        full = (ImageView) itemView.findViewById(R.id.full);
        sound = (ImageView) itemView.findViewById(R.id.sound);
        play = (ImageView) itemView.findViewById(R.id.player_btn);
        pauseImage = (ImageView) view.findViewById(R.id.pause_image);
        pauseClosedImage = (ImageView) view.findViewById(R.id.btsn_closed);
        pauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paseuAdPath != null) {
                    if (onPauseImagListener != null)
                        onPauseImagListener.onClick(paseuAdPath, 2);
                }
            }
        });
        pauseClosedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = (View) pauseImage.getParent();
                if (parent != null)
                    parent.setVisibility(View.GONE);
            }
        });

        brightness_layout = (LinearLayout) view.findViewById(R.id.brightness_layout);
        brightness_seek = (VSeekBar) view.findViewById(R.id.brightness_seek);
        sound_layout = (LinearLayout) view.findViewById(R.id.sound_layout);
        sound_seek = (VSeekBar) view.findViewById(R.id.sound_seek);
        show = (RelativeLayout) view.findViewById(R.id.show);
        seekTxt = (TextView) view.findViewById(R.id.seekTxt);

        setLiveStyleUI();
    }

    /**
     * 设置直播风格显示
     *
     * @param isLive
     */
    public void setLive(boolean isLive) {
        this.isLive = isLive;
        setLiveStyleUI();
    }

    private void initAction() {
        sound_seek.setEnabled(false);
        brightness_seek.setEnabled(false);
        isSound = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_MUSIC) != 0;
        int soundRes = isSound ? R.mipmap.sound_open_icon :
                R.mipmap.sound_mult_icon;
        sound.setImageResource(soundRes);
        detector = new GestureDetector(context, new PlayGestureListener());
        mMaxVolume = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String string = generateTime((long) (duration * progress * 1.0f / 100));
                time.setText(string);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgress();
                isDragging = true;
                VideoVoiceManager.getInstance(context).muteAudio();
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                show();
                handler.removeMessages(SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                if (duration > 0) {
                    videoView.seekTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                }
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                VideoVoiceManager.getInstance(context).unmuteAudio();
                isDragging = false;
                handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
                show();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (detector.onTouchEvent(event))
                    return true;

                // 处理手势结束
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });

        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("custommedia", "event");

                Rect seekRect = new Rect();
                seekBar.getHitRect(seekRect);

                if ((event.getY() >= (seekRect.top - 50)) && (event.getY() <= (seekRect.bottom + 50))) {

                    float y = seekRect.top + seekRect.height() / 2;
                    //seekBar only accept relative x
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    }
                    MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                            event.getAction(), x, y, event.getMetaState());
                    return seekBar.onTouchEvent(me);

                }
                return false;
            }
        });

        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

                Log.e("setOnInfoListener", what + "");
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        if (progressBar.getVisibility() == View.GONE)
                            progressBar.setVisibility(View.VISIBLE);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        progressBar.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        progressBar.setVisibility(View.GONE);
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        progressBar.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        videoView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    if (VideoVoiceManager.getInstance(context).getAm()
                            .getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sound.setImageResource(R.mipmap.sound_open_icon);
                                VideoVoiceManager.getInstance(context).getAm()
                                        .setStreamMute(AudioManager.STREAM_MUSIC, false);
                                isSound = true;
                            }
                        });
                    }
                }
                return false;
            }
        });
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSound) {
                    //静音
                    sound.setImageResource(R.mipmap.sound_mult_icon);
                    VideoVoiceManager.getInstance(context).muteAudio();
                } else {
                    //取消静音
                    sound.setImageResource(R.mipmap.sound_open_icon);
                    VideoVoiceManager.getInstance(context).unmuteAudio();

                }
                isSound = !isSound;
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    pause();
                } else {
                    reStart();
                }
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScreen();
            }

        });
    }

    private void setLiveStyleUI() {
        if (isLive) {
            time.setVisibility(View.INVISIBLE);
            allTime.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
            seekBar.setEnabled(false);
        } else {
            time.setVisibility(View.VISIBLE);
            allTime.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setEnabled(true);
        }
    }

    public void showBottomControl(boolean ishow) {
        if (ishow) {
            time.setVisibility(View.INVISIBLE);
            allTime.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
            seekBar.setEnabled(false);
        } else {
            time.setVisibility(View.VISIBLE);
            allTime.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            seekBar.setEnabled(true);
        }
    }

    public void switchScreen() {
        if (getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            full.setImageResource(R.mipmap.full_screen_icon);
        } else {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            full.setImageResource(R.mipmap.icon_back_full_screen);
        }
    }

    public void start() {
        ((View) pauseImage.getParent()).setVisibility(View.GONE);
        itemView.setVisibility(View.GONE);
        play.setImageResource(R.mipmap.video_stop_btn);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void pause() {
        play.setImageResource(R.mipmap.video_play_btn);
        videoView.pause();

        if (!(paseuAdPath == null || TextUtils.isEmpty(paseuAdPath.getUrl()))) {
            if (Util.isGif(paseuAdPath.getUrl())) {
                Glide
                        .with(context)
                        .load(paseuAdPath.getUrl())
                        .asGif()
                        .error(R.drawable.glide_default_image)
                        .placeholder(R.color.transparent)
                        .crossFade()
                        .into(pauseImage);
            } else {
                Glide
                        .with(context)
                        .load(paseuAdPath.getUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//图片缓存策略,这个一般必须有
                        .error(R.drawable.glide_default_image)
                        .placeholder(R.color.transparent)
                        .crossFade()
                        .into(pauseImage);
            }
            ((View) pauseImage.getParent()).setVisibility(View.VISIBLE);
        }
//        else {
//            bitmap = videoView.getBitmap();
//            if (bitmap != null) {
//                pauseImage.setImageBitmap(bitmap);
//            }
//        }
//        ((View) pauseImage.getParent()).setVisibility(View.VISIBLE);
    }

    public void reStart() {
        play.setImageResource(R.mipmap.video_stop_btn);
        videoView.start();
        if (!(paseuAdPath == null || TextUtils.isEmpty(paseuAdPath.getUrl()))) {
            handler.sendEmptyMessageDelayed(PAUSE_IMAGE_HIDE, 100);
        }
//        if (bitmap != null) {
//            handler.sendEmptyMessageDelayed(PAUSE_IMAGE_HIDE, 100);
//            bitmap.recycle();
//            bitmap = null;
//            //                        pauseImage.setVisibility(View.GONE);
//        }
    }

    public void onCompletion() {
        play.setImageResource(R.mipmap.video_play_btn);
        seekBar.setProgress(100);
        time.setText(allTime.getText());
    }

    public boolean isFullScreen() {
        return getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    public void onPlayError() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private long duration;


    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(SET_VIEW_HIDE);
        itemView.setVisibility(View.GONE);
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

    @Override
    public void hide() {
        if (isShow) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(SET_VIEW_HIDE);
            itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void setAnchorView(View view) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setMediaPlayer(MediaController.MediaPlayerControl player) {
    }

    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, timeout);
    }

    @Override
    public void show() {
        if (!isShowContoller)
            return;
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        isShow = true;
        progressBar.setVisibility(View.GONE);
        itemView.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        show(TIME_OUT);
    }

    @Override
    public void showOnce(View view) {
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public void setVisiable() {
        show();
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        this.duration = duration;
        if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(duration));
        if (seekBar != null) {
            if (duration > 0) {
                float fpos = ((float) position) / duration * 100;
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        String string = generateTime(videoView.getCurrentPosition());
        if (duration == 0) {
            string = generateTime(0);
        }
        time.setText(string);
        return position;
    }

    private VedioIsPause vedioIsPause;

    public interface VedioIsPause {
        void pause(boolean pause);
    }

    public void setPauseImageHide() {
        ((View) pauseImage.getParent()).setVisibility(View.GONE);
    }

    public class PlayGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean firstTouch;
        private boolean volumeControl;
        private boolean seek;

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            //            handler.removeMessages(SET_VIEW_HIDE);
            //横屏下拦截事件
            if (getScreenOrientation((Activity) context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return true;
            } else {
                return super.onDown(e);
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x = e1.getX() - e2.getX();
            float y = e1.getY() - e2.getY();
            Log.e("TAG", "distanceX, Y == " + distanceX + "," + distanceY);
            if (firstTouch) {
                seek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = e1.getX() < view.getMeasuredWidth() * 0.5;
                firstTouch = false;
            }

            if (seek) {
                onProgressSlide(-x / view.getWidth(), e1.getX() / view.getWidth());
            } else {
                float percent = y / view.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private int volume = -1;
    private float brightness = -1;
    private long newPosition = -1;
    private int mMaxVolume;

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            handler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(MESSAGE_HIDE_CONTOLL);
        handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CONTOLL, 500);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = VideoVoiceManager.getInstance(context).getAm().getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        //        hide();

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        VideoVoiceManager.getInstance(context).getAm().setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        if (sound_layout.getVisibility() == View.GONE)
            sound_layout.setVisibility(View.VISIBLE);
        // 变更进度条
        int i = (int) (index * 1.0f / mMaxVolume * 100);
        if (i == 0) {
            sound.setImageResource(R.mipmap.sound_mult_icon);
        } else {
            sound.setImageResource(R.mipmap.sound_open_icon);
        }
        sound_seek.setProgress(i);
    }

    /**
     * 滑动跳转
     *
     * @param percent 移动比例
     * @param downPer 按下比例
     */
    private void onProgressSlide(float percent, float downPer) {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        if (videoView.getDuration() == 0) {// if the video is live stream, could not set new position
            newPosition = -1;
        }
        int showDelta = (int) delta / 1000;
        Log.e("showdelta", ((downPer + percent) * 100) + "");
        if (showDelta != 0) {
            if (seekTxt.getVisibility() == View.GONE)
                seekTxt.setVisibility(View.VISIBLE);
            String current = generateTime(newPosition);
            seekTxt.setText(current + "/" + allTime.getText());
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = ((Activity) context).getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        WindowManager.LayoutParams lpa = ((Activity) context).getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }

        if (brightness_layout.getVisibility() == View.GONE)
            brightness_layout.setVisibility(View.VISIBLE);

        brightness_seek.setProgress((int) (lpa.screenBrightness * 100));
        ((Activity) context).getWindow().setAttributes(lpa);

    }

    public void setPaseuAdPath(MediaItem paseuAdPath) {
        this.paseuAdPath = paseuAdPath;
    }

    public interface OnPauseImagListener {
        public void onClick(MediaItem mediaItem, int adType);
    }

}

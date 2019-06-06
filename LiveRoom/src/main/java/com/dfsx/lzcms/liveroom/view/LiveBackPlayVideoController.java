package com.dfsx.lzcms.liveroom.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.lzcms.liveroom.R;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by liuwb on 2017/5/10.
 */
public class LiveBackPlayVideoController extends FrameLayout implements ILiveVideoController, IMediaPlayer.OnCompletionListener {
    private static final int SET_VIEW_HIDE = 1;
    private static final int TIME_OUT = 3000;
    private static final int MESSAGE_SHOW_PROGRESS = 2;
    private Context context;

    private TextView time, allTime, seekTxt;
    private SeekBar seekBar;
    private ImageView exitFull;
    private ImageView playPauseImage;

    private View bottomControllerView;

    private boolean isDragging;
    private boolean isShow;

    private long duration;

    private MediaController.MediaPlayerControl player;
    private LiveFullRoomVideoPlayer videoPlayerView;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_VIEW_HIDE:
                    isShow = false;
                    hideByAnimator();
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShow) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }
                    break;
            }
        }
    };

    public LiveBackPlayVideoController(Context context) {
        this(context, null);
    }

    public LiveBackPlayVideoController(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveBackPlayVideoController(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveBackPlayVideoController(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    public static LiveBackPlayVideoController newInstanceView(Context context) {
        LiveBackPlayVideoController backPlayVideoController = new LiveBackPlayVideoController(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        backPlayVideoController.setLayoutParams(params);
        return backPlayVideoController;
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.live_back_play_full_screen_controller, this);
        bottomControllerView = findViewById(R.id.controller_bottom_view);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        allTime = (TextView) findViewById(R.id.all_time);
        time = (TextView) findViewById(R.id.time);
        exitFull = (ImageView) findViewById(R.id.exit_full);
        playPauseImage = (ImageView) findViewById(R.id.play_pause);

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
                autoShow();
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                handler.removeMessages(SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                if (duration > 0) {
                    player.seekTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                }
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                isDragging = false;
                handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
                autoShow();
            }
        });

        exitFull.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayerView != null) {
                    videoPlayerView.switchTopVideoScreen();
                }
            }
        });

        playPauseImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    if (player.isPlaying()) {
                        player.pause();
                    } else {
                        player.start();
                    }
                }
                setPlayPauseImage();
            }
        });

        setPlayPauseImage();
    }

    private void setPlayPauseImage() {
        if (player != null && player.isPlaying()) {
            playPauseImage.setImageResource(R.drawable.icon_video_playing);
        } else {
            playPauseImage.setImageResource(R.drawable.icon_video_pause_state);
        }
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ?
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
                :
                String.format("%02d:%02d", minutes, seconds);
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }
        long position = player.getCurrentPosition();
        long duration = player.getDuration();
        this.duration = duration;
        if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(duration));
        if (seekBar != null) {
            if (duration > 0) {
                float fpos = ((float) position) / duration * 100;
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = player.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        String string = generateTime(player.getCurrentPosition());
        if (duration == 0) {
            string = generateTime(0);
        }
        time.setText(string);
        return position;
    }

    @Override
    public View getControllerView() {
        return this;
    }

    @Override
    public void setLiveVideoPlayerView(LiveFullRoomVideoPlayer videoPlayerView) {
        this.videoPlayerView = videoPlayerView;
        videoPlayerView.removeOnCompletionListener(this);
        videoPlayerView.addOnCompletionListener(this);
    }

    @Override
    public void hide() {
        if (isShow) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(SET_VIEW_HIDE);
            hideByAnimator();
        }
    }

    private void hideByAnimator() {
        bottomViewOutAnimator();
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
        this.player = player;
    }

    @Override
    public void show(int timeout) {
        handler.sendEmptyMessageDelayed(SET_VIEW_HIDE, timeout);
    }

    @Override
    public void show() {
        isShow = true;
        setVisibility(View.VISIBLE);
        setPlayPauseImage();
        bottomViewInAnimator();
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        show(TIME_OUT);
    }

    private void bottomViewInAnimator() {
        bottomControllerView.setVisibility(VISIBLE);
        ObjectAnimator movein = ObjectAnimator.ofFloat(bottomControllerView, "translationY", 500f, 0f);
        movein.setDuration(300);
        movein.start();
    }

    private void bottomViewOutAnimator() {
        ObjectAnimator moveOut = ObjectAnimator.ofFloat(bottomControllerView, "translationY", 0, 500f);
        moveOut.setDuration(300);
        moveOut.start();
        moveOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void autoShow() {
        isShow = true;
        setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        show(TIME_OUT);
    }

    @Override
    public void showOnce(View view) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        playPauseImage.setImageResource(R.drawable.icon_video_pause_state);
    }
}

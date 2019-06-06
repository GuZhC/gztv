package com.dfsx.lzcms.liveroom.view;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2017/7/6.
 */
public class LiveServiceVideoTimeBarController extends LiveServiceVideoController {
    private static final int MESSAGE_UPDATE_PROGRESS = 2;

    private SeekBar seekBar;
    private TextView startTime;
    private TextView allTime;

    private long duration;
    private boolean isDragging;

    public LiveServiceVideoTimeBarController(Context context, IVideoMultilineVideoPlayerApi videoPlayer, ViewGroup controllerContainer) {
        super(context, videoPlayer, controllerContainer);
    }

    @Override
    protected void onSetVideoTimeView(FrameLayout videoTimeViewContainer) {
        super.onSetVideoTimeView(videoTimeViewContainer);
        View v = LayoutInflater.from(context)
                .inflate(R.layout.service_video_time_bar_layout, null);
        videoTimeViewContainer.addView(v);
        seekBar = (SeekBar) v.findViewById(R.id.seekbar);
        startTime = (TextView) v.findViewById(R.id.start_time_text);
        allTime = (TextView) v.findViewById(R.id.all_time_text);
    }

    @Override
    protected void initAction() {
        super.initAction();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String string = generateTime((long) (duration * progress * 1.0f / 100));
                startTime.setText(string);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgress();
                isDragging = true;
                handler.removeMessages(MESSAGE_UPDATE_PROGRESS);
                show();
                handler.removeMessages(SET_VIEW_HIDE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                if (duration > 0) {
                    videoPlayer.seekTo((int) (duration * seekBar.getProgress() * 1.0f / 100));
                }
                handler.removeMessages(MESSAGE_UPDATE_PROGRESS);
                isDragging = false;
                handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
                show();
            }
        });
    }

    @Override
    protected void handleHandlerMessage(Message msg) {
        super.handleHandlerMessage(msg);
        if (msg.what == MESSAGE_UPDATE_PROGRESS) {
            setProgress();
            if (!isDragging && isShow) {
                msg = handler.obtainMessage(MESSAGE_UPDATE_PROGRESS);
                handler.sendMessageDelayed(msg, 1000);
            }
        }
    }

    @Override
    public void show() {
        super.show();
        handler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = videoPlayer.getCurrentPosition();
        long duration = videoPlayer.getDuration();
        this.duration = duration;
        if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(duration));
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 100L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoPlayer.getBufferPercentage();
            seekBar.setSecondaryProgress(percent);
        }
        String string = generateTime(videoPlayer.getCurrentPosition());
        if (duration == 0) {
            string = generateTime(0);
        }
        startTime.setText(string);
        return position;
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }
}

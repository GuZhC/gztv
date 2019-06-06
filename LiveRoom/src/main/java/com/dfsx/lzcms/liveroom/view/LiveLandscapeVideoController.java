package com.dfsx.lzcms.liveroom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import com.dfsx.lzcms.liveroom.R;

/**
 * Created by liuwb on 2017/5/10.
 */
public class LiveLandscapeVideoController extends FrameLayout implements ILiveVideoController {

    private Context context;

    private LiveFullRoomVideoPlayer videoPlayerView;

    private ImageView exitFullImage;

    public LiveLandscapeVideoController(Context context) {
        this(context, null);
    }

    public LiveLandscapeVideoController(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveLandscapeVideoController(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LiveLandscapeVideoController(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    @Override
    public View getControllerView() {
        return this;
    }

    @Override
    public void setLiveVideoPlayerView(LiveFullRoomVideoPlayer videoPlayerView) {
        this.videoPlayerView = videoPlayerView;
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.live_video_landescape_layout, this);
        exitFullImage = (ImageView) findViewById(R.id.exit_full);

        exitFullImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayerView != null) {
                    videoPlayerView.switchTopVideoScreen();
                }
            }
        });
    }

    @Override
    public void hide() {

    }

    @Override
    public boolean isShowing() {
        return false;
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

    }

    @Override
    public void show() {

    }

    @Override
    public void showOnce(View view) {

    }
}

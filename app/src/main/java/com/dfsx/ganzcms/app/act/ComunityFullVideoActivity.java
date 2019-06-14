package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.ganzcms.app.R;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by heynag on 2016/10/10.
 */
public class ComunityFullVideoActivity extends AbsVideoScreenSwitchActivity {

    private Activity context;
    private FrameLayout fullScreenLayout;

    protected SystemBarTintManager systemBarTintManager;

    private View rootView;
    private int statusBarColor;
    private String ulr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        systemBarTintManager = Util.applyKitKatTranslucency(this,
        //                getStatusBarColor());
        rootView = getLayoutInflater().inflate(R.layout.act_communty_full_video, null);
        setContentView(rootView);
        if (getIntent() != null) {
            ulr = getIntent().getStringExtra("url");
        }
        context = this;
        initView();
    }

    @Override
    protected boolean isShortVideoFragmetVideoFull() {
        return false;
    }

    private void initView() {
        //        portraintLayout = findViewById(R.id.portrait_layout);
        fullScreenLayout = (FrameLayout) findViewById(R.id.full_screen_layout);
        if (ulr != null && !TextUtils.isEmpty(ulr)) {
            Log.e("TAG", "play url == " + ulr);
            getVideoPlayer().start(ulr);
            getVideoPlayer().setCompletionListener(new VideoAdwarePlayView.CompletionListener() {
                @Override
                public void completion(IMediaPlayer mp) {
                    finish();
                }
            });
            getVideoPlayer().setErrorListener(new VideoAdwarePlayView.OnErrorListener() {
                @Override
                public void errorListener(IMediaPlayer mp) {
                    finish();
                }
            });
        }
    }

    public int getStatusBarColor() {
        return this.getResources().getColor(R.color.public_red_bkg);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void addVideoPlayerToContainer(VideoAdwarePlayView videoPlayer) {
        fullScreenLayout.addView(videoPlayer);
    }

    public VideoAdwarePlayView getVideoPlayer() {
        return videoPlayer;
    }

    //    @Override
    //    public void onBackPressed() {
    //        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
    //            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    //            return;
    //        }
    //        //双击两次退出
    //        if (backPressedCount < 1) {
    //            backPressedCount++;
    //            Toast.makeText(context, "再按一次退出", Toast.LENGTH_SHORT).show();
    //            handler.postDelayed(new Runnable() {
    //                @Override
    //                public void run() {
    //                    backPressedCount = 0;
    //                }
    //            }, 5000);
    //            return;
    //        } else {
    //            backPressedCount = 0;
    //        }
    //        super.onBackPressed();
    //    }

    //    @Override
    //    public void onConfigurationChanged(Configuration newConfig) {
    //        //        if (currentShowId != R.id.bottom_tab_live) {
    //        //            return;
    //        //        }
    //        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
    //            if (activityContainer != null && systemBarTintManager != null) {
    //                activityContainer.setPadding(0, systemBarTintManager.getConfig().getStatusBarHeight(), 0, 0);
    //            }
    //        } else {
    //            if (activityContainer != null) {
    //                activityContainer.setPadding(0, 0, 0, 0);
    //            }
    //        }
    //        super.onConfigurationChanged(newConfig);
    //    }
}

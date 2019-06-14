package com.dfsx.ganzcms.app.act;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dfsx.core.common.act.ApiVersionErrorActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.business.PictureManager;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.NetChecker;
import com.dfsx.videoijkplayer.media.MediaItem;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heynag on 2016/10/10.
 */
public class StartAdFullVideoActivity extends AbsVideoScreenSwitchActivity {

    private Activity context;
    private FrameLayout fullScreenLayout;
    private View rootView;
    private ContentCmsApi contentCmsApi;
    private MediaItem mediaItem;
    private boolean isStartApp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade().setDuration(2000));
            getWindow().setExitTransition(new Fade().setDuration(2000));
        }
        rootView = getLayoutInflater().inflate(R.layout.act_start_ad_video, null);
        setContentView(rootView);
        if (getIntent() != null) {
            isStartApp = getIntent().getBooleanExtra("isstartApp", true);
            mediaItem = (MediaItem) getIntent().getSerializableExtra("object");
        }
        context = this;
        contentCmsApi = new ContentCmsApi(this);
        initView();
        initData();
    }


    @Override
    protected void createVideoPlayer() {
        videoPlayer = new VideoAdwarePlayView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        videoPlayer.setLayoutParams(params);
    }

    @Override
    protected boolean isShortVideoFragmetVideoFull() {
        return false;
    }

    public void initData() {
        List<MediaItem> datalist = new ArrayList<>();
        if (isStartApp) {
            MediaItem adItem = PictureManager.getInstance().getStartAdware();
            if (adItem != null) {
                Log.e("TAG", "play url == " + adItem.getUrl());
                datalist = new ArrayList<>();
                adItem.setAdware(true);
                datalist.add(adItem);
            }
        } else {
            datalist.add(mediaItem);
        }
        ColumnBasicListManager.getInstance().closedVolunes(this);
        getVideoPlayer().start(datalist, false, new NetChecker.CheckCallBack() {
            @Override
            public void callBack(boolean isCouldPlay, Object tag) {
                if (!isCouldPlay && isStartApp)
                    gotoMainAct();
            }
        });
        getVideoPlayer().setCompletionListener(new VideoAdwarePlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                ColumnBasicListManager.getInstance().openVolunes(StartAdFullVideoActivity.this);
                if (isStartApp) {
                    gotoMainAct();
                } else
                    finish();
            }
        });
        getVideoPlayer().setErrorListener(new VideoAdwarePlayView.OnErrorListener() {
            @Override
            public void errorListener(IMediaPlayer mp) {
                ColumnBasicListManager.getInstance().openVolunes(StartAdFullVideoActivity.this);

                if (isStartApp) {
                    gotoMainAct();
                } else
                    finish();
            }
        });
        getVideoPlayer().setOnAdwareListener(new VideoAdwarePlayView.OnAdwareListener() {
            @Override
            public void onClick(MediaItem mediaItem, int adType) {
                contentCmsApi.onADClickNetRequest(mediaItem.getId(), mediaItem.getAdid(), 1);
//                            String url = "http://www.baidu.com";
                Bundle bundles = new Bundle();
                bundles.putString(BaseAndroidWebFragment.PARAMS_URL, mediaItem.getLinkUrl());
                WhiteTopBarActivity.startAct(context, BaseAndroidWebFragment.class.getName(), "", "",
                        bundles);
            }
        });
    }

    private void initView() {
        fullScreenLayout = (FrameLayout) findViewById(R.id.full_screen_layout);
        boolean isFull = getIntent().getExtras().getBoolean("isFull");
        findViewById(R.id.bottom_layout).setVisibility(isFull ? View.GONE : View.VISIBLE);
    }

    @Override
    public void addVideoPlayerToContainer(VideoAdwarePlayView videoPlayer) {
        fullScreenLayout.addView(videoPlayer);
    }

    public VideoAdwarePlayView getVideoPlayer() {
        return videoPlayer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void gotoMainAct() {
        ColumnBasicListManager.getInstance().openVolunes(StartAdFullVideoActivity.this);

        SharedPreferences preferences = getSharedPreferences("count", 0); // 存在则打开它，否则创建新的Preferences
        int count = preferences.getInt("count", 0); // 取出数据

        if (App.getInstance().getmSession().isForceUpdateApp()) {
            Intent intent = new Intent(App.getInstance(), ApiVersionErrorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().getApplicationContext().startActivity(intent);
        } else {
            /**
             *如果用户不是第一次使用则直接调转到显示界面,否则调转到引导界面
             */
            if (count == 0) {
                Intent intent1 = new Intent();
                intent1.setClass(this, MainTabActivity.class);
                startActivity(intent1);
            } else {
                Intent intent2 = new Intent();
                intent2.setClass(this, MainTabActivity.class);
                startActivity(intent2);
            }
            Log.e("Wel", "======================end");
            finish();

            //实例化Editor对象
            SharedPreferences.Editor editor = preferences.edit();
            //存入数据
            editor.putInt("count", 1); // 存入数据
            //提交修改
            editor.commit();
        }
    }


}

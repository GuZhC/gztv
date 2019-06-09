package com.dfsx.ganzcms.app.act;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dfsx.core.common.Util.SystemBarTintManager;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.ShortVideoAdapter;
import com.dfsx.ganzcms.app.adapter.ShortVideoDetailAdapter;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.ganzcms.app.model.TopicalEntry;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.thirdloginandshare.share.*;
import com.dfsx.videoijkplayer.VideoPlayView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class ShortVideoDetailActivity extends AbsVideoActivity implements BaseQuickAdapter.OnItemChildClickListener {

    private SystemBarTintManager systemBarTintManager;
    private SharePopupwindow sharePopupwindow;
    private RecyclerView videoRecycler;
    private LinearLayoutManager mLayoutManager;
    private ShortVideoDetailAdapter adapter;
    private List<ShortVideoBean> mVideoData = new ArrayList<>();
    //将要播放位置
    private int newPosition = 0;
    //播放状态
    private boolean isPlay = false;
    private int playPostion = 0;
    private FrameLayout videoFullLyout;
    private ImageView ivBack;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_short_video_detail, null);
        setContentView(rootView);
        systemBarTintManager = Util.applyKitKatTranslucency(this, ContextCompat.getColor(this,R.color.transparent));
        initData();
        initView();
        initListener();
    }


    private void initView() {
        videoRecycler = (RecyclerView) findViewById(R.id.rl_video_detail_recycler);
        videoFullLyout = (FrameLayout) findViewById(R.id.fl_video_detail_full);
        ivBack = (ImageView) findViewById(R.id.iv_video_detail_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置recycler
        mLayoutManager = new LinearLayoutManager(this);
        videoRecycler.setLayoutManager(mLayoutManager);

        adapter = new ShortVideoDetailAdapter(mVideoData, videoPlayer);
        videoRecycler.setAdapter(adapter);
        playVideo();

    }

    private void initData() {
        for (int i = 0; i < 15; i++) {
            ShortVideoBean shortVideoBean = new ShortVideoBean();
            if (i == 0) shortVideoBean.setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
            if (i == 1) shortVideoBean.setVitemType(ShortVideoBean.TYPE_SHARE);
            shortVideoBean.setTitle(i + " video item");
            mVideoData.add(shortVideoBean);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initListener() {
        videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                mVideoData.get(playPostion ).setVideo_state(ShortVideoAdapter.VIDEO_END);
                adapter.notifyItemChanged(playPostion);
            }
        });
        adapter.setOnItemChildClickListener(this);
        videoRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //静止的时候
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideo();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager == null) {
                    return;
                }
                //得到显示屏内的第一个list的位置数position
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                View firstView = mLayoutManager.findViewByPosition(firstVisibleItem);
                //数量小于二
                if (null == firstView || mLayoutManager.getChildCount() < 2) {
                    return;
                }
                //向上滑动
                if (dy > 0) {
                    if (firstView.getHeight() + firstView.getTop() <= firstView.getHeight() * 2 / 3) {
                        newPosition = firstVisibleItem + 1;
                        if (newPosition == playPostion) return;
                        if (newPosition == 1) {
                            newPosition = 2;
                            return;
                        }
                        stopPVideo();
                    }
                }
                //向下滑动
                if (dy < 0) {
                    if (firstView.getHeight() + firstView.getTop() >= firstView.getHeight() * 2 / 3) {
                        newPosition = firstVisibleItem;
                        if (newPosition == playPostion) return;
                        if (newPosition == 1) {
                            newPosition = 2;
                            return;
                        }
                        stopPVideo();
                    }
                }
            }
        });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_short_video_play:

                playAgain(position);
                break;
            case R.id.tv_short_video_comment:
                Log.e("play", "setPlay: tv_short_video_comment：" + newPosition);
                break;
            case R.id.tv_short_video_share:
                shareWnd();
//                Log.e("play", "setPlay: tv_short_video_share：" + newPosition);
                break;
            case R.id.tv_short_video_share_friends:
                onSharePlatfrom(SharePlatform.Wechat_FRIENDS);
//                Log.e("play", "setPlay: tv_short_video_share_friends：" + newPosition);
                break;
            case R.id.tv_short_video_share_wx:
                onSharePlatfrom(SharePlatform.Wechat);
//                Log.e("play", "setPlay: tv_short_video_share_wx：" + newPosition);
                break;
            case R.id.cb_short_video_sound:
                Log.e("play", "setPlay: cb_short_video_sound：" + newPosition);
                break;
            case R.id.tv_short_video_go_detail:
                Log.e("play", "setPlay: cb_short_video_sound：" + newPosition);
                break;
            case R.id.tv_short_video_praise:
                Log.e("play", "setPlay: tv_short_video_praise：" + newPosition);
                break;
            case R.id.btn_short_video_look_again:
                playAgain(position);
                break;
            case R.id.iv_short_video_detail_praise:
                Log.e("play", "setPlay: iv_short_video_detail_praise：" + newPosition);
                break;
            case R.id.iv_short_video_detail_comment:
                Log.e("play", "setPlay: iv_short_video_detail_comment：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_wx:
                onSharePlatfrom(SharePlatform.Wechat);
                Log.e("play", "setPlay: iv_short_video_detail_share_wx：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_pyq:
                onSharePlatfrom(SharePlatform.Wechat_FRIENDS);
                Log.e("play", "setPlay: iv_short_video_detail_share_pyq：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_wb:
                onSharePlatfrom(SharePlatform.WeiBo);
                Log.e("play", "setPlay: iv_short_video_detail_share_wb：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_qq:
                onSharePlatfrom(SharePlatform.QQ);
                Log.e("play", "setPlay: iv_short_video_detail_share_qq：" + newPosition);
                break;
        }
    }

    private void playAgain(int position) {
        stopPVideo();
        newPosition = position;
        View nowView = mLayoutManager.findViewByPosition(position);
        videoRecycler.smoothScrollBy(0, nowView.getTop());
        playVideo();
    }

    private void stopPVideo() {
        if (isPlay) {
            if (videoPlayer != null) {
                ViewGroup view = (ViewGroup) videoPlayer.getParent();
                if (view != null) {
                    view.removeView(videoPlayer);
                }
            }
            videoPlayer.pause();
            isPlay = false;
            mVideoData.get(playPostion).setVideo_state(ShortVideoAdapter.VIDEO_STOP);
            adapter.notifyItemChanged(playPostion);
            Log.e("play", "setPlay: 暂停：" + playPostion);
        }

    }

    private void playVideo() {
        if (!isPlay) {
            if (videoPlayer != null) {
                ViewGroup view = (ViewGroup) videoPlayer.getParent();
                if (view != null) {
                    view.removeView(videoPlayer);
                }
            }
            if (videoPlayer == null) {
                return;
            }
            mVideoData.get(newPosition).setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
            playPostion = newPosition;
//            adapter.notifyItemChanged(newPosition);
            isPlay = true;
            if (addVideoToLayout(videoPlayer)) {
                videoPlayer.start("http://file.yatv.tv/cms/videos/nmip-media/2019-05-29/4370046821-v0-mp4/8F58BC10D49511829BD82C260052FDEB.mp4");
                Log.e("play", "setPlay: 播放：" + newPosition);
            } else {
                stopPVideo();
            }
        }

    }

    public void shareWnd() {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(this);
            sharePopupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    int vId = v.getId();
                    if (vId == com.dfsx.lzcms.liveroom.R.id.share_qq) {
                        onSharePlatfrom(SharePlatform.QQ);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wb) {
                        onSharePlatfrom(SharePlatform.WeiBo);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wx) {
                        onSharePlatfrom(SharePlatform.Wechat);
                    } else if (vId == com.dfsx.lzcms.liveroom.R.id.share_wxfriends) {
                        onSharePlatfrom(SharePlatform.Wechat_FRIENDS);
                    }
                }
            });
        }
        sharePopupwindow.show(rootView);
    }

    public void onSharePlatfrom(SharePlatform platform) {
        ShareContent info = new ShareContent();
        info.title = "mtitle";
        info.type = ShareContent.UrlType.WebPage;
        String server = App.getInstance().getBaseUrl();
        info.thumb = "mthumb";
        info.url = server;
        info.disc = "mInfo";
//        if (mCotentInfoeny == null) return;
//        ShareContent content = new ShareContent();
//        content.title = mCotentInfoeny.getTitle();
//        content.disc = mCotentInfoeny.getSummary();
//        if (mCotentInfoeny.getThumbnail_urls() != null && mCotentInfoeny.getThumbnail_urls().size() > 0)
//            content.thumb = mCotentInfoeny.getThumbnail_urls().get(0);
//        content.type = ShareContent.UrlType.WebPage;
//        content.url = App.getInstance().getContentShareUrl() + mCotentInfoeny.getId();
        AbsShare share = ShareFactory.createShare(this, platform);
        share.share(info);
    }
    @Override
    public void addVideoPlayerToContainer(VideoPlayView videoPlayer) {
        addVideoToLayout(videoPlayer);
    }

    private boolean addVideoToLayout(VideoPlayView videoPlayer) {
        FrameLayout videoLyout = (FrameLayout) adapter.getViewByPosition(videoRecycler, newPosition, R.id.fl_short_video_video);
        if (videoLyout != null) {
            videoLyout.removeAllViews();
                videoLyout.addView(videoPlayer, 0);
                return true;
        }
        return false;
    }

    @Override
    public void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer) {
        if (videoFullLyout != null) {
            if (videoFullLyout.getChildAt(0) == null ||
                    !(videoFullLyout.getChildAt(0)
                            instanceof VideoPlayView)) {
                videoFullLyout.addView(videoPlayer, 0);
            }
        }
    }
}

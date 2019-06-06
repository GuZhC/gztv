package com.dfsx.ganzcms.app.act;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.adapter.ShortVideoAdapter;
import com.dfsx.ganzcms.app.adapter.ShortVideoDetailAdapter;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareUtil;
import com.dfsx.videoijkplayer.VideoPlayView;

import java.util.ArrayList;
import java.util.List;

public class ShortVideoDetailActivity extends AbsVideoActivity implements BaseQuickAdapter.OnItemChildClickListener {

    private RecyclerView videoRecycler;
    private LinearLayoutManager mLayoutManager;
    private ShortVideoDetailAdapter adapter;
    private List<ShortVideoBean> mVideoData = new ArrayList<>();
    //将要播放位置
    private int newPosition = 0;
    //播放状态
    private boolean isPlay = true;
    private int playPostion = 0;
    private FrameLayout videoFullLyout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_video_detail);
        initData();
        initView();
        initListener();
    }


    private void initView() {
        videoRecycler = (RecyclerView) findViewById(R.id.rl_video_detail_recycler);
        videoFullLyout = (FrameLayout) findViewById(R.id.fl_video_detail_full);
        LinearLayout topView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_short_video_detail_top, null);
        //设置recycler
        mLayoutManager = new LinearLayoutManager(this);
        videoRecycler.setLayoutManager(mLayoutManager);

        adapter = new ShortVideoDetailAdapter(mVideoData, videoPlayer);
//        VideoVoiceManager.getInstance(this).muteAudio();

        videoRecycler.setAdapter(adapter);
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

    private void initListener() {
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
                if (isPlay) {
                    return;
                }
                playAgain(position);
                break;
            case R.id.tv_short_video_comment:
                Log.e("play", "setPlay: tv_short_video_comment：" + newPosition);
                break;
            case R.id.tv_short_video_share:
                ShareContent info = new ShareContent();
                info.title = "mtitle";
                info.type = ShareContent.UrlType.WebPage;
                String server = App.getInstance().getBaseUrl();
                info.thumb = "mthumb";
                info.url = server;
                info.disc = "mInfo";
                ShareUtil.share(this, info);
                Log.e("play", "setPlay: tv_short_video_share：" + newPosition);
                break;
            case R.id.tv_short_video_share_friends:
                Log.e("play", "setPlay: tv_short_video_share_friends：" + newPosition);
                break;
            case R.id.tv_short_video_share_wx:
                Log.e("play", "setPlay: tv_short_video_share_wx：" + newPosition);
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
                Log.e("play", "setPlay: iv_short_video_detail_share_wx：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_pyq:
                Log.e("play", "setPlay: iv_short_video_detail_share_pyq：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_wb:
                Log.e("play", "setPlay: iv_short_video_detail_share_wb：" + newPosition);
                break;
            case R.id.iv_short_video_detail_share_qq:
                Log.e("play", "setPlay: iv_short_video_detail_share_qq：" + newPosition);
                break;
        }
    }

    private void playAgain(int position) {
        stopPVideo();
        newPosition = position;
        View nowView = mLayoutManager.findViewByPosition(position);
        videoRecycler.smoothScrollBy(0, nowView.getTop());
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

    @Override
    public void addVideoPlayerToContainer(VideoPlayView videoPlayer) {
        addVideoToLayout(videoPlayer);
    }

    private boolean addVideoToLayout(VideoPlayView videoPlayer) {
        FrameLayout videoLyout = (FrameLayout) adapter.getViewByPosition(videoRecycler, newPosition, R.id.fl_short_video_video);
        if (videoLyout != null) {
            if (videoLyout.getChildAt(0) == null ||
                    !(videoLyout.getChildAt(0)
                            instanceof VideoPlayView)) {
                videoLyout.addView(videoPlayer, 0);
                return true;
            }
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

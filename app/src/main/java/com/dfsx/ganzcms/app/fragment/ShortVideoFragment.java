package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.CmsVideoActivity;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.act.ShortVideoDetailActivity;
import com.dfsx.ganzcms.app.adapter.ShortVideoAdapter;
import com.dfsx.ganzcms.app.adapter.ShortVideoViewPagerAdapter;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import com.dfsx.videoijkplayer.VideoPlayView;
import com.dfsx.videoijkplayer.media.VideoVoiceManager;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : GuZhC
 * @date : 2019/5/30 16:30
 * @description :  短视频页面fragment
 */
public class ShortVideoFragment extends Fragment implements BaseQuickAdapter.OnItemChildClickListener {
    private RecyclerView videoRecycler;
    private ShortVideoAdapter adapter;
    private List<String> mData = new ArrayList<>();
    private LayoutInflater inflater;
    private ImageView topVideoMore;
    public ViewPager topViewPager;
    private ShortVideoViewPagerAdapter videoViewPagerAdapter;
    private LinearLayoutManager mLayoutManager;
    //将要播放位置
    private int newPosition = 1;
    //播放状态
    private boolean isPlay = true;
    private int playPostion = 1;
    private List<ShortVideoBean> mVideoData = new ArrayList<>();
    private FrameLayout videoFullLayout;
    private VideoPlayView videoPlayer;
    private SharePopupwindow sharePopupwindow;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_short_video, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoRecycler = (RecyclerView) view.findViewById(R.id.recycler_short_video);
        videoFullLayout = (FrameLayout) view.findViewById(R.id.fl_short_video_full);
        //设置播放器
        videoPlayer = new VideoPlayView(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoPlayer.setLayoutParams(params);
        videoPlayer.setFullGone();
        videoPlayer.setMuteAudioe();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            videoPlayer.setTransitionName("comm_short_video");
        }
        initData();
        inintView();
    }

    private void inintView() {

        //设置banner
        LinearLayout topView = (LinearLayout) inflater.inflate(R.layout.layout_short_video_top, null);
        FrameLayout mViewPagerContainer = (FrameLayout) topView.findViewById(R.id.fl_short_video_vp_root);
        topViewPager = (ViewPager) topView.findViewById(R.id.vp_short_video_top);
        topVideoMore = (ImageView) topView.findViewById(R.id.img_short_video_more);
        videoViewPagerAdapter = new ShortVideoViewPagerAdapter(mData, getContext(), topViewPager);
        topViewPager.setAdapter(videoViewPagerAdapter);
        //默认在中间，使用户看不到边界
        int p = Integer.MAX_VALUE / 3;
        topViewPager.setCurrentItem(p - p % mData.size());
        topViewPager.setOffscreenPageLimit(3);
        topViewPager.setPageMargin(Util.dp2px(getContext(), 6));
        //将容器的触摸事件反馈给ViewPager
        mViewPagerContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return topViewPager.dispatchTouchEvent(event);
            }
        });
        if (getActivity() == null) {
            return;
        }
        //设置recycler
        mLayoutManager = new LinearLayoutManager(getActivity());
        videoRecycler.setLayoutManager(mLayoutManager);
        adapter = new ShortVideoAdapter(mVideoData, videoPlayer);
        videoRecycler.setAdapter(adapter);
        adapter.addHeaderView(topView);
        initListener();
    }

    //    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged -- ");
        if (videoPlayer != null) {
            videoPlayer.onChanged(newConfig);
            ViewGroup view = (ViewGroup) videoPlayer.getParent();
            if (view != null) {
                view.removeView(videoPlayer);
            }
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                FrameLayout portraintContainer = (FrameLayout) adapter.getViewByPosition(videoRecycler, playPostion, R.id.fl_short_video_video);
                if (portraintContainer == null) return;
                if (portraintContainer.getChildCount() <= 0 ||
                        !(portraintContainer.getChildAt(0) instanceof VideoPlayView)) {
                    portraintContainer.addView(videoPlayer, 0);
                }
            } else {
//                videoFullLayout = ((MainTabActivity)getActivity()).getmFlFullVideo();
                if (videoFullLayout.getChildCount() <= 0 ||
                        !(videoFullLayout.getChildAt(0) instanceof VideoPlayView)) {
                    videoFullLayout.addView(videoPlayer, 0);
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBannerScroll();
        playVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBannerScroll();
        stopPVideo();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoPlayer.stop();
        videoPlayer.release();
        videoPlayer.onDestroy();
        videoPlayer = null;
    }

    private void initListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Activity context = getActivity();
                if (context == null) {
                    return;
                }
            }
        });
        //banner点击
        videoViewPagerAdapter.setPagerClick(new ShortVideoViewPagerAdapter.PagerClick() {
            @Override
            public void onPagerClik(String data) {
                startActivity(new Intent(getActivity(), ShortVideoDetailActivity.class));
            }
        });
        //查看更多精选视频
        topVideoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "show more", Toast.LENGTH_SHORT);
            }
        });
        //视频播放结束
        videoPlayer.setCompletionListener(new VideoPlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                mVideoData.get(playPostion - 1).setVideo_state(ShortVideoAdapter.VIDEO_END);
                adapter.notifyItemChanged(playPostion);
            }
        });
        adapter.setOnItemChildClickListener(this);
        //recyclerView滑动监听
        videoRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //静止的时候
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideo();
                    if (playPostion == 1) {
                        startBannerScroll();
                    } else {
                        stopBannerScroll();
                    }
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
                        stopPVideo();
                    }
                }
                //向下滑动
                if (dy < 0) {
                    if (firstView.getHeight() + firstView.getTop() >= firstView.getHeight() * 2 / 3) {
                        newPosition = firstVisibleItem;
                        if (newPosition == playPostion) return;
                        if (newPosition == 0) {
                            newPosition = 1;
                            return;
                        }
                        stopPVideo();
                    }
                }
            }
        });
    }


    public void startBannerScroll() {
        if (videoViewPagerAdapter == null) return;
        videoViewPagerAdapter.startScroll();
    }

    public void stopBannerScroll() {
        if (videoViewPagerAdapter == null) return;
        videoViewPagerAdapter.stopScroll();
    }

    public void stopPVideo() {
        if (isPlay) {
            videoPlayer.stop();
            isPlay = false;
            mVideoData.get(playPostion - 1).setVideo_state(ShortVideoAdapter.VIDEO_STOP);
            adapter.notifyItemChanged(playPostion);
//            Log.e("play", "setPlay: 暂停：" + playPostion);
        }

    }

    public void playVideo() {
        if (!isPlay) {
            isPlay = true;
            mVideoData.get(newPosition - 1).setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
            playPostion = newPosition;
            adapter.notifyItemChanged(newPosition);
//            Log.e("play", "setPlay: 播放：" + newPosition);
        }
    }

    private void initData() {
        for (int i = 0; i < 15; i++) {
            mData.add(i + " item");
            ShortVideoBean shortVideoBean = new ShortVideoBean();
            if (i == 0) shortVideoBean.setVideo_state(ShortVideoAdapter.VIDEO_PLAY);
            shortVideoBean.setTitle(i + " video item");
            mVideoData.add(shortVideoBean);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent();
        View commView =  adapter.getViewByPosition(videoRecycler,position+1,R.id.rl_short_video_share_content);

        Bundle bundle1 = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),commView,"comm_short_video").toBundle();
        switch (view.getId()) {
            case R.id.tv_short_video_play:
                playAgain(position);
                break;
            case R.id.tv_short_video_comment:
                Bundle bundle = new Bundle();
                bundle.putLong("index", 14017178);
                intent.setClass(getContext(), CmsVideoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent,bundle1);
                Log.e("play", "setPlay: tv_short_video_comment：" + newPosition);
                break;
            case R.id.tv_short_video_go_detail:
                intent.setClass(getContext(), ShortVideoDetailActivity.class);
                startActivity(intent);
                Log.e("play", "setPlay: cb_short_video_sound：" + newPosition);
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
//                Log.e("play", "setPlay: cb_short_video_sound：" + newPosition);
                break;
            case R.id.tv_short_video_praise:
                Log.e("play", "setPlay: tv_short_video_praise：" + newPosition);
                break;
            case R.id.btn_short_video_look_again:
                playAgain(position);
                break;
        }
    }

    private void playAgain(int position) {
        stopPVideo();
        newPosition = position + 1;
        View nowView = mLayoutManager.findViewByPosition(position + 1);
        videoRecycler.smoothScrollBy(0, nowView.getTop());
    }

    public void shareWnd() {
        if (sharePopupwindow == null) {
            sharePopupwindow = new SharePopupwindow(getContext());
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
        AbsShare share = ShareFactory.createShare(getContext(), platform);
        share.share(info);
    }
}

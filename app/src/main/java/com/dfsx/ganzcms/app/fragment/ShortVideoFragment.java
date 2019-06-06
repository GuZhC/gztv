package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.act.ShortVideoDetailActivity;
import com.dfsx.ganzcms.app.adapter.ShortVideoAdapter;
import com.dfsx.ganzcms.app.adapter.ShortVideoViewPagerAdapter;
import com.dfsx.ganzcms.app.model.ShortVideoBean;
import com.dfsx.videoijkplayer.VideoPlayView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_short_video, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoRecycler = (RecyclerView) view.findViewById(R.id.recycler_short_video);
        videoFullLayout = (FrameLayout) view.findViewById(R.id.fl_short_video_full);
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
        videoPlayer = new VideoPlayView(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoPlayer.setLayoutParams(params);
        videoPlayer.setFullGone();
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //TODO now it's visible to user
        } else {
            videoPlayer.pause();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoViewPagerAdapter.stopScroll();
        videoPlayer.pause();
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
//                Intent intent = new Intent(context,ShortVideoActivity.class);
//                View commView =  adapter.getViewByPosition(videoRecycler,position+1,R.id.tv_short_video_test);
//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(context,commView,"short_video").toBundle();
//                startActivity(intent,bundle);

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
                        videoViewPagerAdapter.startScroll();
                    } else {
                        videoViewPagerAdapter.stopScroll();
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

    private void stopPVideo() {
        if (isPlay) {
            videoPlayer.stop();
            isPlay = false;
            mVideoData.get(playPostion - 1).setVideo_state(ShortVideoAdapter.VIDEO_STOP);
            adapter.notifyItemChanged(playPostion);
//            Log.e("play", "setPlay: 暂停：" + playPostion);
        }

    }

    private void playVideo() {
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
        switch (view.getId()) {
            case R.id.tv_short_video_play:
                playAgain(position);
                break;
            case R.id.tv_short_video_comment:
                Log.e("play", "setPlay: tv_short_video_comment：" + newPosition);
                break;
            case R.id.tv_short_video_go_detail:
                Log.e("play", "setPlay: cb_short_video_sound：" + newPosition);
                break;
            case R.id.tv_short_video_share:
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
}

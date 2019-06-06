package com.dfsx.ganzcms.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.Util.MessageData;
import com.dfsx.core.rx.RxBus;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.MainTabActivity;
import com.dfsx.ganzcms.app.adapter.MyFragmentPagerAdapter;
import com.dfsx.ganzcms.app.business.ColumnBasicListManager;
import com.dfsx.ganzcms.app.business.ContentCmsApi;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.ganzcms.app.util.LSUtils;
import com.dfsx.ganzcms.app.util.MessageIntents;
import com.dfsx.lzcms.liveroom.view.adwareVIew.VideoAdwarePlayView;
import com.dfsx.videoijkplayer.VideoPlayView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.*;

/**
 * Created by heyang on 2016/10/11.
 */
public class DzLiveTvFragment extends Fragment {
    private Activity act;
    private FrameLayout videoContainer;
    private FrameLayout fullContainer;
    private LiveEntity.LiveChannel playedChannel;
    boolean isCreated = false;
    private Subscription tabItemSubscription;
    private Subscription addVideoSubscription;
    private Subscription channelSelectscription;
    private long colId = -1;
    ContentCmsApi mContentCmsApi = null;
    LinearLayout mWeeks;
    private final static int tabItemIndex = 2;
    private ViewPager _tvViewPager;
    private RadioButton _channelTxt;
    private RadioGroup _tvBarGroup;
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private boolean isInitChannel = false;
    private ImageView _tvVideoPlay;
    TvBackFragment backfrag;
    Fragment comemndfrag;
    Fragment chnnelfrag;

    public static DzLiveTvFragment newInstance(long type) {
        DzLiveTvFragment fragment = new DzLiveTvFragment();
        Bundle bundel = new Bundle();
        bundel.putLong("type", type);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_dzlive_video, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            colId = getArguments().getLong("type");
        }
        act = getActivity();
        mContentCmsApi = new ContentCmsApi(getActivity());
        if (colId == -1) {
            colId = ColumnBasicListManager.getInstance().findIdByName("tv");
        }
        initView(view);
        initAction();
    }

    private void initView(View v) {
        _tvVideoPlay = (ImageView) v.findViewById(R.id.live_yv_play_btn);
        _tvVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartPlayer();
            }
        });
        _tvBarGroup = (RadioGroup) v.findViewById(R.id.tv_operation_bar);
        _tvViewPager = (ViewPager) v.findViewById(R.id.tv_pager);
        _channelTxt = (RadioButton) v.findViewById(R.id.bar_channel);
//        _backPlayText = (RadioButton) v.findViewById(R.id.bar_back);
//        _commendTxt = (RadioButton) v.findViewById(R.id.bar_commend);
        mWeeks = (LinearLayout) v.findViewById(R.id.tv_week_bofy);
        videoContainer = (FrameLayout) v.findViewById(R.id.video_container);
        fullContainer = (FrameLayout) v.findViewById(R.id.full_screen_video_containerss);
//        liveChannelText = (TextView) v.findViewById(R.id.live_channel_name);
//        listView = (ListView) v.findViewById(R.id.tv_list);
//        hsvContent = (LinearLayout) v.findViewById(R.id.hsv_content);
//        horizontalScrollView = (HorizontalScrollView) v.findViewById(R.id.hsv_view);
//        radioGroup = (RadioGroup) v.findViewById(R.id.live_radio_bar);
//        radioGroup.setOnCheckedChangeListener(onRadlister);
        _tvBarGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.bar_channel:
                        _tvViewPager.setCurrentItem(0);
                        break;
                    case R.id.bar_back:
                        _tvViewPager.setCurrentItem(1);
                        break;
                    case R.id.bar_commend:
                        _tvViewPager.setCurrentItem(2);
                        break;
                }
            }
        });
//        initWeeksc();
        initTabs();
//        _tvBarGroup.check(R.id.bar_channel);
    }

    public void initTabs() {
//        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        chnnelfrag = TvChannelFragment.newInstance(colId);
        backfrag = TvBackFragment.newInstance(colId);
        comemndfrag = TvChatFragment.newInstance(0);
        fragments.add(chnnelfrag);
        fragments.add(backfrag);
        fragments.add(comemndfrag);
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        _tvViewPager.setAdapter(fragmentPagerAdapter);
        _tvViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        _tvViewPager.setCurrentItem(0);
//        setSelectedTextColor(0);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            setSelectedTextColor(position);
        }

        @Override
        public void onPageSelected(int position) {
            setSelectedTextColor(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void setSelectedTextColor(int selectedPosition) {
        switch (selectedPosition) {
            case 0:
                _tvBarGroup.check(R.id.bar_channel);
                break;
            case 1:
                _tvBarGroup.check(R.id.bar_back);
                break;
            case 2:
                _tvBarGroup.check(R.id.bar_commend);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "Radio_____onResume");
    }

    public int getCurrentIndex() {
        int position = -1;
        if (getParentFragment() != null) {
            position = ((HeadScrollFragment) getParentFragment()).getCurrentPos();
        }
        return position;
    }

    public void playBackItem(LiveEntity.LiveChannel channel) {
        if (channel != null) {
            if (playedChannel != null)
                playedChannel.url = channel.url;
//            playedChannel = channel;
            killPlayer();
            restartPlayer();
        }
    }

    public long getChannalId() {
        return playedChannel == null ? -1 : playedChannel.columnId;
    }

    private void initAction() {
        tabItemSubscription = RxBus.getInstance().
                toObserverable(TabItem.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TabItem>() {
                    @Override
                    public void call(TabItem intent) {
                        if (intent.getMsg().equals(TabItem.BOTTOM_TAB_TV)) {
                            pos = 2;
                            if (getCurrentIndex() == 0 && pos == tabItemIndex && playedChannel != null) {
//                                addVideoPlayerToContainer(getVideoPlyer());
//                                restartPlayer();
                            } else
                                killPlayer();
                        } else {
                            killPlayer();
                            if (backfrag != null)
                                backfrag.clearPlay();
                        }
                    }
                });


        addVideoSubscription = RxBus.getInstance().
                toObserverable(VideoAdwarePlayView.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VideoAdwarePlayView>() {
                    @Override
                    public void call(VideoAdwarePlayView videoView) {
                    addVideoPlayerToContainer(videoView);
                    }
                });

        channelSelectscription = RxBus.getInstance().
                toObserverable(MessageData.class).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MessageData>() {
                    @Override
                    public void call(MessageData messageData) {
                        if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_TV_ITEM_CREATE)) {
                            LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) messageData.getParam();
                            if (channel != null) {
                                if (getVideoPlyer()!=null && getVideoPlyer().isPlay()) return;
//                                if (playedChannel == null || playedChannel.channelID == 0
//                                        || playedChannel.columnId == 0) {
//                                    killPlayer();
//                                    restartPlayer();
                                isInitChannel = true;
                                _channelTxt.setText(channel.channelName);
                                playedChannel = channel;
//                                }
                            }
                        } else if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_TV_ITEM_SELECT)) {
                            LiveEntity.LiveChannel channel = (LiveEntity.LiveChannel) messageData.getParam();
                            if (channel != null) {
                                _channelTxt.setText(channel.channelName);
                                playedChannel = channel;
                                killPlayer();
                                restartPlayer();
                            }
                        } else if (TextUtils.equals(messageData.getMsgType(), MessageIntents.RX_ITME_TV_CLOSED)) {
                            killPlayer();
                            if (backfrag != null)
                                backfrag.clearPlay();
                        }
                    }
                });
    }

    int pos = -1;

    public VideoAdwarePlayView getVideoPlyer() {
        if (act instanceof MainTabActivity) {
            return ((MainTabActivity) act).getVideoPlayer();
        }
        return null;
    }

    /**
     * 设置video播放数据
     *
     * @param item
     */
    private void setVideoPlay(LiveEntity.LiveChannel item) {
        if (item != null && getCurrentIndex() == 0) {
            playedChannel = item;
            if (item.url == null || TextUtils.isEmpty(item.url)) {
                Toast.makeText(getContext(), "播放失败,地址无效!!", Toast.LENGTH_SHORT).show();
            } else {
                if (!getVideoPlyer().isPlay()) {
                    addVideoPlayerToContainer(getVideoPlyer());    //heyang  2017-2-15
                    getVideoPlyer().start(item.url, true,null);
                    _tvVideoPlay.setVisibility(View.GONE);
                    getVideoPlyer().setErrorListener(new VideoAdwarePlayView.OnErrorListener() {
                        @Override
                        public void errorListener(IMediaPlayer mp) {
                            _tvVideoPlay.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }
    }

    public void removeVideoPlayer() {
        if (getVideoPlyer() != null) {
            ViewGroup view = (ViewGroup) getVideoPlyer().getParent();
            if (view != null) {
                view.removeView(getVideoPlyer());
            }
        }
    }

    public void killPlayer() {
        if (getVideoPlyer() != null && getVideoPlyer().isPlay()) {
            getVideoPlyer().stop();
        }
        if (getVideoPlyer() != null) {
            getVideoPlyer().release();
            removeVideoPlayer();
        }
        _tvVideoPlay.setVisibility(View.VISIBLE);
    }

    public void restartPlayer() {
        if (playedChannel != null) {
//            addVideoPlayerToContainer(getVideoPlyer());    //heyang  2017-2-15
            setVideoPlay(playedChannel);
        } else {
            LSUtils.toastMsgFunction(getContext(), "视频源地址无效");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        killPlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
//        addVideoPlayerToContainer(getVideoPlyer());   2018/1/10
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isCreated) {
            return;
        }
        //        if (isVisibleToUser) {
        //            isUserVisible = true;
        //            addVideoPlayerToContainer(getVideoPlyer());
        //            getVideoPlyer().start("http://live.ysxtv.cn:8081/Ch1/playlist.m3u8");
        //        } else {
        //            isUserVisible = false;
        //            killPlayer();
        //        }
    }

    private void addVideoPlayerToContainer(VideoAdwarePlayView videoPlayer) {
        videoContainer.setVisibility(View.VISIBLE);
        fullContainer.setVisibility(View.GONE);
        if (videoContainer != null) {
            if (!(videoContainer.getChildAt(0) != null &&
                    videoContainer.getChildAt(0) instanceof VideoAdwarePlayView)) {
                removeVideoPlayer();
                videoContainer.addView(videoPlayer, 0);
            }
        }
    }

    public void addVideoPlayerToFullScreenContainer(VideoAdwarePlayView videoPlayer) {
        fullContainer.setVisibility(View.VISIBLE);
        videoContainer.setVisibility(View.GONE);
        if (fullContainer.getChildCount() <= 0 ||
                !(fullContainer.getChildAt(0) instanceof VideoAdwarePlayView)) {
            fullContainer.addView(videoPlayer, 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeVideoPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tabItemSubscription != null)
            tabItemSubscription.unsubscribe();

        if (channelSelectscription != null)
            channelSelectscription.unsubscribe();

        if (addVideoSubscription != null)
            addVideoSubscription.unsubscribe();

//        if (getVideoPlyer() != null) {
//            getVideoPlyer().onDestroy();
//        }
    }

}

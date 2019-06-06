package com.dfsx.lzcms.liveroom;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dfsx.core.common.view.CustomeProgressDialog;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.img.GlideImgManager;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.business.*;
import com.dfsx.lzcms.liveroom.fragment.LiveGuessFragment;
import com.dfsx.lzcms.liveroom.fragment.LiveServiceChatFragment;
import com.dfsx.lzcms.liveroom.fragment.LiveServiceInfoFragment;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.util.*;
import com.dfsx.lzcms.liveroom.view.*;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by liuwb on 2017/6/20.
 * 服务器创建的直播。 （图文直播）
 */
public class LiveServiceRoomActivity extends AbsLiveServiceVideoActivity implements SendGiftPopupwindow.OnClickEventListener,
        LiveServiceBottomBar.OnViewBtnClickListener {

    private static final int DP_MIN_MARGIN = 80;
    private static final int DP_MAX_MARGIN = 212;

    private View bottomView;
    private View topNoteIfoView;

    private ImageView openCloseVideoImageView;
    private FrameLayout videoContainer;
    private FrameLayout bottomDrawerView;
    private HorizontalScrollView changeBarScrollView;
    private CenterGroupChangeBar changeBar;
    private ImageView upDownImageView;
    private ViewPager viewPager;
    private LiveServiceBottomBar bottomBar;
    private ImageView backImage;
    private ImageView yuGaoImage;
    private View yuGaoView;
    private TextView yuGaoStartNoteText;

    private MyFragmentPagerAdapter fragmentAdapter;


    private boolean isVideoLayoutOpen;
    private int animLayoutHeight;
    private int bottomDrawerViewHeight;

    private CustomeProgressDialog lodingProgressDialog;
    private SharePopupwindow popupwindow;
    private Handler handler = new Handler();

    private LiveChannelManager channelManager;

    private ArrayList<IMultilineVideo> multilineVideoList;

    private String[] barTextArray;

    /**
     * 正在播放的线路
     */
    private IMultilineVideo playingMultilineVideo;

    private LiveServiceDetailsInfo serviceDetailsInfo;

    private ArrayList<Fragment> pageFragmentList;

    private SendGiftPopupwindow sendGiftPopupwindow;

    /**
     * pluginName 和Fragment的对应关系
     */
    private HashMap<String, Fragment> pluginFragmentMap = new HashMap<>();

    private IsLoginCheck loginCheck;

    private SendGift giftSender;

    /**
     * 记录是否获取到了直播开始的消息
     */
    private LiveStartMessage liveStartMessage;

    private KeyboardChangeListener keyboardChangeListener;

    /**
     * 是否经历了键盘改变过View
     */
    private boolean isKeyboardChangeView;

    /**
     * 是否可以聊天
     */
    private boolean isCouldChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_live_service_room);
        channelManager = new LiveChannelManager(activity);
        initView();
        initAction();

        loginCheck = new IsLoginCheck(activity);

        lodingProgressDialog = CustomeProgressDialog.show(activity, "加载中...");
        getEventLiveDetailsInfo();
        addKeyboardChangeListener();
    }

    private void addKeyboardChangeListener() {
        keyboardChangeListener = new KeyboardChangeListener(this);
        keyboardChangeListener.setKeyBoardListener(new KeyboardChangeListener.KeyBoardListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                if (!isShow) {//没有显示
                    isKeyboardChangeView = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetBottomDrawerViewHeight();
                        }
                    }, 50);

                }
            }
        });
    }

    public void resetBottomDrawerViewHeight() {
        if (bottomDrawerViewHeight != 0) {
            setBottomDrawerViewHeight(bottomDrawerViewHeight);
        }
    }

    private void initView() {
        yuGaoStartNoteText = (TextView) findViewById(R.id.live_start_text);
        yuGaoView = findViewById(R.id.live_yugao_view_layout);
        backImage = (ImageView) findViewById(R.id.image_back);
        yuGaoImage = (ImageView) findViewById(R.id.video_yugao_image);
        bottomView = findViewById(R.id.bottom_view);
        topNoteIfoView = findViewById(R.id.top_note_info_iew);
        openCloseVideoImageView = (ImageView) findViewById(R.id.image_see_video);
        videoContainer = (FrameLayout) findViewById(R.id.video_container);
        bottomDrawerView = (FrameLayout) findViewById(R.id.bottom_drawer);
        changeBarScrollView = (HorizontalScrollView) findViewById(R.id.change_bar_scroll_view);
        changeBar = (CenterGroupChangeBar) findViewById(R.id.center_change_bar_view);
        upDownImageView = (ImageView) findViewById(R.id.up_down_image);
        viewPager = (ViewPager) findViewById(R.id.live_info_view_pager);
        bottomBar = (LiveServiceBottomBar) findViewById(R.id.live_bottom_bar);

        setYuGaoViewVisible(false);
    }

    private void setYuGaoViewVisible(boolean isVisible) {
        yuGaoView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        openCloseVideoImageView.setVisibility(isVisible || isVideoLayoutOpen ? View.GONE : View.VISIBLE);
        upDownImageView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    public LiveServiceDetailsInfo getServiceDetailsInfo() {
        return serviceDetailsInfo;
    }

    private void initData(LiveServiceDetailsInfo serviceDetailsInfo) {
        this.serviceDetailsInfo = serviceDetailsInfo;
        roomTitle = serviceDetailsInfo.getTitle();
        roomUserId = serviceDetailsInfo.getOwnerId();
        if (liveStartMessage != null) {
            //尝试设置线路的播放地址(加入房间和请求数据是同时异步的请求)
            if (liveStartMessage != null && liveStartMessage.getLivetreamList() != null &&
                    !liveStartMessage.getLivetreamList().isEmpty()) {
                for (LiveOutputStreamInfo stream :
                        liveStartMessage.getLivetreamList()) {
                    IMultilineVideo multilineVideo = getMultilineVideoById(stream.getId());
                    if (multilineVideo != null) {
                        multilineVideo.setVideoUrlList(stream.getVideoUrlList());
                        autoControllVideoPlay();
                    }
                }
            }
        }
        //设置插件的显示
        String interaction_plugins = serviceDetailsInfo.getInteractionPlugins();
        if (!TextUtils.isEmpty(interaction_plugins)) {
            String[] pluginArray = interaction_plugins.split(",");
            pageFragmentList = new ArrayList<>();
            ArrayList<String> barTextList = new ArrayList<>();
            boolean isHasImageText = false;
            isCouldChat = false;
            for (String pluginStr : pluginArray) {
                String fragmentName = LiveServicePluginManager.getPluginClassName(pluginStr);
                String pluginName = LiveServicePluginManager.getPluginName(pluginStr);
                if (TextUtils.equals(pluginStr, "image-text")) {
                    isHasImageText = true;
                }
                if (TextUtils.equals(pluginStr, "chat")) {
                    isCouldChat = true;
                }
                if (fragmentName != null) {
                    barTextList.add(pluginName);
                    Fragment fragment = createFragmentByFragmentClassName(fragmentName);
                    pluginFragmentMap.put(pluginStr, fragment);
                    if (fragment != null) {
                        pageFragmentList.add(fragment);
                    } else {
                        Log.e("TAG", "创建" + fragmentName + "插件失败");
                    }
                } else {
                    Log.w("TAG", "没有" + pluginStr + "对应的Fragment");
                }
            }
            //聊天主键一定会有。不受服务器控制。 服务器只控制是否能聊天
            LiveServiceChatFragment chatFragment = new LiveServiceChatFragment();
            pluginFragmentMap.put("chat", chatFragment);
            int chatIndex = isHasImageText ? 1 : 0;
            barTextList.add(chatIndex, "聊天");
            pageFragmentList.add(chatIndex, chatFragment);
            setCouldChat(isCouldChat, true);

            barTextArray = barTextList.toArray(new String[0]);
            changeBar.setBarTextArray(0, barTextArray);
            fragmentAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                    pageFragmentList);
            viewPager.setAdapter(fragmentAdapter);
        }

    }

    public boolean isCouldChat() {
        return isCouldChat;
    }

    public void setCouldChat(boolean isCouldChat, boolean isUpdateChatFrag) {
        this.isCouldChat = isCouldChat;
        if (bottomBar != null) {
            bottomBar.setCouldSendChat(isCouldChat);
        }
        if (getChatFragment() != null && isUpdateChatFrag) {
            getChatFragment().setCouldChat(isCouldChat);
        }
    }

    private void setYuGaoViewData(LiveServiceDetailsInfo data) {
        if (data != null) {
            GlideImgManager.getInstance().showImg(activity,
                    yuGaoImage, data.getCoverUrl());
            String timeText = StringUtil.getTimeFurtureText(data.getPlanStartTime());
            String startText = String.format("本次直播将于%s开始", timeText);
            yuGaoStartNoteText.setText(startText);
        }
    }


    private Fragment createFragmentByFragmentClassName(String fragmentName) {
        try {
            Constructor<Fragment>[] constructors = (Constructor<Fragment>[])
                    Class.forName(fragmentName).getConstructors();
            Constructor<Fragment> constructorFrag = constructors[0];
            return constructorFrag.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onGetEventDetailsSuccess() {
        LiveServiceInfoFragment fragment = getImageTextFragment();
        if (fragment != null) {
            fragment.onGetEventDetailsSuccess();
        }
    }

    private void getEventLiveDetailsInfo() {
        channelManager.getLiveServiceDetailsInfo(roomId, new DataRequest.DataCallback<LiveServiceDetailsInfo>() {
            @Override
            public void onSuccess(boolean isAppend, LiveServiceDetailsInfo data) {
                onGetEventDetailsSuccess();
                if (data != null) {
                    initData(data);
                    int state = data.getState();
                    boolean isLive = state == 2;
                    if (videoPlayer != null) {
                        videoPlayer.setLiving(isLive);
                        videoPlayer.setVideoController(!isLive);
                        videoPlayer.setMultilineClickable(isVideoLayoutOpen);
                        videoPlayer.setVideoTitle(data.getTitle());
                    }
                    if (state == 3/* && data.getPlaybackState() == 2*/) {//表示可以回放
                        getServicePlayBackInfo();
                    } else if (state == 1) {//预告
                        setYuGaoViewVisible(true);
                        setYuGaoViewData(data);
                    }
                    if (state == 1 || state == 2) {//直播和预告都直接设置线路信息
                        if (data.getMultilineList() != null && !data.getMultilineList().isEmpty()) {
                            ArrayList<IMultilineVideo> multilineVideos = new ArrayList<IMultilineVideo>();
                            for (LiveServiceDetailsInfo.LiveServiceMultiline line : data.getMultilineList()) {
                                multilineVideos.add(line);
                            }
                            videoPlayer.setMultilineList(multilineVideos);
                            multilineVideoList = multilineVideos;
                        }
                    }
                } else {
                    videoPlayer.setVideoController(false);
                }
                lodingProgressDialog.dismiss();
            }

            @Override
            public void onFail(ApiException e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                lodingProgressDialog.dismiss();
            }
        });
    }

    private void getServicePlayBackInfo() {
        channelManager.getLiveServicePlayBackInfo(roomId, new DataRequest.DataCallback<LiveServicePlayBackInfo>() {
            @Override
            public void onSuccess(boolean isAppend, LiveServicePlayBackInfo data) {
                if (data != null && data.getMultilineVideoInfoList() != null &&
                        data.getMultilineVideoInfoList().size() > 0) {
                    List<LiveServiceMultilineVideoInfo> liveServiceMultilineVideoInfos = data.getMultilineVideoInfoList();
                    ArrayList<IMultilineVideo> list = new ArrayList<IMultilineVideo>();
                    for (LiveServiceMultilineVideoInfo videoInfo : liveServiceMultilineVideoInfos) {
                        list.add(videoInfo);
                    }
                    videoPlayer.setMultilineList(list);
                    multilineVideoList = list;
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Toast.makeText(activity, "回去回放信息失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLiveOutputStreamStartMessage(LiveOutputStreamStartMessage message) {
        super.onLiveOutputStreamStartMessage(message);
        if (message != null && message.getOutputStreamInfo() != null) {
            IMultilineVideo multilineVideo = getMultilineVideoById(message.getOutputStreamInfo().getId());
            if (multilineVideo != null) {
                multilineVideo.setVideoUrlList(message.getOutputStreamInfo().getVideoUrlList());
            }
            //更新video里面的线路
            IMultilineVideo videoMultilineVideo = getVideoMultilineVideoById(message.getOutputStreamInfo().getId());
            if (videoMultilineVideo != null) {
                videoMultilineVideo.setVideoUrlList(message.getOutputStreamInfo().getVideoUrlList());
                videoPlayer.updateMultilineView();
            }
            autoControllVideoPlay();
        }
    }

    @Override
    public void onUserJoinRoomMessage(UserMessage message) {
        super.onUserJoinRoomMessage(message);
    }

    @Override
    public void onImageAndTextMessage(ImageTextMessage message) {
        super.onImageAndTextMessage(message);
        LiveServiceInfoFragment fragment = getImageTextFragment();
        if (message != null && fragment != null) {
            fragment.processTextImageMessage(message);
        }
    }

    /**
     * 获取当前页面的图文直播页面
     *
     * @return
     */
    private LiveServiceInfoFragment getImageTextFragment() {
        Fragment fragment = pluginFragmentMap.get("image-text");
        if (fragment != null &&
                fragment instanceof LiveServiceInfoFragment) {
            return (LiveServiceInfoFragment) fragment;
        }
        return null;
    }

    private Timer timer;

    /**
     * 大数据测试
     */
    private void startTestLargeMessage() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long systemTime = System.currentTimeMillis();
                sendChatMessageMainThread(ChatMessage.getTextMessage("test--" + systemTime), null);
            }
        }, 300, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (keyboardChangeListener != null) {
            keyboardChangeListener.destroy();
        }
    }

    private void initAction() {
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bottomBar.setOnViewBtnClickListener(this);

        bottomBar.setOnViewShowTypeChangeListener(new LiveServiceBottomBar.OnViewShowTypeChangeListener() {
            @Override
            public void onShowTypeChange(int type) {
                if (LiveServiceBottomBar.TYPE_EDIT == type) {
                    Fragment fragment = pluginFragmentMap.get("chat");
                    int pos = getFragmentPosition(fragment);
                    if (pos >= 0 && pos < pageFragmentList.size()) {
                        viewPager.setCurrentItem(pos, true);
                    }
                }
            }
        });
        videoPlayer.setOnLiveServiceVideoEventClickListener(new OnLiveServiceVideoEventClickListener() {
            @Override
            public void onFinishClick(View v) {
                finish();
            }

            @Override
            public void onMoreClick(View v) {
                showMorePop(v);
            }
        });
        videoPlayer.setOnVideoMultilinePlayChangeListener(new OnVideoMultilinePlayChangeListener() {
            @Override
            public void onVideoMultilinePlayChanged(IMultilineVideo multilineVideo) {
                playingMultilineVideo = multilineVideo;
            }
        });
        openCloseVideoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoLayoutOpen) {
                    closeAnim();
                } else {
                    openAnim();
                }
            }
        });

        changeBar.setOnBarSelectedChangeListener(new CenterGroupChangeBar.OnBarSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedIndex) {
                int pageCount = viewPager.getCurrentItem();
                if (pageCount != selectedIndex) {
                    viewPager.setCurrentItem(selectedIndex);
                }
            }
        });
        bottomDrawerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (animLayoutHeight == 0) {
                    animLayoutHeight = bottom - top;
                    bottomDrawerViewHeight = animLayoutHeight;
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeBar.setCheckIndex(position);
                int left = changeBar.getItemLeft(position - 1);
                if(left >= 0) {
                    changeBarScrollView.smoothScrollTo(left, 0);
                }
                onPageFragmentChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        upDownImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoLayoutOpen) {
                    closeAnim();
                    upDownImageView.setImageResource(R.drawable.icon_down_black);
                } else {
                    openAnim();
                    upDownImageView.setImageResource(R.drawable.icon_up_black);
                }
            }
        });
    }

    protected void onPageFragmentChange(int pagePosition) {
        if (fragmentAdapter != null) {
            Fragment fragment = fragmentAdapter.getItem(pagePosition);
            //显示聊天页面的聊天输入控件
            if (fragment != null && fragment instanceof LiveServiceChatFragment) {
                setChatBottombarVisiable(true);
            } else {
                setChatBottombarVisiable(false);
            }
            if (isKeyboardChangeView && fragment instanceof LiveGuessFragment) {
                isKeyboardChangeView = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setBottomDrawerViewHeight(bottomDrawerViewHeight);
                    }
                }, 100);
            }
        }
    }

    private int getFragmentPosition(Fragment fragment) {
        int pos = -1;
        if (pageFragmentList != null) {
            for (int i = 0; i < pageFragmentList.size(); i++) {
                if (pageFragmentList.get(i) == fragment) {
                    return i;
                }
            }
        }
        return pos;
    }

    protected void sendGiftClick(View v) {
        if (sendGiftPopupwindow == null) {
            sendGiftPopupwindow = new SendGiftPopupwindow(activity);
            sendGiftPopupwindow.setOnClickEventListener(this);
        }
        sendGiftPopupwindow.show(v);
    }

    public void setChatBottombarVisiable(boolean isVisible) {
        if (bottomView != null) {
            bottomView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void showMorePop(View v) {
        if (popupwindow == null) {
            popupwindow = new SharePopupwindow(this);
            popupwindow.setOnShareClickListener(new SharePopupwindow.OnShareClickListener() {
                @Override
                public void onShareClick(View v) {
                    if (popupwindow != null && popupwindow.getSharePlatform(v) != null) {
                        onSharePlatform(popupwindow.getSharePlatform(v));
                    }
                }
            });
        }
        if (!isFinishing()) {
            popupwindow.show(v);
        }
    }

    /**
     * 主要是在接受到消息的时候启动一下视频播放器。
     * 具体是否播放按播放器的判断来控制
     */
    private void autoControllVideoPlay() {
        if (isVideoLayoutOpen) {
            onRequestVideoStartAndPause(isVideoLayoutOpen);
        }
    }

    protected void onRequestVideoStartAndPause(boolean isOpen) {
        if (isOpen && multilineVideoList != null && !multilineVideoList.isEmpty()) {
            if (playingMultilineVideo == null) {
                playingMultilineVideo = getCouldPlayMultilineVideo();
                if (playingMultilineVideo != null) {
                    videoPlayer.start(playingMultilineVideo);
                }
            } else if (videoPlayer.isInPlayBackStatus()) {
                videoPlayer.start();
            }
        } else {
            if (videoPlayer.isPlaying()) {
                videoPlayer.pause();
            } else {
                videoPlayer.release();
            }
        }
        videoPlayer.setMultilineClickable(isOpen);
    }

    /**
     * 获取可以播放的一条线路
     * <p>
     * 以列表的顺序优先选者
     *
     * @return
     */
    private IMultilineVideo getCouldPlayMultilineVideo() {
        if (multilineVideoList != null) {
            for (IMultilineVideo video : multilineVideoList) {
                if (video.getVideoUrlList() != null && !video.getVideoUrlList().isEmpty()) {
                    return video;
                }
            }
        }
        return null;
    }

    private void onSharePlatform(SharePlatform platform) {
        if (serviceDetailsInfo != null) {
            ShareContent content = new ShareContent();
            content.title = getRoomTitle();
            content.disc = getRoomTitle();
            content.thumb = serviceDetailsInfo.getCoverUrl();
            content.type = ShareContent.UrlType.WebPage;
            content.url = AppManager.getInstance().getIApp().getMobileWebUrl() + "/live/activity/" + getShowId();
            AbsShare share = ShareFactory.createShare(activity, platform);
            share.share(content);
        }

    }

    protected void openAnim() {
        isVideoLayoutOpen = true;
        startAnim(isVideoLayoutOpen, DP_MIN_MARGIN, DP_MAX_MARGIN);
    }

    protected void closeAnim() {
        isVideoLayoutOpen = false;
        startAnim(isVideoLayoutOpen, DP_MAX_MARGIN, DP_MIN_MARGIN);
    }

    private void setBottomDrawerViewHeight(int height) {
        ViewGroup.LayoutParams p = bottomDrawerView.getLayoutParams();
        if (p != null) {
            p.height = height;
            bottomDrawerView.setLayoutParams(p);
        }
    }

    private void startAnim(final boolean isOpen, final int startDP, final int endDP) {
        float startY = isOpen ? 0 : Math.abs(PixelUtil.dp2px(this, endDP - startDP));
        float endY = isOpen ? Math.abs(PixelUtil.dp2px(this, endDP - startDP)) : 0;
        ObjectAnimator animator = ObjectAnimator.ofFloat(bottomDrawerView, "translationY",
                startY, endY)
                .setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                bottomDrawerViewHeight = (animLayoutHeight - PixelUtil.dp2px(LiveServiceRoomActivity.this, endDP - startDP));
                setBottomDrawerViewHeight(bottomDrawerViewHeight);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                openCloseVideoImageView.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                onRequestVideoStartAndPause(isOpen);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public void onVideoSwitchScreen(LiveServiceVideoPlayer videoPlayer, boolean isFullScreen) {
        int height = !isFullScreen ? PixelUtil.dp2px(activity, DP_MAX_MARGIN) :
                ViewGroup.LayoutParams.MATCH_PARENT;
        int visiable = isFullScreen ? View.GONE : View.VISIBLE;
        bottomBar.setVisibility(visiable);
        topNoteIfoView.setVisibility(visiable);
        bottomDrawerView.setVisibility(visiable);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    height);
        } else {
            params.height = height;
        }
        videoContainer.setLayoutParams(params);

    }

    @Override
    public void addVideoPlayerToContainer(LiveServiceVideoPlayer videoPlayer) {
        videoContainer.removeAllViews();
        videoContainer.addView(videoPlayer);
    }

    private LiveServiceChatFragment getChatFragment() {
        Fragment f = pluginFragmentMap.get("chat");
        if (f != null && f instanceof LiveServiceChatFragment) {
            LiveServiceChatFragment chatFragment = (LiveServiceChatFragment) f;
            return chatFragment;
        }
        return null;
    }

    @Override
    protected void onReceiveUserChatMessage(List<LiveMessage> messageList) {
        super.onReceiveUserChatMessage(messageList);
        LiveServiceChatFragment f = getChatFragment();
        if (f != null) {
            f.onProcessMessage(messageList);
        }
    }

    @Override
    public void onLiveStartMessage(LiveStartMessage message) {
        super.onLiveStartMessage(message);
        this.liveStartMessage = message;
        if (serviceDetailsInfo != null && message != null && message.getLivetreamList() != null &&
                !message.getLivetreamList().isEmpty()) {
            for (LiveOutputStreamInfo stream : message.getLivetreamList()) {
                IMultilineVideo multilineVideo = getMultilineVideoById(stream.getId());
                if (multilineVideo != null) {
                    multilineVideo.setVideoUrlList(stream.getVideoUrlList());
                }
                IMultilineVideo videoMultilineVideo = getVideoMultilineVideoById(stream.getId());
                if (videoMultilineVideo != null) {
                    videoMultilineVideo.setVideoUrlList(stream.getVideoUrlList());
                }
            }
            videoPlayer.updateMultilineView();
            autoControllVideoPlay();
        }
        //只要直播开始，就结束了预告的状态
        setYuGaoViewVisible(false);
        if (serviceDetailsInfo != null) {
            serviceDetailsInfo.setState(2);
        }
    }

    private IMultilineVideo getVideoMultilineVideoById(long id) {
        if (videoPlayer == null) {
            return null;
        }
        List<IMultilineVideo> videoList = videoPlayer.getMultilineVideoList();
        if (videoList != null) {
            for (IMultilineVideo video : videoList) {
                if (id == video.getId()) {
                    return video;
                }
            }
        }
        return null;
    }

    private IMultilineVideo getMultilineVideoById(long id) {
        if (multilineVideoList != null) {
            for (IMultilineVideo video : multilineVideoList) {
                if (id == video.getId()) {
                    return video;
                }
            }
        }
        return null;
    }

    @Override
    public void onLiveOutputStreamEndMessage(LiveOutputStreamEndMessage message) {
        super.onLiveOutputStreamEndMessage(message);
        if (message != null && message.getStreamNameInfo() != null) {
            if (playingMultilineVideo != null && TextUtils.equals(playingMultilineVideo.getName(),
                    message.getStreamNameInfo().getName())) {
                videoPlayer.stop();
                playingMultilineVideo = null;
            } else {
                Toast.makeText(activity,
                        message.getStreamNameInfo().getName() + "已停止",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLiveEndMessage(LiveEndMessage message) {
        super.onLiveEndMessage(message);
        videoPlayer.stop();
        playingMultilineVideo = null;
    }

    @Override
    public void onBetMessage(BetGuessMessage message) {
        super.onBetMessage(message);
        Fragment fragment = pluginFragmentMap.get("quiz");
        if (fragment instanceof LiveGuessFragment) {
            ((LiveGuessFragment) fragment).doReceiveBetMessage(message);
        }
    }

    public int getBottomDrawerViewHeight() {
        return bottomDrawerViewHeight;
    }

    @Override
    public void onGuessResultMessage(GuessResultMessage message) {
        super.onGuessResultMessage(message);
        Log.e("TAG", "谁获得了胜利" + message.getResultOptionId());
    }

    @Override
    public void onNoTalkUserMessage(LiveNoTalkMessage message) {
        super.onNoTalkUserMessage(message);
        if (message != null && message.getTimestamp() != 0) {
            pushChatMessageQueue(message);
            setCouldChat(false, false);
        }
    }

    @Override
    public void onAllowUserTalkMessage(LiveUserAllowTalkMessage message) {
        super.onAllowUserTalkMessage(message);
        if (message != null && message.getTimestamp() != 0) {
            pushChatMessageQueue(message);
        }
        setCouldChat(true, false);
    }

    @Override
    public void onRoomNoTalkMessage(LiveRoomNoTalkMessage message) {
        super.onRoomNoTalkMessage(message);
        if (message != null && message.getTimestamp() != 0) {
            pushChatMessageQueue(message);
            setCouldChat(false, false);
        }
    }

    @Override
    public void onRoomAllowTalkMessage(LiveRoomCancelNoTalkMessage message) {
        super.onRoomAllowTalkMessage(message);
        if (message != null && message.getTimestamp() != 0) {
            pushChatMessageQueue(message);
            setCouldChat(true, false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lodingProgressDialog != null) {
            lodingProgressDialog.dismiss();
        }
    }

    @Override
    protected void onReceiveUserGiftMessage(List<GiftMessage> messageList) {
        super.onReceiveUserGiftMessage(messageList);
        LiveServiceChatFragment f = getChatFragment();
        if (f != null) {
            f.onReceiveGiftMessage(messageList);
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomBar.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onBuyGoldClick() {
        IntentUtil.goAddMoneyActivity(activity);
    }

    @Override
    public void onSendGiftClick(final GiftModel gift, final int num, boolean isManySend) {
        if (!loginCheck.checkLogin() || gift == null) {
            return;
        }
        if (MyMoneyInfoManager.getInstance().getCacheMoneyInfo() != null
                &&
                MyMoneyInfoManager.getInstance().getCacheMoneyInfo().getCoins() <
                        gift.getPrice() * num) {
            LSLiveUtils.showNoEnoughMoneyDialog(activity);
            return;
        }
        if (giftSender == null) {
            giftSender = new LiveRoomHttpSendGift(activity, getRoomId(), getRoomEnterId());
        }
        giftSender.sendGift(gift, num, getRoomName(), new ICallBack<GiftResponseInfo>() {
            @Override
            public void callBack(GiftResponseInfo data) {
                onGiftSendResponse(data, gift, num);
            }
        });
    }


    private void onGiftSendResponse(GiftResponseInfo data, GiftModel gift, int num) {
        boolean isSuccess = data.isSuccess();
        if (!isSuccess) {
            String giftResponseText = data.getErrorMsg();
            if (data.isNoEnoughMoney()) {
                LSLiveUtils.showNoEnoughMoneyDialog(activity);
            } else {
                Toast.makeText(activity, giftResponseText,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            double payCount = gift.getPrice() * num;
            MyMoneyInfoManager.getInstance().payMoney(payCount);
            sendGiftPopupwindow.updateMoneyText();
        }
    }

    @Override
    public void onSendGiftClick(View v) {
        sendGiftClick(v);
    }

    @Override
    public void onSendTextClick(View v, String text) {
        if (loginCheck.checkLogin()) {
            sendChatMessageMainThread(ChatMessage.getTextMessage(text), null);
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        //        @Override
        //        public void destroyItem(ViewGroup container, int position, Object object) {
        //            Fragment fragment = (Fragment) object;
        //            fm.beginTransaction().hide(fragment);
        //        }
    }
}

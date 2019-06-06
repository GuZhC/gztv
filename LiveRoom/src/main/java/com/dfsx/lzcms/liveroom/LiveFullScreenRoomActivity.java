package com.dfsx.lzcms.liveroom;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.fragment.LiveInfoFrag;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.view.ILiveVideoController;
import com.dfsx.lzcms.liveroom.view.LXDialog;
import com.dfsx.lzcms.liveroom.view.LiveFullRoomVideoPlayer;
import com.dfsx.lzcms.liveroom.view.LiveLandscapeVideoController;
import com.dfsx.videoijkplayer.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.List;

public class LiveFullScreenRoomActivity extends DrawerActivity {
    protected LiveInfoFrag drawerFrag;
    //    private EMVideoView emVideoView;
    protected LiveFullRoomVideoPlayer player;

    public static final String KEY_FULL_SCREEN_VIDEO_IMAGE = "com.dfsx.lzcms.liveroom.LiveFullScreenRoomActivity.video-thumb-image";

    private String videoImage;

    private FullScreenRoomIntentData intentData;

    protected ILiveVideoController landscapeVideoController;

    private boolean isYuGaoLive;
    private long startTimestamp;
    private boolean isLandVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockDrawerClose();
        intentData = (FullScreenRoomIntentData) getIntentSerializableData();
        if (intentData != null) {
            videoImage = intentData.getFullScreenVideoImagePath();
            isYuGaoLive = intentData.isYuGaoLive();
            startTimestamp = intentData.getStartTimestamp();
            isLandVideo = intentData.isLandVideo();
        }
        if (!TextUtils.isEmpty(videoImage)) {
            player.setVideoThumbImage(videoImage);
        }
        //        videoImage = getIntent().getStringExtra(KEY_FULL_SCREEN_VIDEO_IMAGE);
        setIntentDataView();
        getPersonalRoomInfo();

        player.setOnVideoViewSizeSetListener(new IjkVideoView.OnVideoSurfaceViewSizeSetListener() {
            @Override
            public void onVideoSurfaceSize(int width, int height) {
                if (drawerFrag != null) {
                    drawerFrag.updateSwitchVideoImagePosition(height);
                }
            }
        });
    }

    private void setIntentDataView() {
        if (isYuGaoLive) {
            player.setVideoYuGaoView(isYuGaoLive, startTimestamp);
        }
        if (isLandVideo) {
            player.setVideoShowLand();
        }
    }

    @Override
    protected void setDrawer(int id) {
        super.setDrawer(id);
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        drawerFrag = (LiveInfoFrag) fragmentManager.findFragmentByTag("drawer_frag");
        if (drawerFrag == null) {
            drawerFrag = new LiveInfoFrag();
            transaction.add(id, drawerFrag, "drawer_frag");
        } else {
            transaction.show(drawerFrag);
        }
        transaction.commit();
    }

    @Override
    protected void setMainContent(FrameLayout v) {
        super.setMainContent(v);
        Log.e("TAG", "set video ---");
        v.setBackgroundColor(Color.BLACK);
        useLiveIjkPlayer(v);
    }

    @Override
    public void onUpdateOwnerInfo(LivePersonalRoomDetailsInfo owner) {
        super.onUpdateOwnerInfo(owner);
        if (drawerFrag != null) {
            drawerFrag.updateOwner(owner);
        }
    }

    @Override
    public void onUpdateMemberView(int memberSize, List<ChatMember> memberList) {
        if (drawerFrag != null) {
            drawerFrag.updateRoomMember(memberSize, memberList);
        }
    }

    @Override
    protected void onReceiveUserChatMessage(List<LiveMessage> messageList) {
        super.onReceiveUserChatMessage(messageList);
        if (drawerFrag != null) {
            drawerFrag.processChatMessage(messageList);
        }
    }

    @Override
    protected void onReceiveUserGiftMessage(List<GiftMessage> message) {
        super.onReceiveUserGiftMessage(message);
        if (drawerFrag != null) {
            drawerFrag.doReceiveGift(message);
        }
    }

    @Override
    public void onLiveStartMessage(LiveStartMessage message) {
        if (drawerFrag != null) {
            drawerFrag.doReceiveLiveStreamInfo();
        }
        String url = "http://live.ysxtv.cn:8081/Ch1/playlist.m3u8";
        if (message != null && message.getLivetreamList() != null &&
                message.getLivetreamList().size() > 0 &&
                !TextUtils.isEmpty(message.getLivetreamList().get(0).getFlvAddress())) {
            url = message.getLivetreamList().get(0).getFlvAddress();
        }
        if (!player.isPlaying()) {
            player.start(url);
        }

    }

    @Override
    public void onLiveEndMessage(LiveEndMessage message) {
        super.onLiveEndMessage(message);
        player.stop();
        player.showNoteText("直播已停止");
    }

    @Override
    public void onUserJoinRoomMessage(UserMessage message) {
        super.onUserJoinRoomMessage(message);
        if (drawerFrag != null && message != null && message.isSpecial()) {
            drawerFrag.doUserJoinRoom(message);
        }
    }

    @Override
    public void onBanUserMessage(LiveBanUserMessage message) {
        if (isLogin() && message != null && message.getUserId() == AppManager.getInstance().getIApp().getLoginUserId()) {
            if (context != null && !context.isFinishing()) {
                LXDialog dialog = new LXDialog.Builder(context)
                        .isEditMode(false)
                        .isHiddenCancleButton(true)
                        .setMessage("你被踢出房间")
                        .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                            @Override
                            public void onClick(DialogInterface dialog, View v) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .create();
                dialog.show();
            }
            disconnectChatRoom();
            player.stop();
        }
    }

    @Override
    public void onNoTalkUserMessage(LiveNoTalkMessage message) {
        if (isLogin() && message != null &&
                message.getUserId() == AppManager.getInstance().getIApp().getLoginUserId()) {
            int minutes = message.getExpired();
            toastDialog("你被禁言" + minutes + "分钟");

            if (drawerFrag != null) {
                drawerFrag.setUserNoTalk(System.currentTimeMillis() + minutes * 60 * 1000);
            }
        }
    }

    @Override
    public void onAllowUserTalkMessage(LiveUserAllowTalkMessage message) {
        if (isLogin() && message != null &&
                message.getUserId() == AppManager.getInstance().getIApp().getLoginUserId()) {
            toast("禁言已取消，你可以发言了");
            if (drawerFrag != null) {
                drawerFrag.cancelNoTalk();
            }
        }
    }

    private void useLiveIjkPlayer(FrameLayout v) {
        player = new LiveFullRoomVideoPlayer(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        player.setLayoutParams(params);
        v.addView(player);

        //        String testUrl = "http://live.ysxtv.cn:8081/Ch1/playlist.m3u8";
        //        String testUrl = "http://channellive.dfsxcms.cn:80/live/flh1.flv";
        //        player.start(testUrl);


        player.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                //                if (iMediaPlayer.getVideoWidth() > iMediaPlayer.getVideoHeight()) {
                //                    player.setVideoRotation(90, true);
                //                }
                onVideoPrepared();
            }
        });

        player.setOnVideoPositionListener(new LiveFullRoomVideoPlayer.OnVideoPositionListener() {
            @Override
            public void onScreenTop() {
                drawerFrag.setTopVideoSwitchToFullImageVisiable(true);
            }

            @Override
            public void onFullScreen() {
                drawerFrag.setTopVideoSwitchToFullImageVisiable(false);
            }

            @Override
            public void onSwitchFullScreen() {

            }
        });
    }

    protected void onVideoPrepared() {
        openDrawer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        player.onConfigChange(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (landscapeVideoController == null) {
                landscapeVideoController = onCreateLandscapeVideoController();
            }
            player.setLiveFullRoomVideoControllerView(landscapeVideoController);
        } else {
            player.setLiveFullRoomVideoControllerView(null);
        }
    }

    @Override
    protected void onCurrentUserNoTalk(long expiredTime) {
        super.onCurrentUserNoTalk(expiredTime);
        if (drawerFrag != null) {
            drawerFrag.setUserNoTalk(expiredTime);
        }
    }

    @Override
    protected void onConfigurationChangedResetContentViewSize(Configuration newConfig) {
        //这里是切换为全屏是设置播放器全屏播放。所以不能用activity的大小来设置。只能用屏幕的大小来设置
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
        setContentViewSize(width, height);
    }

    protected ILiveVideoController onCreateLandscapeVideoController() {
        LiveLandscapeVideoController liveLandscapeVideoController = new LiveLandscapeVideoController(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        liveLandscapeVideoController.setLayoutParams(params);
        return liveLandscapeVideoController;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLivePlay() && getResumePlayerStatus()) {
            player.start();
        }
    }

    protected boolean isLivePlay() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isLivePlay()) {
            clearVideoPlayStatus();
            if (player.isPlaying()) {
                saveVideoPlayStatus();
                player.pause();
            } else if (!player.isInPlaybackState()) {
                player.stop();
            }
        }
    }

    public void switchScreen() {
        if (player != null) {
            player.switchTopVideoScreen();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean getResumePlayerStatus() {
        String key = "Live_VIDEO_PLAY" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        boolean isPaly = sp.getBoolean(key, false);
        sp.edit().clear().commit();
        return isPaly;
    }

    private void saveVideoPlayStatus() {
        String key = "Live_VIDEO_PLAY" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        sp.edit().putBoolean(key, player.isPlaying()).commit();
    }

    private void clearVideoPlayStatus() {
        String key = "Live_VIDEO_PLAY" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        sp.edit().clear().commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
        }
    }

    public String getVideoImage() {
        return videoImage;
    }

    @Override
    protected void setMainContent(int id) {
        super.setMainContent(id);
    }

    @Override
    protected void onPersonalRoomInfoUpdate(LivePersonalRoomDetailsInfo roomDetailsInfo) {
        super.onPersonalRoomInfoUpdate(roomDetailsInfo);
        if (drawerFrag != null) {
            drawerFrag.doUpdateRoomChannelInfo(roomDetailsInfo);
            if (intentData != null &&
                    TextUtils.isEmpty(videoImage)) {
                videoImage = roomDetailsInfo.getCoverUrl();
                intentData.setFullScreenVideoImagePath(roomDetailsInfo.getCoverUrl());
                isYuGaoLive = roomDetailsInfo.getState() == 1;
                isLandVideo = roomDetailsInfo.getScreenMode() == 1;
                startTimestamp = roomDetailsInfo.getPlanStartTime();
                setIntentDataView();
            }
            drawerFrag.setVideoScreenMode(isLandVideo);
        }
    }

    private void toastDialog(final String message) {
        if (activity != null && !activity.isFinishing()) {
            LXDialog dialog = new LXDialog.Builder(activity)
                    .setMessage(message)
                    .isHiddenCancleButton(true)
                    .setPositiveButton("确定", new LXDialog.Builder.LXDialogInterface() {
                        @Override
                        public void onClick(DialogInterface dialog, View v) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            player.switchTopVideoScreen(activity);
            return;
        }
        super.onBackPressed();
    }
}

package com.dfsx.lzcms.liveroom;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.lzcms.liveroom.business.*;
import com.dfsx.lzcms.liveroom.fragment.LiveBackPlayInfoFrag;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.view.ILiveVideoController;
import com.dfsx.lzcms.liveroom.view.LiveBackPlayVideoController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liuwb on 2016/12/14.
 */
public class LiveBackPlayFullScreenRoomActivity extends LiveFullScreenRoomActivity {

    public static final int TYPE_LIVE_GUESS = 100;
    public static final int TYPE_LIVE_SHOW = 101;

    public static final String KEY_BACK_PLAY_ID = "LiveBackPlayFullScreenRoomActivity.backplay.id";
    public static final String KEY_OWNER_ID = "LiveBackPlayFullScreenRoomActivity.backplay.owner.id";
    public static final String KEY_OWNER_NAME = "LiveBackPlayFullScreenRoomActivity.backplay.owner.name";
    public static final String KEY_OWNER_NICK_NAME = "LiveBackPlayFullScreenRoomActivity.backplay.owner.nick.name";
    public static final String KEY_OWNER_LOGO = "LiveBackPlayFullScreenRoomActivity.backplay.owner.logo";
    public static final String KEY_ROOM_MEMBER_SIZE = "LiveBackPlayFullScreenRoomActivity.backplay.member.size";
    public static final String KEY_ROOM_TOTAL_COINS = "LiveBackPlayFullScreenRoomActivity.backplay.total.coins";

    public static final String KEY_LIVE_TYPE = "LiveBackPlayFullScreenRoomActivity.backplay.live.type";

    public static final String KEY_SHOW_ID = "LiveBackPlayFullScreenRoomActivity.backplay.live.show.id";


    private long backPlayId;

    private LiveBackPlayInfoFrag backPlayInfoFrag;

    private ChatMember ownerInfo;
    private long roomMemberSize;
    private double roomCoins;

    private long showId;

    /**
     * 回放的直播类型
     */
    private int liveType;

    private GuessRoomInfo guessRoomInfo;

    private long backPlayStartLogTime;

    private IGetChatMessage getChatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        doIntent();
        super.onCreate(savedInstanceState);
        setActOrientation(liveType == TYPE_LIVE_GUESS);
        getBackPlayVideoInfo();
        getChatData(0);

        if (drawerFrag instanceof LiveBackPlayInfoFrag) {
            backPlayInfoFrag = (LiveBackPlayInfoFrag) drawerFrag;
        }
        backPlayInfoFrag.setVideoPlayer(player);

        if (liveType == TYPE_LIVE_GUESS) {
            getGuessInfo();
            player.setIsForceUseFullScreenVideo(true);
        } else {
            player.setIsForceUseFullScreenVideo(false);
        }

        lockDrawerOpen();
    }

    private void doIntent() {
        BackPlayIntentData intentData = (BackPlayIntentData) getIntentSerializableData();
        backPlayId = intentData.getBackPlayId();
        setDataByIntent(intentData);

        //        if (showId == 0 ||
        //                intentData.getRoomOwnerId() == 0) {
        //            AppManager.getInstance().getIApp().getPlayBackManager()
        //                    .initBackPlayIntentData(backPlayId, intentData, new ICallBack<BackPlayIntentData>() {
        //                        @Override
        //                        public void callBack(BackPlayIntentData data) {
        //                            setDataByIntent(data);
        //                            drawerFrag.updateOwnerView(ownerInfo, roomMemberSize);
        //                        }
        //                    });
        //        }
    }

    @Override
    public void onPersonalRoomInfoUpdate(LivePersonalRoomDetailsInfo owner) {
        super.onPersonalRoomInfoUpdate(owner);
    }

    private void setDataByIntent(BackPlayIntentData intentData) {
        if (intentData != null) {
            roomMemberSize = intentData.getMemberSize();
            roomCoins = intentData.getRoomTotalCoins();
            liveType = intentData.getLiveType();
            showId = intentData.getShowId();


            ownerInfo = new ChatMember();
            ownerInfo.setUserId(intentData.getRoomOwnerId());
            ownerInfo.setUserName(intentData.getRoomOwnerAccountName());
            ownerInfo.setNickName(intentData.getRoomOwnerNickName());
            ownerInfo.setLogo(intentData.getRoomOwnerLogo());
        }
    }

    private void getGuessInfo() {
        if (showId != 0) {
            if (guessRoomInfo == null) {
                channelManager.getGuessRoomInfo(showId, showId + "", new DataRequest.DataCallback<GuessRoomInfo>() {
                    @Override
                    public void onSuccess(boolean isAppend, GuessRoomInfo data) {
                        guessRoomInfo = data;
                        if (backPlayInfoFrag != null) {
                            backPlayInfoFrag.setGuessRoomInfo(guessRoomInfo);
                        }
                    }

                    @Override
                    public void onFail(ApiException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void setActOrientation(boolean isFull) {
        if (!isFull) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public ChatMember getOwnerInfo() {
        return ownerInfo;
    }

    public long getRoomMemberSize() {
        return roomMemberSize;
    }

    public double getRoomCoins() {
        return roomCoins;
    }

    @Override
    protected void setMainContent(FrameLayout v) {
        super.setMainContent(v);
        player.setIsUseLiveStyle(false);
    }

    @Override
    protected boolean isLivePlay() {
        return false;
    }

    @Override
    protected void onVideoPrepared() {
    }

    @Override
    public void joinChatRoomStatus(ApiException error) {
        super.joinChatRoomStatus(error);
        if (error == null) {
            lockDrawerOpen();
        }
    }

    @Override
    protected void setDrawer(int id) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        drawerFrag = (LiveBackPlayInfoFrag) fragmentManager.findFragmentByTag("back_play_drawer_frag");
        if (drawerFrag == null) {
            drawerFrag = new LiveBackPlayInfoFrag();
            transaction.add(id, drawerFrag, "back_play_drawer_frag");
        } else {
            transaction.show(drawerFrag);
        }
        transaction.commit();
    }

    private void getBackPlayVideoInfo() {
        if (backPlayId == 0) {
            return;
        }
        channelManager.getBackPlayVideoInfo(backPlayId, new DataRequest.DataCallback<List<BackPlayVideoInfo>>() {
            @Override
            public void onSuccess(boolean isAppend, List<BackPlayVideoInfo> data) {
                if (data != null && !data.isEmpty()) {
                    ArrayList<BackPlayVideo> videos = getVideoUrlAndParserLogData(data);
                    if (videos != null && !videos.isEmpty()) {
                        ArrayList<String> videoUrls = new ArrayList<>();
                        ArrayList<Long> videoDurations = new ArrayList<>();
                        for (BackPlayVideo video : videos) {
                            videoUrls.add(video.playUrl);
                            videoDurations.add(video.duration);
                        }
                        setVideoPlay(videoUrls);
                        setVideoDuration(videoDurations);
                    }
                }
            }

            @Override
            public void onFail(ApiException e) {
                e.printStackTrace();
                Toast.makeText(context, "没有可用的回放信息", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取聊天信息
     */
    public void getChatData(long beforeMessageId) {
        if (getChatMessage == null) {
            getChatMessage = new HttpGetChatMessageHelper(this, false,
                    getRoomEnterId());
        }
        getChatMessage.
                getLiveChatMessageList(getRoomId(), beforeMessageId, 10,
                        new DataRequest.DataCallback<List<IChatData>>() {
                            @Override
                            public void onSuccess(boolean isAppend, List<IChatData> data) {
                                if (backPlayInfoFrag != null) {
                                    backPlayInfoFrag.updateChatData(isAppend, data);
                                }
                            }

                            @Override
                            public void onFail(ApiException e) {
                                e.printStackTrace();
                                if (backPlayInfoFrag != null) {
                                    backPlayInfoFrag.updateChatDataError(e);
                                }
                            }
                        });
    }

    /**
     * 获取回放播放的视频路径 m3u8优先， 其次在是默认的flv
     *
     * @param backPlayVideoInfoList
     * @return
     */
    private ArrayList<BackPlayVideo> getVideoUrlAndParserLogData(List<BackPlayVideoInfo> backPlayVideoInfoList) {
        ArrayList<BackPlayVideo> videoUrlList = new ArrayList<>();
        Collections.sort(backPlayVideoInfoList);
        backPlayStartLogTime = backPlayVideoInfoList == null || backPlayVideoInfoList.isEmpty() ? 0 :
                backPlayVideoInfoList.get(0).getStartTime() * 1000;
        for (BackPlayVideoInfo videoInfo : backPlayVideoInfoList) {
            if (videoInfo.getType() == 2) {
                Log.e("TAG", "time == " + videoInfo.getStartTime());
                String videoUrl = getBackPlayVideoUrl(videoInfo);
                if (!TextUtils.isEmpty(videoUrl) && videoUrl.endsWith(".m3u8")) {//修改为只播放m3u8的地址
                    BackPlayVideo video = new BackPlayVideo();
                    video.playUrl = videoUrl;
                    video.duration = (videoInfo.getStopTime() - videoInfo.getStartTime()) * 1000;
                    videoUrlList.add(video);
                }
            } else if (videoInfo.getType() == 1) {
                parserChatFile(videoInfo.getFileUrl());
            } else {
                Log.e("TAG", "BACK play type == " + videoInfo.getType());
            }
        }
        return videoUrlList;
    }

    /**
     * 优先选m3u8的播放路径
     *
     * @param videoInfo
     * @return
     */
    private String getBackPlayVideoUrl(BackPlayVideoInfo videoInfo) {
        String m3u8Url = "";
        String defaultUrl = "";
        if (videoInfo != null) {
            if (videoInfo.getFileExtraUrls() != null) {
                m3u8Url = videoInfo.getFileExtraUrls().getVideoM3u8Url();
            }
            defaultUrl = videoInfo.getFileUrl();
        }
        if (!TextUtils.isEmpty(m3u8Url)) {
            return m3u8Url;
        }
        return defaultUrl;
    }

    private void parserChatFile(String fileUrl) {
        Observable.just(fileUrl)
                .observeOn(Schedulers.io())
                .map(new Func1<String, List<LRCRow>>() {
                    @Override
                    public List<LRCRow> call(String s) {
                        NetChatLRCBuilder chatLRCBuilder = new NetChatLRCBuilder();
                        List<LRCRow> list = chatLRCBuilder.getLRCRowList(s);
                        if (backPlayStartLogTime != 0 && list != null) {
                            for (LRCRow row : list) {
                                if (row != null) {
                                    long dTime = row.getTime() - backPlayStartLogTime;
                                    if (dTime < 0) {
                                        dTime = 0;
                                    }
                                    row.setdTimeStamp(dTime);
                                }
                            }
                        }
                        return list;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<LRCRow>>() {
                    @Override
                    public void call(List<LRCRow> list) {
                        if (backPlayInfoFrag != null) {
                            backPlayInfoFrag.setChatData(list);
                        }
                    }
                });

    }

    private void setVideoPlay(ArrayList<String> urls) {
        if (!player.isPlaying()) {
            player.start(urls);
        }
    }

    private void setVideoDuration(ArrayList<Long> durations) {
        player.setVideoUrlListDuration(durations.toArray(new Long[0]));
    }

    public long getBackPlayId() {
        return backPlayId;
    }

    @Override
    protected ILiveVideoController onCreateLandscapeVideoController() {
        return LiveBackPlayVideoController.newInstanceView(this);
    }

    class BackPlayVideo {
        String playUrl;
        long duration;
    }
}

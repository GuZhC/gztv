package com.dfsx.lzcms.liveroom;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dfsx.core.exception.ApiException;
import com.dfsx.lzcms.liveroom.fragment.LiveInfoFrag;
import com.dfsx.lzcms.liveroom.model.ChatMember;
import com.dfsx.lzcms.liveroom.model.LivePersonalRoomDetailsInfo;
import com.dfsx.lzcms.liveroom.view.LiveIjkVideoPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import java.util.List;

/**
 * 表演类直播间， 像虎牙直播的 颜值 类直播
 * Created by liuwb on 2016/12/5.
 */
public class LiveShowRoomActivity extends LiveRoomDrawerActivity {

    private LiveInfoFrag drawerFrag;
    private LiveIjkVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        lockDrawerClose();
    }

    @Override
    protected void setDrawer(int id) {
        super.setDrawer(id);
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        drawerFrag = (LiveInfoFrag) fragmentManager.findFragmentByTag("live_drawer_frag");
        if (drawerFrag == null) {
            drawerFrag = new LiveInfoFrag();
            transaction.add(id, drawerFrag, "live_drawer_frag");
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
    public void onUpdateOwnerInfo(LivePersonalRoomDetailsInfo roomInfo) {
        super.onUpdateOwnerInfo(roomInfo);
        if (drawerFrag != null) {
            drawerFrag.updateOwner(roomInfo);
        }
    }

    @Override
    public void onUpdateMemberView(int membersize, List<ChatMember> memberList) {
        if (drawerFrag != null) {
            drawerFrag.updateRoomMember(membersize, memberList);
        }
    }

    private void useLiveIjkPlayer(FrameLayout v) {
        player = new LiveIjkVideoPlayer(context);
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
                if (iMediaPlayer.getVideoWidth() > iMediaPlayer.getVideoHeight()) {
                    player.setVideoRotation(90, true);
                }
            }
        });
    }

    @Override
    public void joinChatRoomStatus(ApiException error) {
        super.joinChatRoomStatus(error);
        if (error == null) {
            openDrawer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged ----");
        //        if(ijkVideo.getParent() != null &&
        //                ijkVideo.getParent() instanceof ViewGroup)  {
        //            ((ViewGroup)(ijkVideo.getParent())).removeAllViews();
        //        }
        //        ijkVideo.onChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.restart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setMainContent(int id) {
        super.setMainContent(id);
    }
}

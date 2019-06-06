package com.dfsx.lzcms.liveroom.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.log.LogUtils;
import com.dfsx.lzcms.liveroom.*;
import com.dfsx.lzcms.liveroom.fragment.RoomMemberControlFragment;
import com.dfsx.lzcms.liveroom.fragment.RoomOnlineMemberListFragment;
import com.dfsx.lzcms.liveroom.model.BackPlayIntentData;
import com.dfsx.lzcms.liveroom.model.ChatRoomIntentData;
import com.dfsx.lzcms.liveroom.model.FullScreenRoomIntentData;
import com.dfsx.lzcms.liveroom.model.RecordRoomIntentData;

/**
 * 跳转的工具类
 * Created by liuwb on 2016/12/5.
 */
public class IntentUtil {

    public static void goRoomControl(Context context, long showId, String roomEnterId) {
        try {
            Intent intent = new Intent(context, WhiteTopBarActivity.class);
            intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, "人员管理");
            intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME,
                    RoomMemberControlFragment.class.getName());
            intent.putExtra(RoomMemberControlFragment.KEY_INTENT_SHOW_ID,
                    showId);
            intent.putExtra(RoomMemberControlFragment.KEY_INTENT_ROOM_ENTER_ID,
                    roomEnterId);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goRoomMember(Context context, long showId, String roomEnterId, boolean isLiveUser) {
        try {
            Intent intent = new Intent(context, WhiteTopBarActivity.class);
            intent.putExtra(WhiteTopBarActivity.KEY_TOPBAR_TITLE, "在线用户");
            intent.putExtra(WhiteTopBarActivity.KEY_FRAGMENT_NAME,
                    RoomOnlineMemberListFragment.class.getName());
            intent.putExtra(RoomOnlineMemberListFragment.KEY_INTENT_SHOW_ID,
                    showId);
            intent.putExtra(RoomOnlineMemberListFragment.KEY_INTENT_ROOM_ENTER_ID,
                    roomEnterId);
            intent.putExtra(RoomOnlineMemberListFragment.KEY_INTENT_IS_LIVE_USER,
                    isLiveUser);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调往充值页面
     *
     * @param context
     */
    public static void goAddMoneyActivity(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String title = "充值中心";
            String url = "white_top_bar://" +
                    context.getPackageName()
                    + ".default" + "?title=" + title
                    + "#com.dfsx.lscms.app.fragment.AddMoneyFragment";
            LogUtils.e("TAG", "act url == " + url);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 调往直播页面
     *
     * @param context
     * @param intentData roomId, roomTitle, RoomImage, RoomOwnerId为必须设置
     */
    public static void goFullScreenLiveRoom(Context context, FullScreenRoomIntentData intentData) {
        Intent intent = new Intent(context, LiveFullScreenRoomActivity.class);
        if (intentData != null) {
            intentData.setAutoJoinRoomAtOnce(true);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        context.startActivity(intent);
    }

    public static void goFullScreenLiveRoom(Context context, long showId) {
        FullScreenRoomIntentData intentData = new BackPlayIntentData();
        intentData.setRoomId(showId);
        goFullScreenLiveRoom(context, intentData);
    }

    public static void goLiveGuessRoom(Context context, ChatRoomIntentData intentData) {
        Intent intent = new Intent(context, LiveRoomActivity.class);
        if (intentData != null) {
            intentData.setAutoJoinRoomAtOnce(true);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        context.startActivity(intent);
    }

    public static void goLiveServiceRoom(Context context, long showId) {
        Intent intent = new Intent(context, LiveServiceRoomActivity.class);
        ChatRoomIntentData intentData = new ChatRoomIntentData();
        intentData.setRoomId(showId);
        intentData.setAutoJoinRoomAtOnce(true);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        context.startActivity(intent);
    }

    public static void goLiveGuessRoom(Context context, long roomId) {
        ChatRoomIntentData data = new ChatRoomIntentData();
        data.setRoomId(roomId);
        goLiveGuessRoom(context, data);
    }

    public static void goBackPlayRoom(Context context, long backPlayId) {
        BackPlayIntentData intentData = new BackPlayIntentData();
        intentData.setBackPlayId(backPlayId);
        goBackPlayRoom(context, intentData);
    }

    /**
     * 取回放的房间播放
     *
     * @param context
     * @param intentData
     */
    public static void goBackPlayRoom(Context context, @NonNull BackPlayIntentData intentData) {
        Intent intent = new Intent();
        intent.setClass(context, LiveBackPlayFullScreenRoomActivity.class);
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intentData.setAutoJoinRoomAtOnce(true);
        context.startActivity(intent);
    }

}

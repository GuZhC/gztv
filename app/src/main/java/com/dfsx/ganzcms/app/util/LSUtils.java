package com.dfsx.ganzcms.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.dfsx.ganzcms.app.App;
import com.dfsx.lzcms.liveroom.AbsChatRoomActivity;
import com.dfsx.lzcms.liveroom.model.RecordRoomIntentData;
import me.lake.librestreaming.sample.LiveRecordStreamingActivity;

/**
 * Created by liuwb on 2017/7/3.
 */
public class LSUtils {

    public static void toastNoFunction(Context context) {
        Toast.makeText(context, "功能暂未开通", Toast.LENGTH_SHORT).show();
    }


    public static void toastMsgFunction(Context context,String  msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 活动直播
     *
     * @param context
     * @param isFullScreen
     * @param showId
     * @param rtmpUrl
     */
    public static void goLiveServiceRecord(Activity context, boolean isFullScreen, long showId,
                                           String rtmpUrl) {
        goRecord(context, isFullScreen, showId, true, rtmpUrl, null, null);
    }

    /**
     * 个人直播
     *
     * @param context
     * @param isFullScreen
     * @param showId
     * @param rtmpUrl
     */
    public static void goPersonalRecord(Activity context, boolean isFullScreen, long showId,
                                        String rtmpUrl) {
        goRecord(context, isFullScreen, showId, false, rtmpUrl, null, null);
    }

    /**
     * 个人开启直播可以调用
     *
     * @param context
     * @param isFullScreen
     * @param showId
     */
    public static void goRecord(Activity context, boolean isFullScreen, long showId) {
        goRecord(context, isFullScreen, showId, false, "", null, null);
    }

    public static void goRecord(Activity context, boolean isFullScreen, long showId, boolean isLiveServiceRecord,
                                String rtmpUrl, String subject, String coverImagePath) {
        Intent intent = new Intent(context, LiveRecordStreamingActivity.class);
        RecordRoomIntentData intentData = new RecordRoomIntentData();
        intent.putExtra(AbsChatRoomActivity.KEY_CHAT_ROOM_INTENT_DATA, intentData);
        intentData.setScreenPortrait(!isFullScreen);
        intentData.setAutoJoinRoomAtOnce(true);
        intentData.setRoomId(showId);
        intentData.setLiveServiceRecord(isLiveServiceRecord);
        intentData.setRoomOwnerId(App.getInstance().getUser().getUser().getId());
        intentData.setLiveRTMPURL(rtmpUrl);
        intentData.setCoverImagePath(coverImagePath);
        intentData.setSubject(subject);

        context.startActivity(intent);
    }
}

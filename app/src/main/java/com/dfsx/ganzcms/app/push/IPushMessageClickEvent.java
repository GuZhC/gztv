package com.dfsx.ganzcms.app.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.dfsx.core.common.act.DefaultFragmentActivity;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.ganzcms.app.act.CommunityAct;
import com.dfsx.ganzcms.app.act.CommunityNewAct;
import com.dfsx.ganzcms.app.fragment.CommWebFragment;
import com.dfsx.ganzcms.app.fragment.LoadFragment;
import com.dfsx.ganzcms.app.fragment.LotteryDrawnWebFragment;
import com.dfsx.ganzcms.app.fragment.MyFansFragment;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;
import com.dfsx.lzcms.liveroom.model.FullScreenRoomIntentData;
import com.dfsx.lzcms.liveroom.util.IntentUtil;

/**
 * Created by liuwb on 2017/5/4.
 */
public interface IPushMessageClickEvent {

    void onLiveServiceMessage(Context context, Message message);

    /**
     * 直播的Message
     *
     * @param message
     */
    void onLiveChannelMessage(Context context, Message message);

    /**
     * 竞技直播的Message
     *
     * @param context
     * @param message
     */
    void onLiveGuessMessage(Context context, Message message);

    void onLivePlaybackMessage(Context context, Message message);

    /**
     * 关注的直播消息
     *
     * @param message
     */
    void onConcernLiveMessage(Context context, Message message);

    /**
     * 个人被关注消息(系统类消息)
     *
     * @param message
     */
    void onConcernMessage(Context context, Message message);

    /**
     * 通知推送消息(系统类消息)
     *
     * @param message
     */
    void onNotificationMessage(Context context, Message message);

    /**
     * 内容推送消息(系统类消息)
     *
     * @param message
     */
    void onContentMessage(Context context, Message message);

    /**
     * 个人上传视频消息(关注类消息)
     *
     * @param message
     */
    void onConcernUserUploadVideoMessage(Context context, Message message);

    /**
     * 主题推送消息(系统类消息)
     *
     * @param message
     */
    void onCommunityMessage(Context context, Message message);

    /**
     * community 个人发贴消息(关注类消息)
     *
     * @param message
     */
    void onConcernCommunityMessage(Context context, Message message);

    /**
     * 个人页消息
     *
     * @param context
     * @param message
     */
    void onUserPageMessage(Context context, Message message);


    /**
     * 直播中中奖消息
     *
     * @param context
     * @param message
     */
    void onLotteryDrawnMessage(Context context, Message message);


    public class DefaultPushMessageClickEvent implements IPushMessageClickEvent {

        @Override
        public void onLiveServiceMessage(Context context, Message message) {
            long showId = message.getExt().getBody().getShow_id();
            if (showId != 0) {
                IntentUtil.goLiveServiceRoom(context,
                        showId);
            }
        }

        @Override
        public void onLiveChannelMessage(Context context, Message message) {
            long channelId = message.getExt().getBody().getShow_id();
            if (channelId != 0) {
                FullScreenRoomIntentData intentData = new FullScreenRoomIntentData();
                intentData.setRoomId(channelId);
                IntentUtil.goFullScreenLiveRoom(context,
                        intentData);
            }
        }

        @Override
        public void onLiveGuessMessage(Context context, Message message) {
            try {
                long channelId = message.getExt().getBody().getShow_id();
                if (channelId != 0) {
                    IntentUtil.goLiveGuessRoom(context, channelId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLivePlaybackMessage(Context context, Message message) {
            long playBackId = message.getExt().getBody().getShow_id();
            if (playBackId != 0) {
                IntentUtil.goBackPlayRoom(context, playBackId);
            }
        }

        @Override
        public void onConcernLiveMessage(Context context, Message message) {
            long channelId = message.getExt().getBody().getShow_id();
            long concernUserId = message.getExt().getBody().getFollow_user_id();
            if (channelId != 0) {
                FullScreenRoomIntentData intentData = new FullScreenRoomIntentData();
                intentData.setRoomId(channelId);
                intentData.setRoomOwnerId(concernUserId);
                IntentUtil.goFullScreenLiveRoom(context,
                        intentData);
            }
        }

        @Override
        public void onConcernMessage(Context context, Message message) {
            WhiteTopBarActivity.startAct(context, MyFansFragment.class.getName(), "我的粉丝");
        }

        @Override
        public void onNotificationMessage(Context context, Message message) {
            if ("url".equals(message.getExt().getBody().getLink_type())) {
                String url = message.getExt().getBody().getLink_value(); // web address
                Bundle bundle = new Bundle();
                bundle.putString(CommWebFragment.PARAMS_URL, url);
                WhiteTopBarActivity.startAct(context, CommWebFragment.class.getName(), "网页", "",
                        bundle);
            }
        }

        @Override
        public void onContentMessage(Context context, Message message) {
            long contentId = message.getExt().getBody().getContent_id();
            if (contentId != 0) {
                DefaultFragmentActivity.start(context, LoadFragment.class.getName(), contentId);
                //                Intent intent = new Intent(context, CvideoPlayAct.class);
                //                Bundle bundle = new Bundle();
                //                bundle.putLong("index", contentId);
                //                intent.putExtras(bundle);
                //                if (!(context instanceof Activity)) {
                //                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //                }
                //                context.startActivity(intent);
            }
        }

        @Override
        public void onConcernUserUploadVideoMessage(Context context, Message message) {
            long userId = message.getExt().getBody().getUser_id();
            if (userId != 0) {
                com.dfsx.core.common.Util.IntentUtil.gotoPersonHomeAct(context, userId);
            }
        }

        @Override
        public void onCommunityMessage(Context context, Message message) {
            long threadId = message.getExt().getBody().getThread_id();
            if (threadId != 0) {
                gotoCommuncityAct(context, threadId);
            }
        }

        @Override
        public void onConcernCommunityMessage(Context context, Message message) {
            long threadId = message.getExt().getBody().getThread_id();
            if (threadId != 0) {
                gotoCommuncityAct(context, threadId);
            }
        }

        @Override
        public void onUserPageMessage(Context context, Message message) {
            try {
                long userId = message.getExt().getBody().getUser_id();
                if (userId != 0) {
                    com.dfsx.core.common.Util.
                            IntentUtil.gotoPersonHomeAct(context, userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLotteryDrawnMessage(Context context, Message message) {
            try {
                long messageId = message.getId();
                long drawn_id = message.getExt().getBody().getLottery_drawn_id();
                long show_id = message.getExt().getBody().getShow_id();
                Log.e("TAG", "drawn_id === " + drawn_id);
                if (messageId != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(LotteryDrawnWebFragment.INTENT_DRAWN_ID, messageId);
                    WhiteTopBarActivity.startAct(context, LotteryDrawnWebFragment.class.getName(),
                            "中奖详情", "",
                            bundle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 跳转到圈子详情  heyang  2017-5-5
         */
        public void gotoCommuncityAct(Context context, long tId) {
            Intent intent = new Intent(context, CommunityNewAct.class);
            Bundle bundle = new Bundle();
            bundle.putLong("tid", tId);
            intent.putExtras(bundle);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }

    }

}

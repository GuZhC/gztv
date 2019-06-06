package com.dfsx.ganzcms.app.push;

import android.content.Context;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Extension;
import com.dfsx.ganzcms.app.push.PushMeesageModel.Message;

/**
 * Created by liuwb on 2017/5/4.
 */
public class OnMessageClickEvent {

    private Message message;

    private IPushMessageClickEvent pushMessageClickEvent;
    private Context context;

    public OnMessageClickEvent(Context context, IPushMessageClickEvent pushMessageClickEvent) {
        this.pushMessageClickEvent = pushMessageClickEvent;
        this.context = context;
    }

    public OnMessageClickEvent(Context context, Message message, IPushMessageClickEvent pushMessageClickEvent) {
        this.pushMessageClickEvent = pushMessageClickEvent;
        this.context = context;
        onMessageClick(message);
    }

    public void onMessageClick(Message message) {
        if (message != null && pushMessageClickEvent != null) {
            if (message.getExt() != null) {
                Extension.MessageType messageType = message.getExt().getMessageType();
                switch (messageType) {
                    case general_notice:
                        pushMessageClickEvent.onNotificationMessage(context, message);
                        break;
                    case general_followed:
                        pushMessageClickEvent.onConcernMessage(context, message);
                        break;
                    case cms_content:
                        pushMessageClickEvent.onContentMessage(context, message);
                        break;
                    case cms_follow_video_uploaded:
                        pushMessageClickEvent.onConcernUserUploadVideoMessage(context, message);
                        break;
                    case live_service:
                        pushMessageClickEvent.onLiveServiceMessage(context, message);
                        break;
                    case live_personal_show:
                        pushMessageClickEvent.onLiveChannelMessage(context, message);
                        break;
                    case live_personal_playback:
                        pushMessageClickEvent.onLivePlaybackMessage(context, message);
                        break;
                    case live_follow_channel_living:
                        pushMessageClickEvent.onConcernLiveMessage(context, message);
                        break;
                    case community_thread:
                        pushMessageClickEvent.onCommunityMessage(context, message);
                        break;
                    case community_follow_thread_posted:
                        pushMessageClickEvent.onConcernCommunityMessage(context, message);
                        break;
                    case userPage:
                        pushMessageClickEvent.onUserPageMessage(context, message);
                        break;
                    case live_lottery_drawn:
                        pushMessageClickEvent.onLotteryDrawnMessage(context, message);
                        break;
                    case live_guess_channel:
                        pushMessageClickEvent.onLiveGuessMessage(context, message);
                        break;
                    case unknwn:
                        break;
                }
            }
        }
    }
}

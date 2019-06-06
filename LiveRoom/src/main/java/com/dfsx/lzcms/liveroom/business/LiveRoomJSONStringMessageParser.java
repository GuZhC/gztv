package com.dfsx.lzcms.liveroom.business;

import android.text.TextUtils;
import android.util.Log;
import com.dfsx.lzcms.liveroom.model.*;
import com.dfsx.lzcms.liveroom.mqtt.MqttManager;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuwb on 2017/7/7.
 */
public class LiveRoomJSONStringMessageParser implements ILiveRoomMessageParser<String> {
    private LiveMessageCallback callBack;

    @Override
    public void parserMessage(String tag, String messageJson) {
        LiveMessage liveMessage = parserMessageData(tag, messageJson);
        if (callBack != null && liveMessage != null) {
            callBack.onLiveMessage(liveMessage);
        }
    }

    public LiveMessage parserMessageData(String tag, String messageJson) {
        if (!TextUtils.isEmpty(messageJson)) {
            int topType = MqttManager.getInstance().isRoomTopic(tag) ?
                    LiveMessage.TOPIC_ROOM : LiveMessage.TOPIC_USER;
            String res = messageJson;
            try {
                JSONObject json = new JSONObject(res);
                String type = json.optString("type");
                Gson gson = new Gson();
                LiveMessage m;
                if (!TextUtils.isEmpty(type)) {
                    if (TextUtils.equals(type, "system_user_entered")) {
                        m = gson.fromJson(res, UserMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_user_exited")) {
                        m = gson.fromJson(res, UserMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_quit")) {
                        m = gson.fromJson(res, ExitMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_show_started")) {
                        m = gson.fromJson(res, LiveStartMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_show_stopped")) {
                        m = gson.fromJson(res, LiveEndMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "chat_message")) {
                        m = gson.fromJson(res, UserChatMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "gift_send_gift")) {
                        m = gson.fromJson(res, GiftMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "image_text_message")) {
                        m = gson.fromJson(res, ImageTextMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "quiz_bet")) {
                        m = gson.fromJson(res, BetGuessMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "quiz_bet_result")) {
                        m = gson.fromJson(res, GuessResultMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_input_stream_started")) {
                        m = gson.fromJson(res, LiveInputStreamMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_input_stream_stopped")) {
                        m = gson.fromJson(res, LiveInputStreamEndMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_output_stream_started")) {
                        m = gson.fromJson(res, LiveOutputStreamStartMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_output_stream_stopped")) {
                        m = gson.fromJson(res, LiveOutputStreamEndMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_user_banished")) {
                        m = gson.fromJson(res, LiveBanUserMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_user_forbidden_speak")) {
                        m = gson.fromJson(res, LiveNoTalkMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "system_user_allow_speak")) {
                        m = gson.fromJson(res, LiveUserAllowTalkMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "disable_chat_message")) {
                        m = gson.fromJson(res, LiveRoomNoTalkMessage.class);
                        m.setTopicType(topType);
                    } else if (TextUtils.equals(type, "enable_chat_message")) {
                        m = gson.fromJson(res, LiveRoomCancelNoTalkMessage.class);
                        m.setTopicType(topType);
                    } else {
                        m = null;
                        Log.e("TAG", "没有找到可用的消息类型 type" + type);
                    }
                    return m;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void setMessageTypeCallBack(IRoomMessageType roomMessageType) {
        if (roomMessageType != null && roomMessageType instanceof LiveMessageCallback) {
            callBack = (LiveMessageCallback) roomMessageType;
        }
    }
}

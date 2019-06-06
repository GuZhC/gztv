package com.dfsx.lzcms.liveroom.business;

import android.text.TextUtils;
import com.dfsx.lzcms.liveroom.model.*;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by liuwb on 2017/7/4.
 */
public class MqttMessageParser implements ILiveRoomMessageParser<MqttMessage> {

    private LiveRoomJSONStringMessageParser stringMessageParser;
    private LiveMessageTypeCallBack typeCallBack;

    public MqttMessageParser() {
        stringMessageParser = new LiveRoomJSONStringMessageParser();
        stringMessageParser.setMessageTypeCallBack(new LiveMessageCallback() {
            @Override
            public void onLiveMessage(LiveMessage liveMessage) {
                if (typeCallBack != null) {
                    String type = liveMessage.getType();
                    if (liveMessage instanceof UserMessage) {
                        if (liveMessage instanceof LiveBanUserMessage) {
                            typeCallBack.onBanUserMessage((LiveBanUserMessage) liveMessage);
                        } else if (liveMessage instanceof LiveNoTalkMessage) {
                            typeCallBack.onNoTalkUserMessage((LiveNoTalkMessage) liveMessage);
                        } else if (liveMessage instanceof LiveUserAllowTalkMessage) {
                            typeCallBack.onAllowUserTalkMessage((LiveUserAllowTalkMessage) liveMessage);
                        } else {
                            if (TextUtils.equals(type, "system_user_entered")) {
                                typeCallBack.onUserJoinRoomMessage((UserMessage) liveMessage);
                            } else {
                                typeCallBack.onUserExitRoomMessage((UserMessage) liveMessage);
                            }
                        }
                    } else if (liveMessage instanceof ExitMessage) {
                        typeCallBack.onExitMessage((ExitMessage) liveMessage);
                    } else if (liveMessage instanceof LiveStartMessage) {
                        typeCallBack.onLiveStartMessage((LiveStartMessage) liveMessage);
                    } else if (liveMessage instanceof LiveEndMessage) {
                        typeCallBack.onLiveEndMessage((LiveEndMessage) liveMessage);
                    } else if (liveMessage instanceof UserChatMessage) {
                        typeCallBack.onUserChatMessage((UserChatMessage) liveMessage);
                    } else if (liveMessage instanceof GiftMessage) {
                        typeCallBack.onReceiveGiftMessage((GiftMessage) liveMessage);
                    } else if (liveMessage instanceof ImageTextMessage) {
                        typeCallBack.onImageAndTextMessage((ImageTextMessage) liveMessage);
                    } else if (liveMessage instanceof BetGuessMessage) {
                        typeCallBack.onBetMessage((BetGuessMessage) liveMessage);
                    } else if (liveMessage instanceof GuessResultMessage) {
                        typeCallBack.onGuessResultMessage((GuessResultMessage) liveMessage);
                    } else if (liveMessage instanceof LiveOutputStreamStartMessage) {
                        typeCallBack.onLiveOutputStreamStartMessage((LiveOutputStreamStartMessage) liveMessage);
                    } else if (liveMessage instanceof LiveInputStreamMessage) {
                        typeCallBack.onLiveInputStreamMessage((LiveInputStreamMessage) liveMessage);
                    } else if (liveMessage instanceof LiveOutputStreamEndMessage) {
                        typeCallBack.onLiveOutputStreamEndMessage((LiveOutputStreamEndMessage) liveMessage);
                    } else if (liveMessage instanceof LiveInputStreamEndMessage) {
                        typeCallBack.onLiveInputStreamEndMessage((LiveInputStreamEndMessage) liveMessage);
                    } else if (liveMessage instanceof LiveRoomNoTalkMessage) {
                        typeCallBack.onRoomNoTalkMessage((LiveRoomNoTalkMessage) liveMessage);
                    } else if (liveMessage instanceof LiveRoomCancelNoTalkMessage) {
                        typeCallBack.onRoomAllowTalkMessage((LiveRoomCancelNoTalkMessage) liveMessage);
                    }
                }
            }
        });
    }

    @Override
    public void parserMessage(String tag, MqttMessage message) {
        if (message != null) {
            stringMessageParser.parserMessage(tag, message.toString());
        }
    }

    @Override
    public void setMessageTypeCallBack(IRoomMessageType roomMessageType) {
        if (roomMessageType != null && roomMessageType instanceof LiveMessageTypeCallBack) {
            typeCallBack = (LiveMessageTypeCallBack) roomMessageType;
        }
    }


}

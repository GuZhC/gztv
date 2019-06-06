package com.dfsx.ganzcms.app.push.PushMeesageModel;

import android.text.TextUtils;

/**
 * Created by wen on 2017/3/27.
 */

public class Extension {
    private String source;
    private String type;
    private Body body;


    private MessageType messageType;

    public enum MessageType {
        general_notice, general_followed,
        cms_content, cms_follow_video_uploaded,
        /**
         * 活動直播
         */
        live_service,
        /**
         * 个人直播
         */
        live_personal_show,
        /**
         * 个人直播回放
         */
        live_personal_playback,
        live_follow_channel_living,
        community_thread, community_follow_thread_posted,
        /**
         * 个人主页
         */
        userPage,
        /**
         * 竞猜直播
         */
        live_guess_channel,

        /**
         * 直播中奖消息(系统类消息)
         */
        live_lottery_drawn,
        unknwn
    }


    public Extension(String source, String type, Body body) {
        this.source = source;
        this.type = type;
        this.body = body;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * 根据source和type的值来确定具体的类型，类型使用枚举类型MessageType来表示
     *
     * @return
     */
    public MessageType getMessageType() {
        if (messageType != null)
            return messageType;
        if (source.equals("general")) {
            if (type.equals("notice"))
                messageType = MessageType.general_notice;
            else if (type.equals("followed"))
                messageType = MessageType.general_followed;
        } else if (source.equals("cms")) {
            if (type.equals("content"))
                messageType = MessageType.cms_content;
            else if (type.equals("follow.video-uploaded"))
                messageType = MessageType.cms_follow_video_uploaded;
        } else if (source.equals("live")) {
            if (type.equals("show")) {
                if (body != null) {
                    if (body.getLive_type() == 1 && body.getState() == 3) {
                        messageType = MessageType.live_personal_playback;
                    } else if (body.getLive_type() == 2) {
                        messageType = MessageType.live_service;
                    } else {
                        messageType = MessageType.live_personal_show;
                    }
                } else {
                    messageType = MessageType.live_personal_show;
                }
            } else if (type.equals("follow.show-living"))
                messageType = MessageType.live_follow_channel_living;
            else if (type.equals("channel_quiz")) {
                messageType = MessageType.live_guess_channel;
            } else if (TextUtils.equals("show.lottery-drawn", type)) {
                messageType = MessageType.live_lottery_drawn;
            }
        } else if (source.equals("community")) {
            if (type.equals("thread"))
                messageType = MessageType.community_thread;
            else if (type.equals("follow.thread-posted"))
                messageType = MessageType.community_follow_thread_posted;
        } else if (source.equals("user")) {
            messageType = MessageType.userPage;
        }
        if (messageType == null)
            messageType = MessageType.unknwn;
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}

package com.dfsx.lzcms.liveroom.model;

import com.dfsx.lzcms.liveroom.business.AppManager;
import com.dfsx.lzcms.liveroom.util.StringUtil;

/**
 * Created by liuwb on 2017/7/4.
 */
public class LiveMessage implements IChatData {

    public static final int TOPIC_USER = 10001;
    public static final int TOPIC_ROOM = 10002;


    /**
     * id : 0
     * type : show_started
     * timestamp : 1499139740
     */
    private long id;
    private String type;
    private long timestamp;
    /**
     * # LiveMessage.TOPIC_USER
     * # LiveMessage.TOPIC_ROOM
     */
    private int topicType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    @Override
    public long getChatId() {
        return id;
    }

    @Override
    public long getChatUserId() {
        return 0;
    }

    @Override
    public long getUserLevelId() {
        return 0;
    }

    @Override
    public long getChatTime() {
        return getTimestamp();
    }

    @Override
    public String getChatUserNickName() {
        return null;
    }

    @Override
    public String getChatUserLogo() {
        return null;
    }

    @Override
    public String getChatTimeText() {
        return StringUtil.getChatTimeText(timestamp);
    }

    @Override
    public CharSequence getChatContentText() {
        return null;
    }

    @Override
    public ChatViewType getChatViewType() {
        long curUserId = AppManager.getInstance().getIApp().getLoginUserId();
        if (curUserId != 0 && curUserId == getChatUserId()) {
            return ChatViewType.CURRENT_USER;
        }
        return ChatViewType.NO_CURRENT_USER;
    }
}

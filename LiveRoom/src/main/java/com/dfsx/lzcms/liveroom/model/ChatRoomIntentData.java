package com.dfsx.lzcms.liveroom.model;

import java.io.Serializable;

/**
 * Created by liuwb on 2017/2/21.
 */
public class ChatRoomIntentData implements Serializable {

    /**
     * 房间的id好。 目前乐山的id为channel id
     */
    private long roomId;

    private String roomName;

    /**
     * 进入IM服务器的密码
     */
    private String roomPassword;

    private String roomTitle;

    private long roomOwnerId;

    private String roomOwnerNickName;

    private String roomOwnerAccountName;

    private String roomOwnerLogo;

    /**
     * 是否自动进入房间
     */
    private boolean isAutoJoinRoomAtOnce;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public boolean isAutoJoinRoomAtOnce() {
        return isAutoJoinRoomAtOnce;
    }

    public void setAutoJoinRoomAtOnce(boolean autoJoinRoomAtOnce) {
        isAutoJoinRoomAtOnce = autoJoinRoomAtOnce;
    }

    public long getRoomOwnerId() {
        return roomOwnerId;
    }

    public void setRoomOwnerId(long roomOwnerId) {
        this.roomOwnerId = roomOwnerId;
    }

    public String getRoomOwnerNickName() {
        return roomOwnerNickName;
    }

    public void setRoomOwnerNickName(String roomOwnerNickName) {
        this.roomOwnerNickName = roomOwnerNickName;
    }

    public String getRoomOwnerAccountName() {
        return roomOwnerAccountName;
    }

    public void setRoomOwnerAccountName(String roomOwnerAccountName) {
        this.roomOwnerAccountName = roomOwnerAccountName;
    }

    public String getRoomOwnerLogo() {
        return roomOwnerLogo;
    }

    public void setRoomOwnerLogo(String roomOwnerLogo) {
        this.roomOwnerLogo = roomOwnerLogo;
    }
}

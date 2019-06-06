package com.dfsx.lzcms.liveroom.mqtt;

/**
 * Created by liuwb on 2017/7/3.
 */
public class Connection {
    private String serverUri;
    private String clientId;
    private String roomId;
    private String userId;
    private String userName;

    public Connection() {

    }

    public Connection(String serverUri, String clientId, String userId, String userName, String roomId) {
        this.serverUri = serverUri;
        this.clientId = clientId;
        this.roomId = roomId;
        this.userId = userId;
        this.userName = userName;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

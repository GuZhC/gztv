package com.dfsx.lzcms.liveroom.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuwb on 2016/11/2.
 */
public class EnterRoomInfo implements Serializable {

    /**
     * service_name : 聊天服务器服务名
     * server_address : 聊天服务器地址
     * room_address : 直播间地址
     * room_auth_code : 登陆授权码，生成方式：md5(encrypted_password:room_address)
     */
    @SerializedName("room_id")
    private String roomIdAddress;
    @SerializedName("tcp_address")
    private String tcpAddress;
    @SerializedName("web_socket_address")
    private String webSocketAddress;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("client_id")
    private String clientId;

    private boolean isError = false;
    private String errorMessage;


    public String getServerAddress() {
        return tcpAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.tcpAddress = serverAddress;
    }

    public String getTcpIp() {
        if (!TextUtils.isEmpty(tcpAddress)) {
            String[] addr = tcpAddress.split(":");
            if (addr != null && !TextUtils.isEmpty(addr[0])) {
                return addr[0];
            }
        }
        return tcpAddress;
    }

    public int getTcpPort() {
        if (!TextUtils.isEmpty(tcpAddress)) {
            String[] addr = tcpAddress.split(":");
            if (addr != null && addr.length >= 2 && !TextUtils.isEmpty(addr[1]) &&
                    TextUtils.isDigitsOnly(addr[1])) {
                return Integer.valueOf(addr[1]);
            }
        }
        return 0;
    }

    public String getRoomIdAddress() {
        return roomIdAddress;
    }

    public void setRoomIdAddress(String roomId) {
        this.roomIdAddress = roomId;
    }

    public String getWebSocketAddress() {
        return webSocketAddress;
    }

    public void setWebSocketAddress(String webSocketAddress) {
        this.webSocketAddress = webSocketAddress;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

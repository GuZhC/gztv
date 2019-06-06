package com.dfsx.lzcms.liveroom.mqtt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.dfsx.core.CoreApp;
import com.dfsx.core.exception.ApiException;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

/**
 * Created by liuwb on 2017/7/3.
 */
public class MqttManager implements MqttCallbackExtended {

    public static final int STATE_INIT = 0;
    /**
     * 连接中,请求连接
     */
    public static final int STATE_CONNECTING = 1;
    /**
     * 已经连接
     */
    public static final int STATE_CONNECTED = 2;
    /**
     * 关闭连接, 断开连接
     */
    public static final int STATE_CLOSE = 3;

    public static final String TAG = "MQTT";

    public static final int MSG_MQTT_MESSAGE = 1001;

    public static final int QOS = 1;

    private static MqttManager instance = new MqttManager();
    private MqttAndroidClient client;
    private Connection connection;
    private MQTTMessageReceiveListener messageListener;

    private int connectState = STATE_INIT;

    /**
     * 目标状态
     */
    private int tagConnectState = STATE_INIT;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_MQTT_MESSAGE) {
                MqttMessage mqttMessage = (MqttMessage) msg.obj;
                String topic = msg.getData().getString("MESSAGE_TOPIC", "");
                if (messageListener != null) {
                    messageListener.onProcessMessage(topic, mqttMessage);
                }
            }
        }
    };


    private MqttManager() {

    }

    public static MqttManager getInstance() {
        return instance;
    }

    public void connect(final Connection connection, final ICallBack<ApiException> callBack) {
        this.connection = connection;
        connectState = STATE_CONNECTING;
        tagConnectState = STATE_CONNECTING;
        disconnect();
        new Thread() {
            @Override
            public void run() {
                super.run();
                client = new MqttAndroidClient(CoreApp.getInstance().getApplicationContext(),
                        connection.getServerUri(), connection.getClientId());
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                mqttConnectOptions.setCleanSession(true);
                mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
                String userName = connection.getUserName();
                if (TextUtils.isEmpty(userName)) {
                    userName = "*";
                }
                mqttConnectOptions.setUserName(userName);

                try {
                    client.setCallback(MqttManager.this);
                    client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                            disconnectedBufferOptions.setBufferEnabled(true);
                            disconnectedBufferOptions.setBufferSize(100);
                            disconnectedBufferOptions.setPersistBuffer(false);
                            disconnectedBufferOptions.setDeleteOldestMessages(false);
                            try {
                                if (client != null) {
                                    client.setBufferOpts(disconnectedBufferOptions);
                                }
                                subscribeUserToTopic();
                                subscribeRoomToTopic();
                                Log.e(TAG, "connect onSuccess-------------");
                                callBack.callBack(null);
                            } catch (Exception e) {
                                //这里在快速切换房间的时候有几率发生client为null.这个异常没有影响
                                e.printStackTrace();
                                Log.e(TAG, "disconnectedBufferOptions exception--------");
                                callBack.callBack(new ApiException(e));
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            callBack.callBack(new ApiException(exception));
                            Log.e("TAG", "MQTT connect fail----------");
                            if (exception != null) {
                                exception.printStackTrace();
                            }
                            callBack.callBack(new ApiException("链接聊天服务器失败"));
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void sendHandlerMessage(String topic, MqttMessage mqttMessage) {
        Message msg = handler.obtainMessage(MSG_MQTT_MESSAGE);
        msg.obj = mqttMessage;
        Bundle bundle = new Bundle();
        bundle.putString("MESSAGE_TOPIC", topic);
        msg.setData(bundle);
        handler.sendMessageDelayed(msg, 100);
    }

    protected void subscribeUserToTopic() {
        if (connection != null && !TextUtils.isEmpty(connection.getUserId())) {
            String userTopic = "/users/" + connection.getUserId();
            try {
                subscribeToTopic(userTopic, QOS);
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, userTopic + " --- subscribe Fail");
            }
        }
    }

    protected void subscribeRoomToTopic() {
        if (connection != null && !TextUtils.isEmpty(connection.getRoomId())) {
            String roomTopic = "/rooms/" + connection.getRoomId();
            try {
                subscribeToTopic(roomTopic, QOS);
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, roomTopic + " --- subscribe Fail");
            }
        } else {
            Log.e(TAG, "subscribe data error");
            throw new NullPointerException();
        }
    }

    protected void unsubscribeTopic() {
        if (client != null && connection != null) {
            String userTopic = "/users/" + connection.getUserId();
            String roomTopic = "/rooms/" + connection.getRoomId();
            try {
                String[] arr = null;
                if (!TextUtils.isEmpty(connection.getUserId())) {
                    arr = new String[]{userTopic, roomTopic};
                } else {
                    arr = new String[]{roomTopic};
                }
                if (client != null) {
                    client.unsubscribe(arr);
                }
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, " unsubscribe Fail---------");
            }
        }
    }

    private void subscribeToTopic(final String subscriptionTopic, int qos) throws MqttException {
        if (client == null) {
            return;
        }
        client.subscribe(subscriptionTopic, qos, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.e(TAG, subscriptionTopic + " --- subscribe onSuccess");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e(TAG, subscriptionTopic + " --- subscribe Fail");
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void toast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CoreApp.getInstance(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean disconnect() {
        if (client != null) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (MqttException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean isUserTopic(String topic) {
        if (!TextUtils.isEmpty(topic) && topic.startsWith("/users/")) {
            return true;
        }
        return false;
    }

    public boolean isRoomTopic(String topic) {
        if (!TextUtils.isEmpty(topic) && topic.startsWith("/rooms/")) {
            return true;
        }
        return false;
    }


    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public boolean close() {
        boolean is = false;
        if (connectState == STATE_CONNECTED) {
            unsubscribeTopic();
            is = disconnect();
            client = null;
            connectState = STATE_CLOSE;
        }
        tagConnectState = STATE_CLOSE;
        return is;
    }

    public void setMessageListener(MQTTMessageReceiveListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            subscribeUserToTopic();
            subscribeRoomToTopic();
        }
        connectState = STATE_CONNECTED;
        if (tagConnectState == STATE_CLOSE) {
            close();
        }
        Log.e(TAG, serverURI + "--------" + (reconnect ? "重新" : "") + "连接成功");
    }

    @Override
    public void connectionLost(Throwable cause) {
        connectState = STATE_CLOSE;
        if (cause != null) {
            cause.printStackTrace();
        }
        Log.e(TAG, "聊天服务器已断开-----------------");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.e(TAG, "messageArrived  ---- " + message.toString());
        //                            if (messageListener != null) {
        //                                messageListener.onProcessMessage(topic, message);
        //                            }
        sendHandlerMessage(topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.e(TAG, "deliveryComplete  ---- " + (token != null ? token.getResponse().getMessageId() : ""));
    }
}

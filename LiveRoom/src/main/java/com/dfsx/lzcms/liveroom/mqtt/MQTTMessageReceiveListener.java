package com.dfsx.lzcms.liveroom.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by liuwb on 2017/7/4.
 */
public interface MQTTMessageReceiveListener {
    void onProcessMessage(String topic, MqttMessage mqttMessage);
}

package com.example.smart.mqtt;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttPublisher {

    private final MqttConnectOptions options;

    public void publish(String topic, String payload) {
        try {
            MqttClient client = new MqttClient("tcp://localhost:1883", "spring-publisher-" + System.currentTimeMillis());
            client.connect(options);
            client.publish(topic, payload.getBytes(), 0, false);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

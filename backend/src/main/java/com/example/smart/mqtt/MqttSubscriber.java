package com.example.smart.mqtt;

import com.example.smart.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class MqttSubscriber {

    private final MqttConnectOptions options;
    private final SensorService sensorService;

    @PostConstruct
    public void init() throws MqttException {

        MqttClient client = new MqttClient("tcp://localhost:1883", "spring-subscriber");
        client.connect(options);

        // home/sensor/temp, home/sensor/humidity 등 전체 구독
        client.subscribe("home/sensor/#", (topic, message) -> {
            String payload = new String(message.getPayload());
            System.out.println("⚡ MQTT 수신 → " + topic + " | " + payload);

            sensorService.saveFromMqtt(topic, payload);
        });

        System.out.println("MQTT Subscriber Started!");
    }
}

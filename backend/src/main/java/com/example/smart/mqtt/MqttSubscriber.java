package com.example.smart.mqtt;

import com.example.smart.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@Profile("local")
@RequiredArgsConstructor
public class MqttSubscriber {

    private final MqttConnectOptions options;
    private final SensorService sensorService;

    @PostConstruct
    public void init() throws MqttException {

        System.out.println("Connecting to MQTT broker...");

        MqttClient client =
                new MqttClient("tcp://localhost:1883", "spring-subscriber");

        client.connect(options);

        System.out.println("Connected to MQTT broker");

        client.subscribe("home/sensor/#", (topic, message) -> {
            String payload = new String(message.getPayload());
            System.out.println("MQTT 수신 → " + topic + " | " + payload);

            sensorService.saveFromMqtt(topic, payload);
        });

        System.out.println("Subscribed to topic: home/sensor/#");
        System.out.println("MQTT Subscriber Started!");
    }
}

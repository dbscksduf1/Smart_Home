package com.example.smart.mqtt;

import com.example.smart.service.SensorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttSubscriber {

    private final MqttConnectOptions options;
    private final SensorService sensorService;

    @Value("${mqtt.broker:}")
    private String broker;

    @Value("${mqtt.topic:}")
    private String topic;

    @Value("${mqtt.client-id:}")
    private String clientId;

    @PostConstruct
    public void init() throws MqttException {

        if (broker.isEmpty() || topic.isEmpty() || clientId.isEmpty()) {
            System.out.println("MQTT config missing. Skip MQTT subscriber init.");
            return;
        }

        System.out.println("Connecting to MQTT broker: " + broker);

        MqttClient client = new MqttClient(broker, clientId);
        client.connect(options);

        System.out.println("Connected to MQTT broker");

        client.subscribe(topic, (t, message) -> {
            String payload = new String(message.getPayload());
            System.out.println("MQTT 수신 → " + t + " | " + payload);
            sensorService.saveFromMqtt(t, payload);
        });

        System.out.println("Subscribed to topic: " + topic);
        System.out.println("MQTT Subscriber Started!");
    }
}

package com.example.smart.mqtt;

import com.example.smart.service.SensorService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

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

            try {
                SensorPayload p = objectMapper.readValue(payload, SensorPayload.class);

                System.out.println(
                        "MQTT 수신 → " + t +
                                " | 온도:" + p.temperature + "°C" +
                                ", 습도:" + p.humidity + "%" +
                                ", 조도:" + p.light + "lux" +
                                ", 공기질:" + p.airQuality +
                                ", 가스:" + p.gas +
                                ", 소음:" + p.noise + "dB"
                );

            } catch (Exception e) {
                System.out.println("MQTT 수신(JSON 파싱 실패) → " + t + " | " + payload);
            }

            sensorService.saveFromMqtt(t, payload);
        });

        System.out.println("Subscribed to topic: " + topic);
        System.out.println("MQTT Subscriber Started!");
    }

    // MQTT JSON payload용 DTO
    static class SensorPayload {
        public double temperature;
        public double humidity;
        public int light;
        public int airQuality;
        public int gas;
        public int noise;
    }
}

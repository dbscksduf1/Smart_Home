package com.example.smart.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public MqttConnectOptions mqttConnectOptions(
            @Value("${mqtt.username:}") String username,
            @Value("${mqtt.password:}") String password
    ) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        if (!username.isEmpty()) {
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }

        return options;
    }
}

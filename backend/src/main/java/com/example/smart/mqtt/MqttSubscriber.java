/** 실사용 X, 실제 센서 사용시에 확장을 위한 코드
 현재 배포 환경에서는 사용하지 않으며, 실제 센서 연동을 고려해 구조만 분리해 둔 코드이다.


 MQTT를 통해 센서 데이터를 수신하기 위한 클래스
 **/

package com.example.smart.mqtt;

import com.example.smart.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;


@Service
@Profile("local")
// 실제 센서 연동 시 local 환경에서만 활성화할 수 있도록 분리한 설정
@RequiredArgsConstructor
public class MqttSubscriber {

    // MQTT 브로커 연결에 필요한 옵션 설정
    private final MqttConnectOptions options;

    // 수신된 센서 데이터를 저장하기 위한 서비스
    private final SensorService sensorService;

    /**
     애플리케이션 시작 시 실행되며,
     MQTT 브로커에 연결한 뒤 센서 관련 토픽을 구독한다.
     **/
    @PostConstruct
    public void init() throws MqttException {

        // 로컬 환경에서 실행되는 MQTT 브로커에 연결
        MqttClient client = new MqttClient("tcp://localhost:1883", "spring-subscriber");
        client.connect(options);

        /*
         * 센서와 관련된 모든 토픽을 구독
         * 예: home/sensor/temp, home/sensor/humidity 등
         */
        client.subscribe("home/sensor/#", (topic, message) -> {
            String payload = new String(message.getPayload());
            System.out.println("MQTT 수신 → " + topic + " | " + payload);

            // 수신된 센서 데이터를 공통 저장 로직으로 전달
            sensorService.saveFromMqtt(topic, payload);
        });

        System.out.println("MQTT Subscriber Started!");
    }
}

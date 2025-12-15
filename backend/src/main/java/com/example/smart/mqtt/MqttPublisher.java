/** 실사용 X, 실제 센서 사용시에 확장을 위한 코드
 현재 프로젝트에서는 실제로 사용하지 않으며,
 향후 실제 센서나 외부 시스템과 연동할 경우를 고려해 구조만 미리 구현해 둔 코드이다.

 MQTT 브로커로 데이터를 전송하기 위한 클래스
 **/

// MQTT 브로커로 데이터를 전송하기 위한 클래스
package com.example.smart.mqtt;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttPublisher {

    // MQTT 브로커 연결에 필요한 옵션 설정
    private final MqttConnectOptions options;

    /**
     * 지정된 토픽으로 메시지를 전송한다.
     *
     * 실제 센서나 외부 장치에서
     * 데이터를 MQTT 방식으로 전달해야 할 경우를 대비한 메서드이다.
     */
    public void publish(String topic, String payload) {
        try {
            // MQTT 브로커에 연결할 클라이언트 생성
            MqttClient client = new MqttClient(
                    "tcp://localhost:1883",
                    "spring-publisher-" + System.currentTimeMillis()
            );

            // 브로커 연결
            client.connect(options);

            // 지정된 토픽으로 메시지 전송
            client.publish(topic, payload.getBytes(), 0, false);

            // 전송 후 연결 종료
            client.disconnect();
        } catch (MqttException e) {
            // MQTT 전송 중 발생한 예외 처리
            e.printStackTrace();
        }
    }
}

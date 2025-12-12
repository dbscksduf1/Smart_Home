package com.example.smart.simulator;

import com.example.smart.domain.SensorData;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SensorSimulator {

    private final Random random = new Random();

    // 🔥 현재 센서값을 저장해두는 변수 (모든 페이지가 같은 값을 보게 됨)
    @Getter
    private SensorData latestData = generateSensorData();

    // 🔥 1분마다 한번만 새로운 센서값 생성
    @Scheduled(fixedRate = 60000)
    public void updateSensorData() {
        latestData = generateSensorData();
        System.out.println("센서값 업데이트됨 → " + latestData);
    }

    // 🔥 랜덤 센서값 생성 함수
    private SensorData generateSensorData() {
        return SensorData.builder()
                .temperature(20 + random.nextDouble() * 10) // 20~30도
                .humidity(30 + random.nextDouble() * 50) // 30~80%
                .light(100 + random.nextInt(900)) // 100~1000 lux
                .gas(10 + random.nextInt(70)) // 10~80 ppm
                .air(10 + random.nextInt(70))
                .noise(30 + random.nextInt(50)) // 30~80 dB
                .time(String.valueOf(System.currentTimeMillis()))
                .build();
    }
}

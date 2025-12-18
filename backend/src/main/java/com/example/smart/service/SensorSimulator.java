package com.example.smart.service;

import com.example.smart.domain.SensorData;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 실제 센서가 없는 환경에서도
 시스템 전체 흐름을 테스트할 수 있도록 만든 센서 시뮬레이터

 1분마다 가상의 센서 값을 생성해
 실제 센서가 동작하는 것과 유사한 환경을 만들어준다.
 **/
@Component
public class SensorSimulator {

    // 랜덤 값 생성을 위한 객체
    private final Random random = new Random();

    /**
     가장 최근에 생성된 센서 데이터를 저장하는 변수

     여러 API나 화면에서 동일한 센서 값을
     함께 사용할 수 있도록 공유 용도로 사용된다.
     **/
    @Getter
    private SensorData latestData = generateSensorData();


    //1분마다 새로운 센서값 생성, 값 갱신
    @Scheduled(fixedRate = 60000)
    public void updateSensorData() {
        latestData = generateSensorData();
        System.out.println("센서값 업데이트됨 → " + latestData);
    }


    //가상의 센서 데이터를 생성하는 메서드이며 아래 범위를 기준으로 랜덤 생성
    private SensorData generateSensorData() {
        return SensorData.builder()
                .temperature(20 + random.nextDouble() * 10)   // 20 ~ 30도
                .humidity(30 + random.nextDouble() * 50)      // 30 ~ 80%
                .light(100 + random.nextInt(900))             // 100 ~ 1000 lux
                .gas(10 + random.nextInt(70))                 // 10 ~ 80 ppm
                .air(10 + random.nextInt(70))                 // 10 ~ 80
                .noise(30 + random.nextInt(50))               // 30 ~ 80 dB
                .time(String.valueOf(System.currentTimeMillis()))   // 현재 시간 표시
                .build();
    }
}

package com.example.smart.controller;

import com.example.smart.dto.SensorResponse;
import com.example.smart.domain.SensorData;
import com.example.smart.service.SensorSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 현재 센서 데이터를 조회하는 컨트롤러이다.

 최신 센서값을 제공하며, 시뮬레이터에 저장된 가장 최근 데이터를 사용한다.
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorController {

    //시뮬레이터에 저장된 최신 센서 값을 사용
    private final SensorSimulator service;

    /**
     가장 최근에 수집된 센서 데이터를 반환한다.

     대시보드, AI분석, 알림, 그래프 등의 화면에서 동일한 데이터를 사용하도록
     하나의 API에서 최신 값을 제공하는 구조로 설계했다.
     **/
    @GetMapping("/latest")
    public ResponseEntity<?> latest() {

        // 시뮬레이터에 저장된 최신 센서 데이터를 가져옴
        SensorData data = service.getLatestData();

        // 아직 센서 데이터가 한 번도 생성되지 않은 경우를 대비한 예외처리
        if (data == null) {
            return ResponseEntity.ok(
                    new SensorResponse(
                            0, 0, 0, 0, 0, 0,
                            "데이터 없음"
                    )
            );
        }

        // 가독성을 위해 소수점 첫째자리까지 출력
        double temp = Math.round(data.getTemperature() * 10) / 10.0;
        double hum = Math.round(data.getHumidity() * 10) / 10.0;
        double light = Math.round(data.getLight() * 10) / 10.0;
        double gas = Math.round(data.getGas() * 10) / 10.0;
        double noise = Math.round(data.getNoise() * 10) / 10.0;
        double air = Math.round(data.getAir() * 10) / 10.0;

        // 센서값들과 측정 시간을 반환
        return ResponseEntity.ok(
                new SensorResponse(
                        temp,
                        hum,
                        light,
                        gas,
                        noise,
                        air,
                        data.getTime()
                )
        );
    }
}

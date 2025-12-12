package com.example.smart.controller;

import com.example.smart.dto.SensorResponse;
import com.example.smart.domain.SensorData;
import com.example.smart.simulator.SensorSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorController {

    private final SensorSimulator simulator;  // 🔥 DB 대신 시뮬레이터에서 값 가져옴

    // 🔥 최신 센서 데이터 조회 (모든 페이지가 이 값 공유)
    @GetMapping("/latest")
    public ResponseEntity<?> latest() {

        SensorData data = simulator.getLatestData();  // 🔥 1분마다 업데이트되는 저장값 사용

        if (data == null) {
            return ResponseEntity.ok(
                    new SensorResponse(
                            0, 0, 0, 0, 0, 0,
                            "데이터 없음"
                    )
            );
        }

        // 소수점 1자리 강제 적용
        double temp = Math.round(data.getTemperature() * 10) / 10.0;
        double hum = Math.round(data.getHumidity() * 10) / 10.0;
        double light = Math.round(data.getLight() * 10) / 10.0;
        double gas = Math.round(data.getGas() * 10) / 10.0;
        double noise = Math.round(data.getNoise() * 10) / 10.0;
        double air = Math.round(data.getAir() * 10) / 10.0;

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

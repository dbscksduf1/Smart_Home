package com.example.smart.controller;

import com.example.smart.dto.*;
import com.example.smart.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sensor")
public class SensorController {

    private final SensorService sensorService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestBody SensorRequest dto) {
        sensorService.saveSensor(dto);
        return ResponseEntity.ok("센서 저장 완료");
    }

    @GetMapping("/latest")
    public ResponseEntity<?> latest() {
        var data = sensorService.latest();

        if (data == null) return ResponseEntity.ok("데이터 없음");

        return ResponseEntity.ok(
                new SensorResponse(
                        data.getTemperature(),
                        data.getHumidity(),
                        data.getLight(),
                        data.getGas(),
                        data.getNoise(),
                        data.getTime()
                )
        );
    }
}

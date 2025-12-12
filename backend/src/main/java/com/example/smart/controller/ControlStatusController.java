package com.example.smart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/control")
public class ControlStatusController {

    // 지금은 실제 LED 제어가 없으므로 임시 응답
    @GetMapping("/status")
    public ResponseEntity<?> status() {

        return ResponseEntity.ok(
                Map.of(
                        "led", "green",
                        "buzzer", "off"
                )
        );
    }
}

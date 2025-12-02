package com.example.smart.controller;

import com.example.smart.dto.LedCommand;
import com.example.smart.service.LedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/led")
public class LedController {

    private final LedService ledService;

    @PostMapping("/control")
    public ResponseEntity<?> control(@RequestBody LedCommand cmd) {
        ledService.update(cmd);
        return ResponseEntity.ok("LED 명령 업데이트 완료");
    }

    @GetMapping("/current")
    public ResponseEntity<?> current() {
        return ResponseEntity.ok(ledService.getCurrent());
    }
}

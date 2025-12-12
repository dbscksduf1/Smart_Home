package com.example.smart.controller;

import com.example.smart.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {

    private final AlertService alertService;

    // 🔔 간단 알림 1줄
    @GetMapping("/short")
    public String shortAlert() {
        return alertService.shortAlert();
    }
}

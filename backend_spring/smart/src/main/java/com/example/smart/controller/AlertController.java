package com.example.smart.controller;

import com.example.smart.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/alert/latest")
    public String latest() {
        return alertService.getLatestAlert();
    }
}

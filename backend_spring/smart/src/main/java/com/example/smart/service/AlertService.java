package com.example.smart.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    @Getter
    private String latestAlert = "정상 상태입니다.";

    public void sendAlert(String msg) {
        this.latestAlert = msg;
    }
}

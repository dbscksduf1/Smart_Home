package com.example.smart.controller;

import com.example.smart.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
  현재 실내 환경 상태를 간단한 알림 문장으로 제공하는 컨트롤러이다.

  알림 화면에서 한 줄로 현재 상태를 빠르게 확인할 수 있도록 사용된다.
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {

    // 환경 상태에 따라 알림 문장을 생성
    private final AlertService alertService;

    /**
      현재 환경 상태를
      한 줄짜리 간단한 알림 문장으로 반환한다.
     **/
    @GetMapping("/short")
    public String shortAlert() {
        return alertService.shortAlert();
    }
}

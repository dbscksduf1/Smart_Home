package com.example.smart.service;

import com.example.smart.domain.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 현재 센서 데이터를 기준으로 사용자에게 전달할 한 줄 알림 문장을 생성하는 서비스이다.

 현재 가장 주의가 필요한 요소를 우선적으로 알려준다.
 **/
@Service
@RequiredArgsConstructor
public class AlertService {

    // 가장 최근 센서 데이터를 가져오기 위한 서비스
    private final SensorService sensorService;


    //현재 환경 상태를 기준으로 한 줄짜리 알림 문장을 생성
    public String shortAlert() {

        // 가장 최근 센서 데이터를 가져온다
        SensorData d = sensorService.latest();
        if (d == null) return "데이터가 없습니다.";


        // 여러 센서 중에서 위험도가 높은 항목을 우선적으로 판단


        // 가스
        if (d.getGas() >= 60)
            return "가스 농도가 매우 높습니다. 즉시 환기하세요.";

        if (d.getGas() >= 40)
            return "가스 농도가 높습니다. 환기하는 것을 권장합니다.";

        // 온도
        if (d.getTemperature() >= 29)
            return "온도가 높습니다. 실내 온도를 조절해주세요.";

        if (d.getTemperature() <= 17)
            return "온도가 낮습니다. 난방 조절이 필요합니다.";

        // 소음
        if (d.getNoise() >= 85)
            return "소음 수준이 매우 높습니다. 주의하세요.";

        if (d.getNoise() >= 75)
            return "소음 수준이 높습니다. 조용한 환경 유지가 필요합니다.";

        // 습도
        if (d.getHumidity() <= 30)
            return "습도가 낮습니다. 가습이 필요할 수 있습니다.";

        if (d.getHumidity() >= 70)
            return "습도가 높습니다. 환기가 필요할 수 있습니다.";

        // 조도
        if (d.getLight() <= 100)
            return "조도가 낮습니다. 조명이 필요합니다.";

        if (d.getLight() >= 600)
            return "조도가 높습니다. 눈의 피로에 주의하세요.";

        //모든 센서 정상 수치
        return "전체적으로 쾌적합니다. 현재 상태를 유지하세요.";
    }
}

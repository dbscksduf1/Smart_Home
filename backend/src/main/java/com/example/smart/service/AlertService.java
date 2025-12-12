package com.example.smart.service;

import com.example.smart.domain.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final SensorService sensorService;

    public String shortAlert() {

        SensorData d = sensorService.latest();
        if (d == null) return "데이터가 없습니다.";

        // 1) 우선순위 (위험도 높은 순)
        // 가스 → 온도 → 소음 → 습도 → 조도 순으로 판단

        // 가스 위험
        if (d.getGas() >= 60)
            return "가스 농도가 매우 높습니다. 즉시 환기하세요.";

        if (d.getGas() >= 40)
            return "가스 농도가 높습니다. 환기하는 것을 권장합니다.";

        // 온도 위험
        if (d.getTemperature() >= 29)
            return "온도가 높습니다. 실내 온도를 조절해주세요.";

        if (d.getTemperature() <= 17)
            return "온도가 낮습니다. 난방 조절이 필요합니다.";

        // 소음 위험
        if (d.getNoise() >= 85)
            return "소음 수준이 매우 높습니다. 주의하세요.";

        if (d.getNoise() >= 75)
            return "소음 수준이 높습니다. 조용한 환경 유지가 필요합니다.";

        // 습도 위험
        if (d.getHumidity() <= 30)
            return "습도가 낮습니다. 가습이 필요할 수 있습니다.";

        if (d.getHumidity() >= 70)
            return "습도가 높습니다. 환기가 필요할 수 있습니다.";

        // 조도는 아주 기본만
        if (d.getLight() <= 100)
            return "조도가 낮습니다. 조명이 필요합니다.";

        if (d.getLight() >= 600)
            return "조도가 높습니다. 눈의 피로에 주의하세요.";

        // 모든 센서 정상
        return "전체적으로 쾌적합니다. 현재 상태를 유지하세요.";
    }
}

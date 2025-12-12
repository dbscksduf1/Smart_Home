package com.example.smart.service;

import com.example.smart.domain.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorAnalysisService {

    private final SensorService sensorService;

    // 🔥 최신 센서 데이터 가져오기
    public SensorData latestSensorData() {
        return sensorService.latest();
    }

    // 🔥 불쾌지수 계산
    public double calculateDiscomfortIndex(double temp, double humidity) {
        return 1.8 * temp - 0.55 * (1 - humidity / 100.0) * (1.8 * temp - 26) + 32;
    }

    public String classifyDiscomfort(double di) {
        if (di < 68) return "쾌적";
        if (di < 75) return "보통";
        if (di < 80) return "주의";
        if (di < 84) return "상당히 불쾌";
        return "위험";
    }

    // 🔥 공기질 추가
    public String classifyAir(int air) {
        if (air <= 30) return "좋음";
        if (air <= 60) return "보통";
        return "나쁨";
    }

    public String classifyLight(int light) {
        if (light < 150) return "어두움";
        if (light < 350) return "보통";
        return "밝음";
    }

    public String classifyGas(int gas) {
        if (gas < 30) return "정상";
        if (gas < 60) return "주의";
        return "위험";
    }

    public String classifyNoise(int noise) {
        if (noise < 60) return "정상";
        if (noise < 80) return "주의";
        return "높음";
    }

    public String classifyTemperature(double temp) {
        if (temp < 18) return "낮음";
        if (temp <= 26) return "정상";
        return "높음";
    }

    public String classifyHumidity(double humidity) {
        if (humidity < 40) return "낮음";
        if (humidity <= 60) return "정상";
        return "높음";
    }

    // 🔥 공기질을 포함한 전체 상태 요약
    public String classifyOverallStatus(double temp, double hum, int light, int gas, int noise, int air) {
        return String.format(
                "온도: %s / 습도: %s / 조도: %s / 가스: %s / 소음: %s / 공기질: %s",
                classifyTemperature(temp),
                classifyHumidity(hum),
                classifyLight(light),
                classifyGas(gas),
                classifyNoise(noise),
                classifyAir(air)
        );
    }

    // 🔥 AI 환경 분석 리포트 (공기질 포함)
    public String buildEnvironmentReport(double temp, double humidity, int light, int gas, int noise, int air) {

        double di = calculateDiscomfortIndex(temp, humidity);
        String discomfort = classifyDiscomfort(di);
        String airStatus = classifyAir(air);

        return String.format(
                """
                온도 %.1f°C, 습도 %.1f%%, 조도 %d lx, 가스 %d, 소음 %d dB, 공기질 %d(%s), 불쾌지수 %.1f(%s).
                위 환경을 3줄 이하로 간단히 요약하고,
                사용자가 바로 이해할 수 있도록 환경 관리 조언만 짧게 작성하세요.
                """,
                temp, humidity, light, gas, noise, air, airStatus, di, discomfort
        );
    }

}

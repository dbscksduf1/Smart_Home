package com.example.smart.service;

import com.example.smart.domain.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 센서 데이터를 해석하고 현재 실내 환경 상태를 판단하는 역할을 담당하는 서비스이다.

 각 센서 값에 대해 사람이 이해하기 쉬운 기준으로 상태를 나누고,
 AI 분석에 사용할 수 있는 설명 문장도 함께 만들어준다.
 **/
@Service
@RequiredArgsConstructor
public class SensorAnalysisService {

    // 가장 최근 센서 데이터를 가져오기 위한 서비스
    private final SensorService sensorService;

    //가장 최근에 수집된 센서 데이터를 반환
    public SensorData latestSensorData() {
        return sensorService.latest();
    }

    //온도와 습도를 기반으로 불쾌지수를 계산
    public double calculateDiscomfortIndex(double temp, double humidity) {
        return 1.8 * temp - 0.55 * (1 - humidity / 100.0) * (1.8 * temp - 26) + 32;
    }


    // 불쾌지수를 기준으로 현재 환경을 단계별 상태로 구분
    public String classifyDiscomfort(double di) {
        if (di < 68) return "쾌적";
        if (di < 75) return "보통";
        if (di < 80) return "주의";
        if (di < 84) return "상당히 불쾌";
        return "위험";
    }


    // 공기질 센서 값을 기준으로 실내 공기 상태를 간단히 분류
    public String classifyAir(int air) {
        if (air <= 30) return "좋음";
        if (air <= 60) return "보통";
        return "나쁨";
    }


    // 조도 값을 기준으로 실내 밝기 상태를 판단
    public String classifyLight(int light) {
        if (light < 150) return "어두움";
        if (light < 350) return "보통";
        return "밝음";
    }


    // 가스 센서 수치를 기준으로 안전 상태를 간단히 판단
    public String classifyGas(int gas) {
        if (gas < 30) return "정상";
        if (gas < 60) return "주의";
        return "위험";
    }


    // 소음 값을 기준으로 현재 환경의 소음 수준을 판단
    public String classifyNoise(int noise) {
        if (noise < 60) return "정상";
        if (noise < 80) return "주의";
        return "높음";
    }


    // 온도 값을 기준으로 실내 온도 상태를 구분
    public String classifyTemperature(double temp) {
        if (temp < 18) return "낮음";
        if (temp <= 26) return "정상";
        return "높음";
    }


    // 습도 값을 기준으로 실내 습도 상태를 판단
    public String classifyHumidity(double humidity) {
        if (humidity < 40) return "낮음";
        if (humidity <= 60) return "정상";
        return "높음";
    }


    // 모든 센서 값을 종합해서 현재 실내 환경상태를 한줄로 요약
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

    /**
     AI에게 전달할 환경 분석용 문장을 생성한다.

     센서 값을 사람이 읽기 쉬운 형태로 정리하고,
     AI가 짧고 이해하기 쉬운 설명을 하도록 요청한다.
     **/
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

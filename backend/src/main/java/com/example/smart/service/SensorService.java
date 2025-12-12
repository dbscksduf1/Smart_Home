package com.example.smart.service;

import com.example.smart.domain.SensorData;
import com.example.smart.dto.SensorRequest;
import com.example.smart.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository repo;



    // 🔥 센서 저장 로직 (MQTT / HTTP 업로드 공통)
    public void saveSensor(SensorRequest dto) {

        SensorData data = SensorData.builder()
                .temperature(dto.getTemperature())
                .humidity(dto.getHumidity())
                .light(dto.getLight())
                .gas(dto.getGas())
                .noise(dto.getNoise())
                .air(dto.getAir())            // ✅ 공기질 저장 추가됨
                .time(dto.getTime())
                .build();

        repo.save(data);

        // ------------------------------------------------
        // 🔥 환경 자동 제어 로직
        // ------------------------------------------------

        double thi = 0.81 * dto.getTemperature()
                + 0.01 * dto.getHumidity() * (0.99 * dto.getTemperature() - 14.3)
                + 46.3;

        String thiLevel;
        if (thi < 68) thiLevel = "good";
        else if (thi < 75) thiLevel = "normal";
        else thiLevel = "bad";

        String gasLevel;
        if (dto.getGas() < 100) gasLevel = "good";
        else if (dto.getGas() < 150) gasLevel = "normal";
        else gasLevel = "bad";

        String noiseLevel;
        if (dto.getNoise() < 30) noiseLevel = "good";
        else if (dto.getNoise() < 60) noiseLevel = "normal";
        else noiseLevel = "bad";



        // 원인
        String reason = "good";
        if (gasLevel.equals("bad")) reason = "airquality";
        else if (thiLevel.equals("bad")) reason = "discomfort";
        else if (noiseLevel.equals("bad")) reason = "noise";
        else if (gasLevel.equals("normal")) reason = "airquality";
        else if (thiLevel.equals("normal")) reason = "discomfort";
        else if (noiseLevel.equals("normal")) reason = "noise";




    }

    // 🔥 최신 데이터 1개
    public SensorData latest() {
        return repo.findTopByOrderByIdDesc();
    }

    // 🔥 그래프용 전체 데이터 조회
    public List<SensorData> findAll() {
        return repo.findAll();
    }

    // 🔥 MQTT 저장용 (원하면 공기질 토픽도 추가 가능)
    public void saveFromMqtt(String topic, String value) {
        SensorRequest req = new SensorRequest();

        switch (topic) {
            case "home/sensor/temp":
                req.setTemperature(Double.parseDouble(value));
                break;
            case "home/sensor/humidity":
                req.setHumidity(Double.parseDouble(value));
                break;
            case "home/sensor/light":
                req.setLight(Integer.parseInt(value));
                break;
            case "home/sensor/gas":
                req.setGas(Integer.parseInt(value));
                break;
            case "home/sensor/noise":
                req.setNoise(Integer.parseInt(value));
                break;
            // case "home/sensor/air": req.setAir(Integer.parseInt(value)); break;   // ← 필요 시 추가
            default:
                System.out.println("⚠ 알 수 없는 센서 타입: " + topic);
                return;
        }

        req.setTime(String.valueOf(System.currentTimeMillis()));
        saveSensor(req);
    }
}

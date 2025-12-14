package com.example.smart.service;

import com.example.smart.domain.SensorData;
import com.example.smart.dto.SensorRequest;
import com.example.smart.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 센서 데이터를 저장하고 조회하는 역할을 담당하는 서비스이다.
 센서 데이터를 하나의 로직으로 처리하도록 구성되어 있다.
 **/
@Service
@RequiredArgsConstructor
public class SensorService {

    // 센서 데이터를 DB에 저장하고 조회하기 위한 저장소
    private final SensorRepository repo;


    // 센서 데이터를 저장
    public void saveSensor(SensorRequest dto) {

        // 전달받은 센서 값을 엔티티로 변환해 저장
        SensorData data = SensorData.builder()
                .temperature(dto.getTemperature())
                .humidity(dto.getHumidity())
                .light(dto.getLight())
                .gas(dto.getGas())
                .noise(dto.getNoise())
                .air(dto.getAir())
                .time(dto.getTime())
                .build();

        repo.save(data);
    }


    // 가장 최근에 저장된 센서데이터 1건을 반환
    //(대시보드나 실시간 화면에서 현재 상태를 표시할때 사용)
    public SensorData latest() {
        return repo.findTopByOrderByIdDesc();
    }


    // 저장된 전체 센서 데이터 조회
    //(그래프 화면에서 시간에 따른 변화를 확인할 때 사용)
    public List<SensorData> findAll() {
        return repo.findAll();
    }


    // 수신된 센서 데이터 저장
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
            case "home/sensor/air":
                req.setAir(Integer.parseInt(value));
                break;

            default:
                System.out.println("알 수 없는 센서 타입: " + topic);
                return;
        }

        // 수신한 시간을 저장
        req.setTime(String.valueOf(System.currentTimeMillis()));

        // 변환된 센서 데이터를 공통 저장 로직으로 전달
        saveSensor(req);
    }
}

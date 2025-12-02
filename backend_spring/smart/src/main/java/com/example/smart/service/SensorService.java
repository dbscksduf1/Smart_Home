package com.example.smart.service;

import com.example.smart.domain.SensorData;
import com.example.smart.dto.LedCommand;
import com.example.smart.dto.SensorRequest;
import com.example.smart.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository repo;
    private final LedService ledService;
    private final AlertService alertService;

    public void saveSensor(SensorRequest dto) {

        SensorData data = SensorData.builder()
                .temperature(dto.getTemperature())
                .humidity(dto.getHumidity())
                .light(dto.getLight())
                .gas(dto.getGas())
                .noise(dto.getNoise())
                .time(dto.getTime())
                .build();

        repo.save(data);

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

        String finalColor = "green";
        if (gasLevel.equals("bad") || thiLevel.equals("bad") || noiseLevel.equals("bad")) {
            finalColor = "red";
        } else if (gasLevel.equals("normal") || thiLevel.equals("normal") || noiseLevel.equals("normal")) {
            finalColor = "yellow";
        }

        int brightness;
        int lux = dto.getLight();
        if (lux < 150) brightness = 100;
        else if (lux > 500) brightness = 10;
        else brightness = 60;

        boolean blink = noiseLevel.equals("bad");

        String reason = "good";
        if (gasLevel.equals("bad")) reason = "airquality";
        else if (thiLevel.equals("bad")) reason = "discomfort";
        else if (noiseLevel.equals("bad")) reason = "noise";
        else if (gasLevel.equals("normal")) reason = "airquality";
        else if (thiLevel.equals("normal")) reason = "discomfort";
        else if (noiseLevel.equals("normal")) reason = "noise";

        LedCommand cmd = new LedCommand();
        cmd.setColor(finalColor);
        cmd.setBrightness(brightness);
        cmd.setBlink(blink);
        cmd.setReason(reason);

        ledService.update(cmd);

        if (reason.equals("airquality") && gasLevel.equals("bad"))
            alertService.sendAlert("공기질이 매우 좋지 않습니다. 환기 또는 공기청정이 필요합니다.");

        else if (reason.equals("discomfort") && thiLevel.equals("bad"))
            alertService.sendAlert("불쾌지수가 매우 높습니다. 냉난방 온도를 조절해주세요.");

        else if (reason.equals("noise") && noiseLevel.equals("bad"))
            alertService.sendAlert("소음 수준이 매우 높습니다. 정숙한 환경 조성이 필요합니다.");

        else if (finalColor.equals("yellow"))
            alertService.sendAlert("환경 상태가 보통입니다. 상황을 지속적으로 모니터링하세요.");

        else
            alertService.sendAlert("환경 상태가 정상입니다.");
    }

    public SensorData latest() {
        return repo.findAll()
                .stream()
                .reduce((first, second) -> second)
                .orElse(null);
    }
}

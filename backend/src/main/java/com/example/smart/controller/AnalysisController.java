package com.example.smart.controller;

import com.example.smart.domain.SensorData;
import com.example.smart.service.OpenAIService;
import com.example.smart.service.SensorAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    private final SensorAnalysisService analysisService;
    private final OpenAIService openAIService;

    @GetMapping("/report")
    public ResponseEntity<?> report() {

        SensorData data = analysisService.latestSensorData();

        if (data == null) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "데이터 없음",
                            "ai", "센서 데이터가 없어 분석을 진행할 수 없습니다."
                    )
            );
        }

        // 🔥 센서별 상태 요약 생성 (공기질 추가!)
        String overall = analysisService.classifyOverallStatus(
                data.getTemperature(),
                data.getHumidity(),
                data.getLight(),
                data.getGas(),
                data.getNoise(),
                data.getAir()           // ← 추가됨
        );

        // 🔥 GPT 프롬프트 생성 (공기질 추가!)
        String prompt = analysisService.buildEnvironmentReport(
                data.getTemperature(),
                data.getHumidity(),
                data.getLight(),
                data.getGas(),
                data.getNoise(),
                data.getAir()           // ← 추가됨
        );

        // 🔥 GPT 분석
        String ai = openAIService.ask(prompt);

        return ResponseEntity.ok(
                Map.of(
                        "status", overall,
                        "ai", ai
                )
        );
    }
}

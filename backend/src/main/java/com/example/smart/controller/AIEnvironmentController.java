package com.example.smart.controller;

import com.example.smart.domain.SensorData;
import com.example.smart.service.SensorAnalysisService;
import com.example.smart.service.OpenAIService;
import com.example.smart.simulator.SensorSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIEnvironmentController {

    private final SensorSimulator simulator;              // 🔥 DB 대신 시뮬레이터 사용
    private final SensorAnalysisService analysisService;
    private final OpenAIService openAIService;

    @GetMapping("/environment")
    public String analyzeEnvironment() {

        // 🔥 최신 센서값: 반드시 시뮬레이터에서 읽어야 한다!
        SensorData latest = simulator.getLatestData();

        if (latest == null) {
            return "현재 센서 데이터가 존재하지 않습니다.";
        }

        // 🔥 GPT 프롬프트 생성
        String prompt = analysisService.buildEnvironmentReport(
                latest.getTemperature(),
                latest.getHumidity(),
                latest.getLight(),
                latest.getGas(),
                latest.getNoise(),
                latest.getAir()
        );

        // 🔥 AI 호출
        String aiResponse = openAIService.ask(prompt);

        return aiResponse;
    }
}

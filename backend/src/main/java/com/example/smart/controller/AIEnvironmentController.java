package com.example.smart.controller;

import com.example.smart.domain.SensorData;
import com.example.smart.service.SensorAnalysisService;
import com.example.smart.service.OpenAIService;
import com.example.smart.simulator.SensorSimulator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 현재 센서 데이터를 기반으로 AI가 실내 환경 상태를 분석해서 문장 형태로 알려주는 컨트롤러이다.

 부품 부족으로 인해 센서 시뮬레이터를 통해 1분마다 가장 최근 데이터를 가져오도록 구성했다.
 **/


@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIEnvironmentController {

    // 실제 센서 대신 테스트용으로 만든 시뮬레이터에서 센서 데이터를 가져옴
    private final SensorSimulator simulator;

    // 센서 값들을 종합해서 분석용 문장을 만들어주는 서비스
    private final SensorAnalysisService analysisService;

    // 생성된 문장을 AI에게 전달하고 응답을 받아오는 서비스
    private final OpenAIService openAIService;

    /**
      현재 실내 환경 상태를 AI가 분석해서 사용자에게 간단한 분석 및 조언을 제공한다.
     **/
    @GetMapping("/environment")
    public String analyzeEnvironment() {

        // 가장 최근에 수집된 센서 데이터를 가져옴
        SensorData latest = simulator.getLatestData();

        // 아직 센서 데이터가 한 번도 생성되지 않은 경우를 대비한 예외 처리
        if (latest == null) {
            return "현재 센서 데이터가 존재하지 않습니다.";
        }

        // 센서 값들을 바탕으로 AI에게 전달할 분석용 문장을 생성
        String prompt = analysisService.buildEnvironmentReport(
                latest.getTemperature(),
                latest.getHumidity(),
                latest.getLight(),
                latest.getGas(),
                latest.getNoise(),
                latest.getAir()
        );

        // 생성된 문장을 AI에게 전달하고 분석 결과를 받아옴
        String aiResponse = openAIService.ask(prompt);

        // AI가 생성한 최종 분석 결과를 전달
        return aiResponse;
    }
}

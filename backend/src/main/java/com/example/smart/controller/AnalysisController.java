package com.example.smart.controller;

import com.example.smart.domain.SensorData;
import com.example.smart.service.OpenAIService;
import com.example.smart.service.SensorAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 현재 센서 데이터를 종합해서 실내 환경 상태를 분석하고 결과를 반환하는 컨트롤러이다.

 숫자 형태의 센서 값뿐만 아니라, 사람이 이해하기 쉬운 상태 요약과 AI 분석 결과를 함께 제공한다.
 **/

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    // 센서 데이터 분석 및 상태 분류를 담당
    private final SensorAnalysisService analysisService;

    // 분석 결과를 문장으로 설명하기 위해 AI를 호출
    private final OpenAIService openAIService;

    /**
     가장 최근 센서 데이터를 기준으로
     현재 실내 환경 상태와 AI 분석 결과를 반환한다.
     **/
    @GetMapping("/report")
    public ResponseEntity<?> report() {

        // 가장 최근에 수집된 센서 데이터를 가져옴
        SensorData data = analysisService.latestSensorData();

        // 센서 데이터가 아직 없는 경우를 대비한 예외처리
        if (data == null) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "데이터 없음",
                            "ai", "센서 데이터가 없어 분석을 진행할 수 없습니다."
                    )
            );
        }

        // 각 센서 값을 종합해 현재 환경 상태를 간단한 문장으로 정리
        String overall = analysisService.classifyOverallStatus(
                data.getTemperature(),
                data.getHumidity(),
                data.getLight(),
                data.getGas(),
                data.getNoise(),
                data.getAir()
        );

        // 센서 데이터를 바탕으로 AI에게 전달할 분석용 문장 생성
        String prompt = analysisService.buildEnvironmentReport(
                data.getTemperature(),
                data.getHumidity(),
                data.getLight(),
                data.getGas(),
                data.getNoise(),
                data.getAir()
        );

        // 생성된 문장을 AI에게 전달해 환경 분석 결과를 받음
        String ai = openAIService.ask(prompt);

        // 상태 요약과 AI 분석 결과를 반환
        return ResponseEntity.ok(
                Map.of(
                        "status", overall,
                        "ai", ai
                )
        );
    }
}

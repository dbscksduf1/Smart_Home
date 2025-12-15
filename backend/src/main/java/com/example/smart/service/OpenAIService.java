package com.example.smart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 OpenAI API를 호출해서 센서 데이터를 기반으로 한 분석 결과를 문장 형태로 받아오는 서비스이다.

 AI가 판단하는것이 아닌 SensorAnalysisService파일에서 분류된 결과를 바탕으로 사용자에게
 이해하기 쉬운 설명과 조언을 제공하는 용도로 사용한다.
 **/
@Service
@RequiredArgsConstructor
public class OpenAIService {

    // OpenAI API Key
    @Value("${openai.api.key}")
    private String API_KEY;

    // 외부 API 호출
    private final RestTemplate restTemplate = new RestTemplate();


     //생성된 프롬프트를 OpenAI API에 전달하고 AI가 생성한 응답 문장을 반환

    public String ask(String prompt) {


        String url = "https://api.openai.com/v1/chat/completions";

        // OpenAI API 요청에 사용
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("max_tokens", 200);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // OpenAI API 호출
            ResponseEntity<Map> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);








            //예외처리

            // 응답이 비어 있는 경우
            if (response.getBody() == null) {
                return "AI 응답이 비어 있습니다.";
            }

            // choices 필드 확인
            Object choicesObj = response.getBody().get("choices");
            if (!(choicesObj instanceof List)) {
                return "AI 응답 형식 오류";
            }

            List choices = (List) choicesObj;
            if (choices.isEmpty()) {
                return "AI 응답 없음";
            }

            // 첫 번째 응답 메시지 추출
            Object msgObj = ((Map) choices.get(0)).get("message");
            if (!(msgObj instanceof Map)) {
                return "AI 메시지 구조 오류";
            }

            // 실제 응답 내용 추출
            Object contentObj = ((Map) msgObj).get("content");
            if (contentObj == null) {
                return "AI가 내용을 생성하지 않았습니다.";
            }

            // 최종 응답 문장 반환
            return contentObj.toString().trim();

        } catch (Exception e) {
            // API 호출 중 오류 발생
            e.printStackTrace();
            return "AI 분석 중 오류 발생. 잠시 후 다시 시도해주세요.";
        }
    }
}

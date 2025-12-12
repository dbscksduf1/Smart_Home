package com.example.smart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api.key}")
    private String API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔥 프롬프트를 받아 GPT에게 질문하는 메서드
    public String ask(String prompt) {

        String url = "https://api.openai.com/v1/chat/completions";

        // 🔥 요청 Body 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("max_tokens", 200);

        // 🔥 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 🔥 OpenAI API 요청
            ResponseEntity<Map> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getBody() == null) {
                return "AI 응답이 비어 있습니다.";
            }

            Object choicesObj = response.getBody().get("choices");
            if (!(choicesObj instanceof List)) {
                return "AI 응답 형식 오류";
            }

            List choices = (List) choicesObj;
            if (choices.isEmpty()) {
                return "AI 응답 없음";
            }

            Object msgObj = ((Map) choices.get(0)).get("message");
            if (!(msgObj instanceof Map)) {
                return "AI 메시지 구조 오류";
            }

            Object contentObj = ((Map) msgObj).get("content");
            if (contentObj == null) {
                return "AI가 내용을 생성하지 않았습니다.";
            }

            return contentObj.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 분석 중 오류 발생. 잠시 후 다시 시도해주세요.";
        }
    }
}

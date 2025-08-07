package com.waguwagu.weat.domain.analysis.adaptor;

import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AIServiceAdaptor {

    private final RestTemplate aiRestTemplate;

    @Value("${ai.service.base-url}")
    private String baseURL;

    public AIAnalysisDTO.Response requestAnalysis(AIAnalysisDTO.Request payload) {
        String url = baseURL + "/api/analyze";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AIAnalysisDTO.Request > request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<AIAnalysisDTO.Response> response = aiRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    AIAnalysisDTO.Response.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("AI 분석 요청 실패", e);
            throw new RuntimeException("AI 분석 요청 중 오류 발생", e);
        }
    }
}
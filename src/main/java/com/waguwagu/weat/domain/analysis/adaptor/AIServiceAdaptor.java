package com.waguwagu.weat.domain.analysis.adaptor;

import com.waguwagu.weat.domain.analysis.exception.AIServerException;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;

import com.waguwagu.weat.domain.analysis.model.dto.ValidationDTO;
import com.waguwagu.weat.domain.common.dto.AIErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AIServiceAdaptor {
    @Value("${ai.service.base-url}")
    private String baseURL;

    @Value("${ai.service.uri.analysis}")
    private String analysisUri;

    @Value("${ai.service.uri.validation}")
    private String validationUri;

    private final RestTemplate aiRestTemplate;

    private final WebClient aiWebClient;


    public AIAnalysisDTO.Response requestAnalysis(AIAnalysisDTO.Request payload) {
        String url = baseURL + analysisUri;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AIAnalysisDTO.Request > request = new HttpEntity<>(payload, headers);
        log.info("payload => {}", payload);
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

    public ValidationDTO.Response requestValidation(ValidationDTO.Request payload) {

        return aiWebClient.post()
                .uri(validationUri)
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(AIErrorResponse.class).flatMap(error -> {
                            log.error("AI 오류 - code: {}, message: {}", error.getCode(), error.getMessage());
                            return Mono.error(new AIServerException(error.getMessage()));
                        }))
                .bodyToMono(ValidationDTO.Response.class)
                .block();
    }

}
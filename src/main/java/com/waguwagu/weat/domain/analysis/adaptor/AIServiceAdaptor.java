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

import java.time.Duration;

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

    private final WebClient aiWebClient;


    public AIAnalysisDTO.Response requestAnalysis(AIAnalysisDTO.Request payload) {
        return aiWebClient.post()
                .uri(analysisUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(AIErrorResponse.class)
                                .flatMap(error -> {
                                    log.error("AI 오류 - code: {}, message: {}", error.getCode(), error.getMessage());
                                    return Mono.error(new AIServerException(error.getMessage()));
                                })
                )
                .bodyToMono(AIAnalysisDTO.Response.class)
                .doOnSubscribe(s -> log.info("payload => {}", payload))
                .block();
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


    /** 공통 POST(JSON) */
    public <Request, Response> Mono<Response> postJson(String uri, Request payload, Class<Response> responseType, Duration timeout) {
        return aiWebClient.post()
                .uri(uri)
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(AIErrorResponse.class)
                                .flatMap(err -> {
                                    log.error("[AI ERROR] uri={}, code={}, message={}", uri, err.getCode(), err.getMessage());
                                    return Mono.error(new AIServerException(err.getMessage()));
                                })
                )
                .bodyToMono(responseType)
                .timeout(timeout)
                // 로그용 (임시)
                .doOnSubscribe(s -> log.info("[AI REQ] uri={}, payload={}", uri, payload))
                .doOnNext(response -> log.info("[AI OK] uri={}, response={}", uri, response))
                .doOnError(e -> log.error("[AI FAIL] uri={}, error={}", uri, e.toString()));
    }

}
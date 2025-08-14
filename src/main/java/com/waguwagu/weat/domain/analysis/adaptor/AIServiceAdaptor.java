package com.waguwagu.weat.domain.analysis.adaptor;

import com.waguwagu.weat.domain.analysis.exception.AIServerException;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;

import com.waguwagu.weat.domain.analysis.model.dto.ValidationDTO;
import com.waguwagu.weat.domain.common.dto.AIErrorResponse;
import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
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


    /** 공통 POST(JSON) */
    public <Req, Res> Mono<Res> postJson(String uri, Req payload, Class<Res> responseType, Duration timeout) {
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
                .doOnNext(res -> log.info("[AI OK] uri={}, response={}", uri, res))
                .doOnError(e -> log.error("[AI FAIL] uri={}, error={}", uri, e.toString()));
    }


    /** Mono<T>를 DeferredResult<ResponseDTO<T>>로 변환 */
    public static <T> DeferredResult<ResponseDTO<T>> toDeferredResponseDTO(Mono<T> mono, long timeoutMillis) {
        DeferredResult<ResponseDTO<T>> dr = new DeferredResult<>(timeoutMillis);
        mono.subscribe(
                result -> dr.setResult(ResponseDTO.of(result)),  // 성공
                error  -> dr.setErrorResult(error)        // 실패
        );
        return dr;
    }

}
package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.MemberNotFoundException;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalysisAsyncExecutor {

    private final AnalysisResultRepository analysisResultRepository;
    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;
    private final AnalysisResultDetailRepository analysisResultDetailRepository;
    private final AnalysisBasisRepository analysisBasisRepository;
    private final AnalysisRepository analysisRepository;
    private final GroupRepository groupRepository;

    private final AIServiceAdaptor aiServiceAdaptor;
    private final PlatformTransactionManager transactionManager;
    private final Executor analysisExecutor;

    @Value("${ai.service.uri.analysis}")
    private String analysisUri;

    // TODO: 추후 스레드 풀 설정
    public void startAnalysisAsync(AIAnalysisDTO.Request request) {
        CompletableFuture.runAsync(() -> {

            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));

            Analysis analysis = analysisRepository.findByGroupGroupId(group.getGroupId())
                    .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(group.getGroupId()));

            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            log.info("request => {}", request);

            try {
                AIAnalysisDTO.Response response =
                        aiServiceAdaptor.postJson(
                                analysisUri,
                                request,
                                AIAnalysisDTO.Response.class,
                                Duration.ofSeconds(60)
                        ).toFuture().get(60, TimeUnit.SECONDS);

                log.info("response => {}", response);

                // 트랜잭션 시작
                tx.executeWithoutResult(status -> {
                    // 결과 저장
                    AnalysisResult result = analysisResultRepository.save(
                            AnalysisResult.builder()
                                    .group(group)
                                    .analysis(analysis)
                                    .build()
                    );

                    for (var detail : response.getAnalysisResult().getAnalysisResultDetailList()) {
                        // 장소 저장
                        var placeInfo = detail.getPlace();
                        Place place = placeRepository.save(Place.builder()
                                .placeName(placeInfo.getPlaceName())
                                .placeRoadnameAddress(placeInfo.getPlaceRoadNameAddress())
                                .build());

                        if (placeInfo.getPlaceImageList() != null) {
                            // 이미지 저장
                            for (var image : placeInfo.getPlaceImageList()) {
                                placeImageRepository.save(PlaceImage.builder()
                                        .place(place)
                                        .placeImageUrl(image.getPlaceImageUrl())
                                        .build());
                            }
                        }
                        // 결과 상세 저장
                        AnalysisResultDetail resultDetail = analysisResultDetailRepository.save(
                                AnalysisResultDetail.builder()
                                        .analysisResult(result)
                                        .analysisResultDetailContent(detail.getAnalysisResultDetailContent())
                                        .place(place)
                                        .build()
                        );

                        // 분석 근거 저장
                        for (var basis : detail.getAnalysisBasisList()) {
                            analysisBasisRepository.save(
                                    AnalysisBasis.builder()
                                            .analysisResultDetail(resultDetail)
                                            .analysisBasisType(basis.getAnalysisBasisType())
                                            .analysisBasisContent(basis.getAnalysisBasisContent())
                                            .build()
                            );
                        }
                    }

                    // 분석 상태 완료 처리
                    analysis.setAnalysisStatus(AnalysisStatus.COMPLETED);
                    analysisRepository.save(analysis);
                });

            } catch (Exception e) {
                log.error("AI 분석 실패", e);
                // 분석 상태 실패 처리
                analysis.setAnalysisStatus(AnalysisStatus.FAILED);
                analysisRepository.save(analysis);
            }
        }, analysisExecutor);
    }
}
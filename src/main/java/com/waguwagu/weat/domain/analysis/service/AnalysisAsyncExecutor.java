package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.group.model.entity.Group;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalysisAsyncExecutor {

    private final AIServiceAdaptor aiServiceAdaptor;
    private final AnalysisResultRepository analysisResultRepository;
    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;
    private final AnalysisResultDetailRepository analysisResultDetailRepository;
    private final AnalysisBasisRepository analysisBasisRepository;
    private final AnalysisRepository analysisRepository;
    private final PlatformTransactionManager transactionManager;

    // TODO: 추후 스레드 풀 설정
    public void startAnalysisAsync(Group group, Analysis analysis) {
        CompletableFuture.runAsync(() -> {

            TransactionTemplate tx = new TransactionTemplate(transactionManager);

            try {
                AIAnalysisDTO.Response response =
                        aiServiceAdaptor.requestAnalysis(AIAnalysisDTO.Request.builder()
                                .groupId(group.getGroupId())
                                .build());

                log.info("response => {}", response);

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
                                        .placeImageData(image.getPlaceImageData())
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
        });
    }
}
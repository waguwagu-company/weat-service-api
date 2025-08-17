package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
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
    private final GroupRepository groupRepository;

    // TODO: 추후 스레드 풀 설정
    public void startAnalysisAsync(AIAnalysisDTO.Request request) {
        CompletableFuture.runAsync(() -> {

            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));

            Analysis analysis = analysisRepository.findByGroupGroupId(group.getGroupId())
                    .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(group.getGroupId()));

            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            log.info("** request => {}", request);

            AIAnalysisDTO.Response response;
            AnalysisStatus analysisStatus = AnalysisStatus.COMPLETED;

            try {
                // AI 서버에 요청 실패하는 경우, 분석 실패 상태로 기록하고 응답(analysisResultDetailList)은 빈값으로 보내서,
                // 마치 분석 결과가 없는 것처럼 표시
                try {
                    response = aiServiceAdaptor.requestAnalysis(request);
                    log.info("** AI 분석 서버 처리 성공");
                } catch (Exception e) {
                    // AI 서버에 분석 요청 과정에서 오류 발생 시, 로깅
                    response = AIAnalysisDTO.Response.builder()
                            .groupId(group.getGroupId())
                            .analysisResult(
                                    AIAnalysisDTO.Response.AnalysisResult.builder()
                                            .analysisResultDetailList(new ArrayList<>())
                                            .build())
                            .build();
                    analysisStatus = AnalysisStatus.FAILED;
                    log.error("** AI 분석 서버 요청 실패 => {}", e.getMessage());
                }

                log.info("** response => {}", response);

                AIAnalysisDTO.Response finalResponse = response;
                AnalysisStatus finalAnalysisStatus = analysisStatus;

                // 트랜잭션 시작
                tx.executeWithoutResult(status -> {

                    // 결과 저장
                    AnalysisResult result = analysisResultRepository.save(
                            AnalysisResult.builder()
                                    .group(group)
                                    .analysis(analysis)
                                    .build()
                    );

                    for (var detail : finalResponse.getAnalysisResult().getAnalysisResultDetailList()) {
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
                                        //.analysisResultDetailTemplateMessage(detail.getAnalysisResultDetailTemplateMessage())
                                        .analysisResultDetailContent(detail.getAnalysisResultDetailContent())
                                        .analysisResultKeywords(detail.getAnalysisResultKeywords())
                                        .place(place)
                                        .build()
                        );

                        // 분석 근거 저장
                        for (var basis : detail.getAnalysisBasisList()) {
                            log.info("basis: {}", basis);
                            analysisBasisRepository.save(
                                    AnalysisBasis.builder()
                                            .analysisResultDetail(resultDetail)
                                            .analysisBasisType(basis.getAnalysisBasisType())
                                            .analysisBasisContent(basis.getAnalysisBasisContent())
                                            .analysisScore(basis.getAnalysisScore())
                                            .build()
                            );
                        }
                    }

                    // 분석 상태 완료 처리
                    analysis.setAnalysisStatus(finalAnalysisStatus);
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
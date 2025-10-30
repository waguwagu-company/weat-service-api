package com.waguwagu.weat.domain.group.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.group.model.dto.GroupAnalysisBasisQueryDTO;
import com.waguwagu.weat.domain.group.model.dto.QGroupAnalysisBasisQueryDTO;
import com.waguwagu.weat.domain.group.model.entity.QGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<GroupAnalysisBasisQueryDTO> findGroupAnalysisBasis(String groupId) {

        Objects.requireNonNull(groupId, "groupId must not be null");

        // 메인 쿼리 alias
        QGroup g = QGroup.group;
        QAnalysisResult ar = QAnalysisResult.analysisResult;
        QAnalysisResultDetail ard = QAnalysisResultDetail.analysisResultDetail;
        QAnalysisBasis ab = QAnalysisBasis.analysisBasis;
        QPlace p = QPlace.place;
        QPlaceImage pi = QPlaceImage.placeImage;

        // 서브쿼리 alias (동일 테이블 재참조)
        QAnalysisBasis ab2 = new QAnalysisBasis("ab2");
        QAnalysisResultDetail ard2 = new QAnalysisResultDetail("ard2");
        QPlaceImage pi2 = new QPlaceImage("pi2");

        return queryFactory
                .select(new QGroupAnalysisBasisQueryDTO(
                        ard.analysisResultDetailId,
                        ard.analysisResultKeywords,
                        p.placeId,
                        p.placeName,
                        p.placeRoadnameAddress,
                        p.placeUrl,
                        ab.analysisScore,
                        ab.analysisBasisType,
                        ab.analysisBasisContent,
                        pi.placeImageUrl
                ))
                .from(g)
                .join(ar).on(g.groupId.eq(ar.group.groupId))
                .join(ard).on(ar.analysisResultId.eq(ard.analysisResult.analysisResultId))
                .join(ab).on(ard.analysisResultDetailId.eq(ab.analysisResultDetail.analysisResultDetailId))
                .join(p).on(ard.place.placeId.eq(p.placeId))
                .leftJoin(pi).on(
                        pi.place.placeId.eq(p.placeId)
                                .and(pi.placeImageId.eq(
                                        JPAExpressions
                                                .select(pi2.placeImageId.min())
                                                .from(pi2)
                                                .where(pi2.place.placeId.eq(p.placeId))
                                ))
                )
                .where(
                        g.groupId.eq(groupId),
                        ab.analysisScore.eq(
                                JPAExpressions
                                        .select(ab2.analysisScore.max())
                                        .from(ab2)
                                        .join(ard2).on(ab2.analysisResultDetail.analysisResultDetailId.eq(ard2.analysisResultDetailId))
                                        .where(
                                                ard2.place.placeId.eq(p.placeId),
                                                ard2.analysisResult.analysisResultId.eq(ar.analysisResultId)
                                        )
                        )
                )
                .orderBy(p.placeId.asc(), ab.analysisScore.desc())
                .fetch();
    }

}

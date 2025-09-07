package com.waguwagu.weat.domain.group.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.waguwagu.weat.domain.analysis.model.entity.QAnalysisBasis;
import com.waguwagu.weat.domain.analysis.model.entity.QAnalysisResultDetail;
import com.waguwagu.weat.domain.analysis.model.entity.QPlaceImage;
import com.waguwagu.weat.domain.group.model.dto.GroupAnalysisBasisQueryDTO;
import com.waguwagu.weat.domain.group.model.dto.QGroupAnalysisBasisQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.waguwagu.weat.domain.analysis.model.entity.QAnalysisBasis.analysisBasis;
import static com.waguwagu.weat.domain.analysis.model.entity.QAnalysisResult.analysisResult;
import static com.waguwagu.weat.domain.analysis.model.entity.QAnalysisResultDetail.analysisResultDetail;
import static com.waguwagu.weat.domain.analysis.model.entity.QPlace.place;
import static com.waguwagu.weat.domain.analysis.model.entity.QPlaceImage.placeImage;
import static com.waguwagu.weat.domain.group.model.entity.QGroup.group;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<GroupAnalysisBasisQueryDTO> findGroupAnalysisBasis(String groupId) {
        var ab2 = new QAnalysisBasis("ab2");
        var ard2 = new QAnalysisResultDetail("ard2");
        var pi2 = new QPlaceImage("pi2");

        return queryFactory
                .select(
                        new QGroupAnalysisBasisQueryDTO(
                                analysisResultDetail.analysisResultDetailId,
                                analysisResultDetail.analysisResultKeywords,
                                place.placeId,
                                place.placeName,
                                place.placeRoadnameAddress,
                                place.placeUrl,
                                analysisBasis.analysisScore,
                                analysisBasis.analysisBasisType,
                                analysisBasis.analysisBasisContent,
                                placeImage.placeImageUrl
                        )
                )
                .from(group)
                .join(analysisResult).on(group.groupId.eq(analysisResult.group.groupId))
                .join(analysisResultDetail).on(analysisResult.analysisResultId.eq(analysisResultDetail.analysisResult.analysisResultId))
                .join(analysisBasis).on(analysisResultDetail.analysisResultDetailId.eq(analysisBasis.analysisResultDetail.analysisResultDetailId))
                .join(place).on(analysisResultDetail.place.placeId.eq(place.placeId))
                .leftJoin(placeImage).on(
                        placeImage.place.placeId.eq(place.placeId)
                                .and(
                                        placeImage.placeImageId.eq(
                                                JPAExpressions
                                                        .select(pi2.placeImageId.min())
                                                        .from(pi2)
                                                        .where(pi2.place.placeId.eq(place.placeId))
                                        )
                                )
                )

                .where(
                        group.groupId.eq(groupId),
                        analysisBasis.analysisScore.eq(
                                JPAExpressions
                                        .select(ab2.analysisScore.max())
                                        .from(ab2)
                                        .join(ard2).on(ab2.analysisResultDetail.analysisResultDetailId.eq(ard2.analysisResultDetailId))
                                        .where(
                                                ard2.place.placeId.eq(place.placeId),
                                                ard2.analysisResult.analysisResultId.eq(analysisResult.analysisResultId)
                                        )
                        )
                )
                .orderBy(place.placeId.asc(), analysisBasis.analysisScore.desc())
                .fetch();
    }

}

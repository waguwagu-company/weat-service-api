package com.waguwagu.weat.domain.analysis.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.waguwagu.weat.domain.analysis.model.dto.MemberAnalysisSettingDto;
import com.waguwagu.weat.domain.analysis.model.entity.QAnalysisSetting;
import com.waguwagu.weat.domain.analysis.model.entity.QCategorySetting;
import com.waguwagu.weat.domain.analysis.model.entity.QLocationSetting;
import com.waguwagu.weat.domain.analysis.model.entity.QTextInputSetting;
import com.waguwagu.weat.domain.category.model.entity.QCategory;
import com.waguwagu.weat.domain.category.model.entity.QCategoryTag;
import com.waguwagu.weat.domain.group.model.entity.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AnalysisSettingRepositoryImpl implements AnalysisSettingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 그룹내 멤버들의 설정 제출 정보 조회
     *
     * @apiNote 위치 설정과 카테고리 설정은 필수 제출 항목이며, 비정형 입력 설정은 선택 항목임에 주의
     */
    @Override
    @Transactional(readOnly = true)
    public List<MemberAnalysisSettingDto> findMemberAnalysisSettingsByGroupId(String groupId) {
        QMember m = QMember.member;
        QAnalysisSetting as = QAnalysisSetting.analysisSetting;
        QLocationSetting ls = QLocationSetting.locationSetting;
        QTextInputSetting ts = QTextInputSetting.textInputSetting;
        QCategorySetting cs = QCategorySetting.categorySetting;
        QCategory c = QCategory.category;
        QCategoryTag ct = QCategoryTag.categoryTag;

        List<Tuple> results = queryFactory
                .select(m.memberId,
                        ls.xPosition,
                        ls.yPosition,
                        ls.roadnameAddress,
                        ts.inputText,
                        c.categoryId,
                        c.categoryName,
                        ct.categoryTagId,
                        ct.categoryTagName,
                        cs.isPreferred)
                .from(m)
                .join(as).on(as.member.eq(m))
                .join(ls).on(ls.analysisSetting.eq(as))
                .join(cs).on(cs.analysisSetting.eq(as))
                .leftJoin(ts).on(ts.analysisSetting.eq(as))
                .join(c).on(cs.category.eq(c))
                .join(ct).on(cs.categoryTag.eq(ct))
                .where(groupId != null ? m.group.groupId.eq(groupId) : null)
                .fetch();

        Map<Long, MemberAnalysisSettingDto> memberMap = new HashMap<>();

        for (Tuple result : results) {
            Long memberId = result.get(m.memberId);

            memberMap.computeIfAbsent(memberId, id ->
                    MemberAnalysisSettingDto.builder()
                            .memberId(id)
                            .xPosition(result.get(ls.xPosition))
                            .yPosition(result.get(ls.yPosition))
                            .roadnameAddress(result.get(ls.roadnameAddress))
                            .inputText(result.get(ts.inputText))
                            .categorySettings(new ArrayList<>())
                            .build()
            );

            memberMap.get(memberId).getCategorySettings().add(
                    MemberAnalysisSettingDto.CategorySettingDto.builder()
                            .categoryId(result.get(c.categoryId))
                            .categoryName(result.get(c.categoryName))
                            .categoryTagId(result.get(ct.categoryTagId))
                            .categoryTagName(result.get(ct.categoryTagName))
                            .isPreferred(result.get(cs.isPreferred))
                            .build()
            );
        }

        return new ArrayList<>(memberMap.values());
    }
}
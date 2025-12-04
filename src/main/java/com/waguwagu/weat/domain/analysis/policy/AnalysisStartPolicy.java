package com.waguwagu.weat.domain.analysis.policy;

import com.waguwagu.weat.domain.analysis.exception.AnalysisAlreadyStartedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisConditionNotSatisfiedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisStatus;
import com.waguwagu.weat.domain.analysis.repository.AnalysisRepository;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingRepository;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.EnumSet;
import java.util.Set;

@Validated
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisStartPolicy {
    private final AnalysisStartPolicySupport support;

    /**
     * 분석시작가능 여부 평가
     *
     * <p>분석 시작을 위해 필요한 조건들은 평가한다.</p>
     */
    public Result evaluate(@NotNull String groupId) {
        boolean isSubmittedConditionSatisfied = support.evaluateSubmittedCondition(groupId);
        boolean isAnalysisStatusConditionSatisfied = support.evaluateAnalysisStatusCondition(groupId);
        boolean isSatisfied = isSubmittedConditionSatisfied && isAnalysisStatusConditionSatisfied;

        return new Result(
                isSatisfied,
                isSubmittedConditionSatisfied,
                isAnalysisStatusConditionSatisfied
        );
    }

    /**
     * 분석시작가능 여부 검증
     *
     * <p>분석 시작이 가능한지 검증하고, 만족하지 못하는 조건이 있는 경우 예외를 발생시킨다.</p>
     */
    public void validate(@NotNull String groupId) {
        if (!support.evaluateAnalysisStatusCondition(groupId)) {
            throw new AnalysisAlreadyStartedForGroupIdException(groupId);
        }

        if (!support.evaluateSubmittedCondition(groupId)) {
            throw new AnalysisConditionNotSatisfiedForGroupIdException(groupId);
        }
    }

    /**
     * 분석시작가능 여부 평가 결과
     *
     * <p>분석시작가능 여부 평가 후 각 조건들에 대한 충족 여부를 반환하기 위한 레코드</p>
     *
     * @see AnalysisStartPolicy#evaluate
     */
    public record Result(
            boolean isSatisfied, // 전체조건 충족 여부
            boolean isSubmittedConditionSatisfied, // 설정제출조건 충족 여부
            boolean isAnalysisStatusConditionSatisfied // 분석상태조건 충족 여부
    ) {
    }

    @Validated
    @Component
    @RequiredArgsConstructor
    @Transactional(readOnly = true)
    public static class AnalysisStartPolicySupport {
        private final GroupRepository groupRepository;
        private final AnalysisRepository analysisRepository;
        private final AnalysisSettingRepository analysisSettingRepository;

        /**
         * 분석 시작을 위한 분석설정제출 조건 평가
         *
         * <p>분석설정제출 기준에 따라 분석 시작이 가능한지 평가한다.</p>
         *
         * @see AnalysisStartPolicy.SubmitCriteria
         */
        public boolean evaluateSubmittedCondition(@NotNull String groupId) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new GroupNotFoundException(groupId));

            SubmitCriteria submitCriteria = group.isSingleMemberGroup() ?
                    SubmitCriteria.SINGLE_MEMBER_GROUP : SubmitCriteria.MULTI_MEMBER_GROUP;

            long submittedCount = analysisSettingRepository.countAnalysisSettingByGroupId(groupId);

            return submitCriteria.isSatisfied(submittedCount);
        }

        /**
         * 분석 시작을 위한 분석상태 조건 평가
         *
         * <p>분석상태 기준에 따라 분석 시작이 가능한지 평가한다.</p>
         *
         * @see AnalysisStartPolicy.StatusCriteria
         */
        public boolean evaluateAnalysisStatusCondition(@NotNull String groupId) {
            AnalysisStatus analysisStatus = analysisRepository.findByGroupGroupId(groupId)
                    .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(groupId))
                    .getAnalysisStatus();

            return StatusCriteria.STARTABLE.isSatisfied(analysisStatus);
        }
    }

    /**
     * 분석 시작을 위한 분석 설정 제출에 대한 기준
     *
     * <p>분석 시작을 위해서는 반드시 그룹 유형별로 일정 수 이상의 분석 설정이 제출되어야 한다.</p>
     */
    @Getter
    @RequiredArgsConstructor
    public enum SubmitCriteria {
        SINGLE_MEMBER_GROUP(1L),
        MULTI_MEMBER_GROUP(2L);

        // 분석 설정 제출 인원수에 대한 기준
        private final long requiredCount;

        // 단일멤버그룹 또는 다중멤버그룹 여부에 따라서 일정 수 이상 설정을 제출해야 만족
        public boolean isSatisfied(long submittedCount) {
            return submittedCount >= requiredCount;
        }
    }

    /**
     * 분석 시작을 위한 분석 상태에 대한 기준
     *
     * <p>분석 시작을 위해서는 반드시 그룹의 분석상태가 "분석시작전"(AnalysisStatus.NOT_STARTED) 상태이어야 한다.</p>
     */
    @Getter
    @RequiredArgsConstructor
    public enum StatusCriteria {
        // 분석 시작이 가능한 상태 정의 (분석이 아직 시작되지 않은 경우에만 허용)
        STARTABLE(EnumSet.of(AnalysisStatus.NOT_STARTED));

        // 허용된 분석 상태 집합
        private final Set<AnalysisStatus> allowedStatuses;

        public boolean isSatisfied(@NotNull AnalysisStatus status) {
            return allowedStatuses.contains(status);
        }
    }
}

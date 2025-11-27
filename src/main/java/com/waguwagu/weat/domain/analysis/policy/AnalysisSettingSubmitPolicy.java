package com.waguwagu.weat.domain.analysis.policy;

import com.waguwagu.weat.domain.analysis.exception.MemberAlreadySubmitSettingForMemberIdException;
import com.waguwagu.weat.domain.analysis.exception.MemberNotFoundException;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisSettingSubmitPolicy {
    private final MemberRepository memberRepository;
    private final AnalysisSettingRepository analysisSettingRepository;

    /**
     * 분석설정 제출이 가능한 상태인지 검증
     *
     * <p>분석 설정 제출이 가능한 상태인지 검증하고, 만족하지 못하는 조건이 있는 경우 예외 발생시킨다.</p>
     */
    public void validate(long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }

        // 멤버가 기존에 설정을 제출했는지 여부 확인
        boolean alreadySubmitted = analysisSettingRepository.existsByMemberMemberId(memberId);

        // 이미 제출한 경우 예외 발생
        if (!SubmitCriteria.SUBMITTABLE.isSatisfied(alreadySubmitted)) {
            throw new MemberAlreadySubmitSettingForMemberIdException(memberId);
        }
    }

    /**
     * 제출 가능한 상태에 대한 기준
     *
     * <p>분석 설정을 제출하기 위해서는 기존에 제출한 상태가 아니어야 한다.</p>
     */
    public enum SubmitCriteria {
        SUBMITTABLE;

        // 기존에 제출한 상태가 아니어야만 제출 가능
        public boolean isSatisfied(boolean alreadySubmitted) {
            return !alreadySubmitted;
        }
    }
}

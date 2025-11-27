package com.waguwagu.weat.domain.analysis.policy;

import com.waguwagu.weat.domain.analysis.exception.MemberAlreadySubmitSettingForMemberIdException;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AnalysisSettingSubmitPolicy.class,
        MethodValidationPostProcessor.class
})
class AnalysisSettingSubmitPolicyTest {

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    AnalysisSettingRepository analysisSettingRepository;

    @Autowired
    AnalysisSettingSubmitPolicy analysisSettingSubmitPolicy;

    @Nested
    @DisplayName("validate - 분석설정제출 가능 여부 검증")
    class Validate {
        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {

            @Test
            @DisplayName("분석 설정을 제출한적이 없는 멤버인 경우 예외가 발생하지 않아야 한다.")
            void validate_success_tc_01() {
                // given
                final long givenMemberId = 0L;
                when(memberRepository.existsById(givenMemberId)).thenReturn(true);
                when(analysisSettingRepository.existsByMemberMemberId(givenMemberId)).thenReturn(false);

                // when
                assertDoesNotThrow(
                        () -> analysisSettingSubmitPolicy.validate(givenMemberId)
                );
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {
            @Test
            @DisplayName("분석 설정을 제출한적이 있는 멤버인 경우 `MemberAlreadySubmitSettingForMemberIdException` 예외가 발생해야 한다.")
            void validate_exception_tc_01() {
                // given
                final long givenMemberId = 0L;
                when(memberRepository.existsById(givenMemberId)).thenReturn(true);
                when(analysisSettingRepository.existsByMemberMemberId(givenMemberId)).thenReturn(true);

                // when
                assertThrows(MemberAlreadySubmitSettingForMemberIdException.class,
                        () -> analysisSettingSubmitPolicy.validate(givenMemberId)
                );
            }
        }
    }

}

package com.waguwagu.weat.domain.analysis.policy;

import com.waguwagu.weat.domain.analysis.exception.AnalysisAlreadyStartedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisConditionNotSatisfiedForGroupIdException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AnalysisStartPolicy.class,
        MethodValidationPostProcessor.class
})
class AnalysisStartPolicyTest {

    @MockBean
    AnalysisStartPolicy.AnalysisStartPolicySupport support;

    @Autowired
    AnalysisStartPolicy analysisStartPolicy;

    String testGroupId;

    @BeforeEach
    void setUp() {
        testGroupId = UUID.randomUUID().toString();
    }

    @Nested
    @DisplayName("evaluate - 분석시작가능 조건 평가")
    class Evaluate {
        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {
            @Test
            @DisplayName("모든 조건이 충족된 경우 평가결과내의 모든 충족여부 값이 true로 반환되어야 한다.")
            void evaluate_success_tc_01() {
                // given
                final boolean givenSubmittedCondition = true;
                final boolean givenAnalysisStatusCondition = true;
                final boolean expectedIsSatisfied = true;

                when(support.evaluateSubmittedCondition(testGroupId)).thenReturn(givenSubmittedCondition);
                when(support.evaluateAnalysisStatusCondition(testGroupId)).thenReturn(givenAnalysisStatusCondition);

                // when
                final AnalysisStartPolicy.Result result = analysisStartPolicy.evaluate(testGroupId);

                // then
                assertThat(result.isSubmittedConditionSatisfied()).isEqualTo(givenSubmittedCondition);
                assertThat(result.isAnalysisStatusConditionSatisfied()).isEqualTo(givenAnalysisStatusCondition);
                assertThat(result.isSatisfied()).isEqualTo(expectedIsSatisfied);
            }

            @ParameterizedTest(name = "[CASE{index}] givenSubmittedCondition={0}, givenAnalysisStatusCondition={1} -> expectedIsSatisfied={2}")
            @CsvSource({
                    "false, true,  false", // 제출조건만 불만족
                    "true,  false, false", // 분석상태조건만 불만족
                    "false, false, false"  // 모두 불만족
            })
            @DisplayName("만족하지 않는 조건이 있을 경우 불만족하는 결과에 대해서 false를 반환하고, 전체 만족여부도 false를 반환해야한다.")
            void evaluate_success_tc_02(boolean givenSubmittedCondition, boolean givenAnalysisStatusCondition, boolean expectedIsSatisfied) {
                // given
                when(support.evaluateSubmittedCondition(testGroupId)).thenReturn(givenSubmittedCondition);
                when(support.evaluateAnalysisStatusCondition(testGroupId)).thenReturn(givenAnalysisStatusCondition);

                // when
                final AnalysisStartPolicy.Result result = analysisStartPolicy.evaluate(testGroupId);

                // then
                verify(support).evaluateSubmittedCondition(testGroupId);
                verify(support).evaluateAnalysisStatusCondition(testGroupId);

                assertThat(result.isSubmittedConditionSatisfied()).isEqualTo(givenSubmittedCondition);
                assertThat(result.isAnalysisStatusConditionSatisfied()).isEqualTo(givenAnalysisStatusCondition);
                assertThat(result.isSatisfied()).isEqualTo(expectedIsSatisfied);
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {
            @Test
            @DisplayName("그룹 식별자가 null 인 경우 `ConstraintViolationException` 예외가 발생해야 한다.")
            void evaluate_exception_tc_01() {
                // given
                final String givenGroupId = null;

                // when & then
                assertThrows(ConstraintViolationException.class,
                        () -> analysisStartPolicy.evaluate(givenGroupId));
            }
        }
    }

    @Nested
    @DisplayName("validate - 분석시작가능 조건 검증")
    class Validate {
        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {
            @Test
            @DisplayName("모든 조건을 만족한 경우에는 어떠한 예외도 발생하지 않아야 한다.")
            void validate_success_tc_01() {
                // given
                final boolean givenSubmittedCondition = true;
                final boolean givenAnalysisStatusCondition = true;

                when(support.evaluateSubmittedCondition(testGroupId)).thenReturn(givenSubmittedCondition);
                when(support.evaluateAnalysisStatusCondition(testGroupId)).thenReturn(givenAnalysisStatusCondition);

                // when
                assertDoesNotThrow(() -> analysisStartPolicy.validate(testGroupId));
                verify(support).evaluateAnalysisStatusCondition(testGroupId);
                verify(support).evaluateAnalysisStatusCondition(testGroupId);
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {
            @Test
            @DisplayName("그룹 식별자가 null 인 경우 `ConstraintViolationException` 예외가 발생해야 한다.")
            void validate_exception_tc_01() {
                // given
                final String givenGroupId = null;

                // when & then
                assertThrows(
                        ConstraintViolationException.class,
                        () -> analysisStartPolicy.validate(givenGroupId)
                );
            }

            @Test
            @DisplayName("제출조건이 만족하지 않은 경우에는 `AnalysisConditionNotSatisfiedForGroupIdException`를 발생시켜야한다.")
            void validate_exception_tc_02() {
                // given
                final boolean givenAnalysisStatusCondition = true;
                final boolean givenSubmittedCondition = false;

                lenient().when(support.evaluateAnalysisStatusCondition(testGroupId)).thenReturn(givenAnalysisStatusCondition);
                lenient().when(support.evaluateSubmittedCondition(testGroupId)).thenReturn(givenSubmittedCondition);

                // when & then
                assertThrows(AnalysisConditionNotSatisfiedForGroupIdException.class,
                        () -> analysisStartPolicy.validate(testGroupId));

                verify(support).evaluateSubmittedCondition(testGroupId);
            }

            @Test
            @DisplayName("분석상태조건이 만족하지 않은 경우에는 `AnalysisAlreadyStartedForGroupIdException`를 발생시켜야한다.")
            void validate_exception_tc_03() {
                // given
                final boolean givenAnalysisStatusCondition = false;
                final boolean givenSubmittedCondition = true;

                lenient().when(support.evaluateAnalysisStatusCondition(testGroupId)).thenReturn(givenAnalysisStatusCondition);
                lenient().when(support.evaluateSubmittedCondition(testGroupId)).thenReturn(givenSubmittedCondition);

                // when & then
                assertThrows(AnalysisAlreadyStartedForGroupIdException.class,
                        () -> analysisStartPolicy.validate(testGroupId));

                verify(support).evaluateAnalysisStatusCondition(testGroupId);
                verify(support, never()).evaluateSubmittedCondition(testGroupId);
            }

            @Test
            @DisplayName("모두 만족하지 않는 경우 어떠한 예외든 발생하여야 한다.")
            void validate_exception_tc_04() {
                // given
                final boolean givenAnalysisStatusCondition = false;
                final boolean givenSubmittedCondition = false;

                lenient().when(support.evaluateAnalysisStatusCondition(testGroupId)).thenReturn(givenAnalysisStatusCondition);
                lenient().when(support.evaluateSubmittedCondition(testGroupId)).thenReturn(givenSubmittedCondition);

                // when & then
                assertThrows(RuntimeException.class,
                        () -> analysisStartPolicy.validate(testGroupId));

                verify(support).evaluateAnalysisStatusCondition(testGroupId);
                verify(support, never()).evaluateSubmittedCondition(testGroupId);
            }
        }
    }
}

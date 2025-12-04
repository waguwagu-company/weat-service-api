package com.waguwagu.weat.domain.analysis.policy;

import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.model.entity.Analysis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisStatus;
import com.waguwagu.weat.domain.analysis.repository.AnalysisRepository;
import com.waguwagu.weat.domain.analysis.repository.AnalysisSettingRepository;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AnalysisStartPolicy.AnalysisStartPolicySupport.class,
        MethodValidationPostProcessor.class
})
class AnalysisStartPolicySupportTest {

    @MockBean
    GroupRepository groupRepository;

    @MockBean
    AnalysisRepository analysisRepository;

    @MockBean
    AnalysisSettingRepository analysisSettingRepository;

    @Autowired
    AnalysisStartPolicy.AnalysisStartPolicySupport analysisStartPolicySupport;

    String testSingleMemberGroupId;
    String testMultipleMemberGroupId;
    Group testSingleMemberGroup;
    Group testMultipleMemberGroup;

    @BeforeEach
    void setUp() {
        testSingleMemberGroupId = UUID.randomUUID().toString().replace("-", "");
        testSingleMemberGroup = Group.builder()
                .groupId(testSingleMemberGroupId)
                .isSingleMemberGroup(true)
                .build();

        testMultipleMemberGroupId = UUID.randomUUID().toString().replace("-", "");
        testMultipleMemberGroup = Group.builder()
                .groupId(testMultipleMemberGroupId)
                .isSingleMemberGroup(false)
                .build();
    }

    @Nested
    @DisplayName("evaluateSubmittedCondition - 분석설정제출 조건")
    class EvaluateSubmittedCondition {

        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {

            @Test
            @DisplayName("단일 멤버 그룹에서 설정을 제출한 멤버수가 1명인 경우 true를 반환해야한다.")
            void evaluateSubmittedCondition_success_tc_01() {
                // given
                final long givenSubmittedCount = 1L;
                when(groupRepository.findById(testSingleMemberGroupId)).thenReturn(Optional.of(testSingleMemberGroup));
                when(analysisSettingRepository.countAnalysisSettingByGroupId(testSingleMemberGroupId)).thenReturn(givenSubmittedCount);

                // when
                final boolean result = analysisStartPolicySupport.evaluateSubmittedCondition(testSingleMemberGroupId);

                // then
                assertThat(result).isTrue();

                verify(groupRepository).findById(testSingleMemberGroupId);
                verify(analysisSettingRepository).countAnalysisSettingByGroupId(testSingleMemberGroupId);
            }

            @Test
            @DisplayName("단일 멤버 그룹에서 설정을 제출한 멤버가 없을 경우 false를 반환해야한다.")
            void evaluateSubmittedCondition_success_tc_02() {
                // given
                final long givenSubmittedCount = 0L;
                when(groupRepository.findById(testSingleMemberGroupId)).thenReturn(Optional.of(testSingleMemberGroup));
                when(analysisSettingRepository.countAnalysisSettingByGroupId(testSingleMemberGroupId)).thenReturn(givenSubmittedCount);

                // when
                final boolean result = analysisStartPolicySupport.evaluateSubmittedCondition(testSingleMemberGroupId);

                // then
                assertThat(result).isFalse();

                verify(groupRepository).findById(testSingleMemberGroupId);
                verify(analysisSettingRepository).countAnalysisSettingByGroupId(testSingleMemberGroupId);
            }

            @Test
            @DisplayName("다중 멤버 그룹에서 설정을 제출한 멤버수가 2명 이상인 경우 true를 반환해야한다.")
            void evaluateSubmittedCondition_success_tc_03() {
                // given
                final long givenSubmittedCount = 2 + new Random().nextInt(10);
                when(groupRepository.findById(testMultipleMemberGroupId)).thenReturn(Optional.of(testMultipleMemberGroup));
                when(analysisSettingRepository.countAnalysisSettingByGroupId(testMultipleMemberGroupId)).thenReturn(givenSubmittedCount);

                // when
                final boolean result = analysisStartPolicySupport.evaluateSubmittedCondition(testMultipleMemberGroupId);

                // then
                assertThat(result).isTrue();

                verify(groupRepository).findById(testMultipleMemberGroupId);
                verify(analysisSettingRepository).countAnalysisSettingByGroupId(testMultipleMemberGroupId);
            }

            @Test
            @DisplayName("다중 멤버 그룹에서 설정을 제출한 멤버수가 2명 미만인 경우 false를 반환해야한다.")
            void evaluateSubmittedCondition_success_tc_04() {
                // given
                final long givenSubmittedCount = new Random().nextInt(2);
                when(groupRepository.findById(testMultipleMemberGroupId)).thenReturn(Optional.of(testMultipleMemberGroup));
                when(analysisSettingRepository.countAnalysisSettingByGroupId(testMultipleMemberGroupId)).thenReturn(givenSubmittedCount);

                // when
                final boolean result = analysisStartPolicySupport.evaluateSubmittedCondition(testMultipleMemberGroupId);

                // then
                assertThat(result).isFalse();

                verify(groupRepository).findById(testMultipleMemberGroupId);
                verify(analysisSettingRepository).countAnalysisSettingByGroupId(testMultipleMemberGroupId);
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {
            @Test
            @DisplayName("그룹 식별자가 null인 경우 `ConstraintViolationException` 예외가 발생해야 한다.")
            void evaluateSubmittedCondition_exception_tc_01() {
                // given
                final String givenGroupId = null;

                // when
                assertThrows(ConstraintViolationException.class,
                        () -> analysisStartPolicySupport.evaluateSubmittedCondition(givenGroupId)
                );

                verify(groupRepository, times(0)).findById(givenGroupId);
            }

            @Test
            @DisplayName("존재하지 않는 그룹 식별자인 경우 `GroupNotFoundException`이 발생해야 한다.")
            void evaluateSubmittedCondition_exception_tc_02() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.empty());

                // when
                assertThrows(GroupNotFoundException.class,
                        () -> analysisStartPolicySupport.evaluateSubmittedCondition(givenGroupId)
                );

                verify(groupRepository).findById(givenGroupId);
            }
        }
    }

    @Nested
    @DisplayName("evaluateAnalysisStatusCondition - 분석상태 조건")
    class EvaluateAnalysisStatusCondition {

        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {
            @Test
            @DisplayName("그룹의 분석상태가 시작전인 경우 true를 반환해야한다.")
            void evaluateAnalysisStatusCondition_success_tc_01() {
                // given
                final AnalysisStatus givenAnalysisStatus = AnalysisStatus.NOT_STARTED;
                when(groupRepository.findById(testMultipleMemberGroupId)).thenReturn(Optional.of(testMultipleMemberGroup));

                Analysis mockAnalysis = mock(Analysis.class);
                when(analysisRepository.findByGroupGroupId(testMultipleMemberGroupId)).thenReturn(Optional.of(mockAnalysis));
                when(mockAnalysis.getAnalysisStatus()).thenReturn(givenAnalysisStatus);

                // when
                final boolean result = analysisStartPolicySupport.evaluateAnalysisStatusCondition(testMultipleMemberGroupId);

                assertThat(result).isTrue();
            }

            @ParameterizedTest(name = "[CASE {index}] analysisStatus={0} -> expected=false")
            @ValueSource(strings = {"IN_PROGRESS", "COMPLETED", "FAILED", "CANCELLED"})
            @DisplayName("그룹의 분석상태가 시작전이 아닌 경우 false를 반환해야한다.")
            void evaluateAnalysisStatusCondition_success_tc_02(String givenAnalysisStatusName) {
                // given
                AnalysisStatus givenAnalysisStatus = AnalysisStatus.valueOf(givenAnalysisStatusName);
                when(groupRepository.findById(testMultipleMemberGroupId)).thenReturn(Optional.of(testMultipleMemberGroup));

                Analysis mockAnalysis = mock(Analysis.class);
                when(analysisRepository.findByGroupGroupId(testMultipleMemberGroupId)).thenReturn(Optional.of(mockAnalysis));
                when(mockAnalysis.getAnalysisStatus()).thenReturn(givenAnalysisStatus);

                // when
                final boolean result = analysisStartPolicySupport.evaluateAnalysisStatusCondition(testMultipleMemberGroupId);

                assertThat(result).isFalse();
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {

            @Test
            @DisplayName("존재하지 않는 그룹 식별자인 경우 `AnalysisNotFoundForGroupIdException` 예외가 발생해야 한다.")
            void evaluateAnalysisStatusCondition_exception_tc_01() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                when(analysisRepository.findByGroupGroupId(givenGroupId)).thenReturn(Optional.empty());

                // when & then
                assertThrows(AnalysisNotFoundForGroupIdException.class, () ->
                        analysisStartPolicySupport.evaluateAnalysisStatusCondition(givenGroupId)
                );

                verify(analysisRepository).findByGroupGroupId(givenGroupId);
            }

            @Test
            @DisplayName("그룹 식별자가 null인 경우 `ConstraintViolationException` 예외가 발생해야 한다.")
            void evaluateAnalysisStatusCondition_exception_tc_02() {
                // given
                final String groupId = null;

                // when & then
                assertThrows(ConstraintViolationException.class, () ->
                        analysisStartPolicySupport.evaluateAnalysisStatusCondition(groupId)
                );
            }
        }
    }
}

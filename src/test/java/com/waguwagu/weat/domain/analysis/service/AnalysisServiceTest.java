package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.model.dto.GetAnalysisStatusDTO;
import com.waguwagu.weat.domain.analysis.model.entity.Analysis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisStatus;
import com.waguwagu.weat.domain.analysis.policy.AnalysisSettingSubmitPolicy;
import com.waguwagu.weat.domain.analysis.policy.AnalysisStartPolicy;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AnalysisService.class,
        MethodValidationPostProcessor.class
})
class AnalysisServiceTest {

    @MockBean
    AIServiceAdaptor aiServiceAdaptor;
    @MockBean
    GroupRepository groupRepository;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    AnalysisRepository analysisRepository;
    @MockBean
    AnalysisSettingRepository analysisSettingRepository;
    @MockBean
    AnalysisSettingDetailRepository analysisSettingDetailRepository;
    @MockBean
    CategoryTagRepository categoryTagRepository;
    @MockBean
    AnalysisResultLikeRepository analysisResultLikeRepository;
    @MockBean
    AnalysisResultDetailRepository analysisResultDetailRepository;
    @MockBean
    ApplicationEventPublisher eventPublisher;
    @MockBean
    AnalysisStartPolicy analysisStartPolicy;
    @MockBean
    AnalysisSettingSubmitPolicy analysisSettingSubmitPolicy;

    @Autowired
    AnalysisService analysisService;

    @Nested
    @DisplayName("getAnalysisStatus - 분석진행상태 정보 조회")
    class GetAnalysisStatus {

        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {
            /**
             * @see com.waguwagu.weat.domain.analysis.model.dto.GetAnalysisStatusDTO.Response
             */
            @Test
            @DisplayName("그룹에 대한 분석상태 정보가 실제 상태와 동일하게 반환되어야 한다.")
            void getAnalysisStatus_success_tc_01() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                final boolean givenIsSingleMemberGroup = false;
                final boolean givenIsAnalysisStartPolicySatisfied = true;
                final AnalysisStatus givenAnalysisStatus = AnalysisStatus.NOT_STARTED;
                final Long givenSubmittedCount = 2L;

                final Group mockGroup = mock(Group.class);
                when(mockGroup.getGroupId()).thenReturn(givenGroupId);
                when(mockGroup.isSingleMemberGroup()).thenReturn(givenIsSingleMemberGroup);

                final Analysis mockAnalysis = mock(Analysis.class);
                when(mockAnalysis.getAnalysisStatus()).thenReturn(givenAnalysisStatus);

                final AnalysisStartPolicy.Result mockAnalysisStartPolicyResult = mock(AnalysisStartPolicy.Result.class);
                when(mockAnalysisStartPolicyResult.isSatisfied()).thenReturn(givenIsAnalysisStartPolicySatisfied);

                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.of(mockGroup));
                when(analysisRepository.findByGroupGroupId(givenGroupId)).thenReturn(Optional.of(mockAnalysis));
                when(analysisSettingRepository.countAnalysisSettingByGroupId(givenGroupId)).thenReturn(givenSubmittedCount);
                when(analysisStartPolicy.evaluate(givenGroupId)).thenReturn(mockAnalysisStartPolicyResult);

                // when
                final GetAnalysisStatusDTO.Response response = analysisService.getAnalysisStatus(givenGroupId);

                // then
                assertThat(response)
                        .extracting(
                                GetAnalysisStatusDTO.Response::getGroupId,
                                GetAnalysisStatusDTO.Response::getIsSingleMemberGroup,
                                GetAnalysisStatusDTO.Response::getSubmittedCount,
                                GetAnalysisStatusDTO.Response::getIsAnalysisStartConditionSatisfied,
                                GetAnalysisStatusDTO.Response::getAnalysisStatus
                        )
                        .containsExactly(
                                givenGroupId,
                                givenIsSingleMemberGroup,
                                givenSubmittedCount,
                                givenIsAnalysisStartPolicySatisfied,
                                givenAnalysisStatus
                        );
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {

            @Test
            @DisplayName("그룹 식별자가 null 인 경우 `ConstraintViolationException` 예외가 발생해야 한다.")
            void getAnalysisStatus_exception_tc_01() {
                // given
                final String givenGroupId = null;

                // when & then
                assertThrows(
                        ConstraintViolationException.class,
                        () -> analysisService.getAnalysisStatus(givenGroupId)
                );
            }

            @Test
            @DisplayName("존재하지 않는 그룹 식별자인 경우 `GroupNotFoundException` 예외가 발생해야 한다.")
            void getAnalysisStatus_exception_tc_02() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.empty());

                // when & then
                assertThrows(GroupNotFoundException.class, () ->
                        analysisService.getAnalysisStatus(givenGroupId)
                );
            }

            @Test
            @DisplayName("그룹에 대한 분석 정보가 존재하지 않는 경우 `AnalysisNotFoundForGroupIdException` 예외가 발생해야 한다.")
            void getAnalysisStatus_exception_tc_03() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                final Group mockGroup = mock(Group.class);
                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.of(mockGroup));
                when(analysisRepository.findByGroupGroupId(givenGroupId)).thenReturn(Optional.empty());

                // when & then
                assertThrows(AnalysisNotFoundForGroupIdException.class, () ->
                        analysisService.getAnalysisStatus(givenGroupId)
                );
            }
        }
    }
}

package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.event.AnalysisStartEvent;
import com.waguwagu.weat.domain.analysis.exception.AnalysisAlreadyStartedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisConditionNotSatisfiedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;
import com.waguwagu.weat.domain.analysis.model.dto.AnalysisStartDTO;
import com.waguwagu.weat.domain.analysis.model.dto.GetAnalysisStatusDTO;
import com.waguwagu.weat.domain.analysis.model.dto.MemberAnalysisSettingDTO;
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
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RecordApplicationEvents
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
    @Autowired
    ApplicationEvents applicationEvents;
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

    @Nested
    @DisplayName("analysisStart - 분석시작")
    class AnalysisStart {

        String givenGroupId;
        Group givenGroup;
        Analysis givenAnalysis;
        List<MemberAnalysisSettingDTO> givenMemberAnalysisSettings;
        AnalysisStartDTO.Request givenRequest;

        @BeforeEach
        void setUp() {
            givenGroupId = UUID.randomUUID().toString().replace("-", "");
            givenGroup = Group.builder()
                    .groupId(givenGroupId)
                    .isSingleMemberGroup(false)
                    .build();

            givenAnalysis = Analysis.builder()
                    .analysisId(0L)
                    .group(givenGroup)
                    .analysisStatus(AnalysisStatus.NOT_STARTED)
                    .build();

            givenRequest = AnalysisStartDTO.Request.builder()
                    .groupId(givenGroupId)
                    .build();

            final MemberAnalysisSettingDTO memberSettingDto1 = buildMemberAnalysisSettingDto(
                    1L, 127.123456, 37.987654,
                    "경기도 성남시 수정구 창업로 42", "주변 맛집 추천",
                    List.of(
                            buildCategorySettingDto(0L, "한식", 10L, "백반", true),
                            buildCategorySettingDto(1L, "중식", 11L, "짜장면", false),
                            buildCategorySettingDto(3L, "카페", 21L, "디저트", true)
                    )
            );
            final MemberAnalysisSettingDTO memberSettingDto2 = buildMemberAnalysisSettingDto(
                    2L, 127.223456, 37.887654,
                    "서울특별시 중구 을지로 12", "회사 근처 점심 추천",
                    List.of(
                            buildCategorySettingDto(0L, "한식", 12L, "된장찌개", false),
                            buildCategorySettingDto(2L, "일식", 13L, "스시", true),
                            buildCategorySettingDto(4L, "양식", 14L, "파스타", true),
                            buildCategorySettingDto(5L, "분식", 15L, "떡볶이", false)
                    )
            );
            givenMemberAnalysisSettings = List.of(memberSettingDto1, memberSettingDto2);

            when(groupRepository.findById(givenGroupId)).thenReturn(Optional.of(givenGroup));
            doNothing().when(analysisStartPolicy).validate(givenGroupId);
            when(analysisRepository.findByGroupGroupId(givenGroupId)).thenReturn(Optional.of(givenAnalysis));
            when(analysisSettingRepository.findMemberAnalysisSettingsByGroupId(givenGroupId))
                    .thenReturn(givenMemberAnalysisSettings);
        }

        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {
            @Test
            @DisplayName("분석시작 내부 처리과정이 올바른 순서로 진행되어야 한다.")
            void analysisStart_success_tc_01() {
                // when
                analysisService.analysisStart(givenRequest);

                // then
                InOrder inOrder = inOrder(groupRepository, analysisStartPolicy, analysisRepository, analysisSettingRepository);
                inOrder.verify(groupRepository).findById(givenGroupId);
                inOrder.verify(analysisStartPolicy).validate(givenGroupId);
                inOrder.verify(analysisRepository).findByGroupGroupId(givenGroupId);
                inOrder.verify(analysisSettingRepository).findMemberAnalysisSettingsByGroupId(givenGroupId);
                verifyNoMoreInteractions(groupRepository, analysisStartPolicy, analysisRepository, analysisSettingRepository);
            }

            @Test
            @DisplayName("분석시작 이벤트가 정확히 1건 발행되어야 한다.")
            void analysisStart_success_tc_02() {
                // when
                analysisService.analysisStart(givenRequest);

                // then
                final List<AnalysisStartEvent> events = applicationEvents.stream(AnalysisStartEvent.class).toList();
                assertThat(events).hasSize(1);
            }

            @Test
            @DisplayName("발행된 이벤트의 내부 데이터가 요청한 분석정보와 동일해야한다.")
            void analysisStart_success_tc_03() {
                // when
                analysisService.analysisStart(givenRequest);

                // then
                final AnalysisStartEvent event = applicationEvents.stream(AnalysisStartEvent.class).toList().get(0);

                assertThat(event.groupId()).isEqualTo(givenGroup.getGroupId());
                assertThat(event.analysisId()).isEqualTo(givenAnalysis.getAnalysisId());

                final List<AIAnalysisDTO.Request.MemberSetting> expectedMembers =
                        givenMemberAnalysisSettings.stream()
                                .map(ms -> AIAnalysisDTO.Request.MemberSetting.builder()
                                        .memberId(ms.getMemberId())
                                        .xPosition(ms.getXPosition())
                                        .yPosition(ms.getYPosition())
                                        .roadnameAddress(ms.getRoadnameAddress())
                                        .inputText(ms.getInputText())
                                        .categoryList(
                                                ms.getCategorySettings().stream()
                                                        .map(cs -> AIAnalysisDTO.Request.MemberSetting.CategorySetting.builder()
                                                                .categoryId(cs.getCategoryId())
                                                                .categoryName(cs.getCategoryName())
                                                                .categoryTagId(cs.getCategoryTagId())
                                                                .categoryTagName(cs.getCategoryTagName())
                                                                .isPreferred(cs.getIsPreferred())
                                                                .build())
                                                        .toList()
                                        )
                                        .build())
                                .toList();

                assertThat(event.memberSettingList())
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expectedMembers);
            }

            @Test
            @DisplayName("분석시작 후 그룹의 분석상태가 진행중으로 변경되어야 한다.")
            void analysisStart_success_tc_04() {
                // when
                analysisService.analysisStart(givenRequest);

                // then
                assertThat(givenAnalysis.getAnalysisStatus()).isEqualTo(AnalysisStatus.IN_PROGRESS);
            }

            @Test
            @DisplayName("분석시작 후 반환된 응답이 요청한 그룹의 분석 정보와 일치해야 한다.")
            void analysisStart_success_tc_05() {
                // when
                final AnalysisStartDTO.Response response = analysisService.analysisStart(givenRequest);

                // then
                assertThat(response.getGroupId()).isEqualTo(givenGroup.getGroupId());
                assertThat(response.getAnalysisStatus()).isEqualTo(givenAnalysis.getAnalysisStatus().toString());
                assertThat(response.getAnalysisId()).isEqualTo(givenAnalysis.getAnalysisId());
            }
        }


        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {
            @Test
            @DisplayName("이미 분석이 시작된 그룹인 경우 `AnalysisAlreadyStartedForGroupIdException` 예외가 발생해야 한다.")
            void analysisStart_exception_tc_01() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                final Group mockGroup = mock(Group.class);

                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.of(mockGroup));
                when(mockGroup.getGroupId()).thenReturn(givenGroupId);

                // when
                doThrow(AnalysisAlreadyStartedForGroupIdException.class).
                        when(analysisStartPolicy).validate(givenGroupId);

                // then
                assertThrows(AnalysisAlreadyStartedForGroupIdException.class,
                        () -> analysisService.analysisStart(AnalysisStartDTO.Request.builder()
                                .groupId(givenGroupId)
                                .build())
                );
            }

            @Test
            @DisplayName("분석제출 조건을 만족하지 못한 그룹인 경우 `AnalysisConditionNotSatisfiedForGroupIdException` 예외가 발생해야 한다.")
            void analysisStart_exception_tc_02() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                final Group mockGroup = mock(Group.class);

                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.of(mockGroup));
                when(mockGroup.getGroupId()).thenReturn(givenGroupId);

                // when
                doThrow(AnalysisConditionNotSatisfiedForGroupIdException.class).
                        when(analysisStartPolicy).validate(givenGroupId);

                // then
                assertThrows(AnalysisConditionNotSatisfiedForGroupIdException.class,
                        () -> analysisService.analysisStart(AnalysisStartDTO.Request.builder()
                                .groupId(givenGroupId)
                                .build())
                );
            }

            @Test
            @DisplayName("존재하지 않는 그룹 식별자인 경우 `GroupNotFoundException` 예외가 발생해야 한다.")
            void analysisStart_exception_tc_03() {
                // given
                final String givenGroupId = UUID.randomUUID().toString().replace("-", "");
                final AnalysisStartDTO.Request request = mock(AnalysisStartDTO.Request.class);

                when(request.getGroupId()).thenReturn(givenGroupId);
                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.empty());

                // when & then
                assertThrows(GroupNotFoundException.class,
                        () -> analysisService.analysisStart(request)
                );
            }

            @Test
            @DisplayName("그룹에 대한 분석정보가 사전에 생성되어 있지 않은 경우 `AnalysisNotFoundForGroupIdException` 예외가 발생하여야 한다.")
            void analysisStart_exception_tc_04() {
                // given
                when(groupRepository.findById(givenGroupId)).thenReturn(Optional.of(givenGroup));
                doNothing().when(analysisStartPolicy).validate(givenGroup.getGroupId());
                when(analysisRepository.findByGroupGroupId(givenGroup.getGroupId()))
                        .thenReturn(Optional.empty());

                // when & then
                assertThrows(AnalysisNotFoundForGroupIdException.class,
                        () -> analysisService.analysisStart(givenRequest)
                );
            }
        }

        private static MemberAnalysisSettingDTO buildMemberAnalysisSettingDto(
                long memberId, double x, double y,
                String roadnameAddress, String inputText,
                List<MemberAnalysisSettingDTO.CategorySetting> categories
        ) {
            return MemberAnalysisSettingDTO.builder()
                    .memberId(memberId)
                    .xPosition(x)
                    .yPosition(y)
                    .roadnameAddress(roadnameAddress)
                    .inputText(inputText)
                    .categorySettings(categories)
                    .build();
        }

        private static MemberAnalysisSettingDTO.CategorySetting buildCategorySettingDto(
                long categoryId, String categoryName,
                long categoryTagId, String categoryTagName, boolean isPreferred
        ) {
            return MemberAnalysisSettingDTO.CategorySetting.builder()
                    .categoryId(categoryId)
                    .categoryName(categoryName)
                    .categoryTagId(categoryTagId)
                    .categoryTagName(categoryTagName)
                    .isPreferred(isPreferred)
                    .build();
        }
    }
}
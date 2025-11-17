package com.waguwagu.weat.domain.analysis.service;

import com.waguwagu.weat.domain.analysis.event.AnalysisStartEvent;
import com.waguwagu.weat.domain.analysis.exception.AnalysisAlreadyStartedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisConditionNotSatisfiedForGroupIdException;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.model.dto.AIAnalysisDTO;
import com.waguwagu.weat.domain.analysis.model.dto.AnalysisStartDTO;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import com.waguwagu.weat.global.TestContainersConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.waguwagu.weat.domain.analysis.handler.AnalysisStartEventHandler;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import({TestContainersConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@RecordApplicationEvents
@Transactional
class AnalysisServiceIntegrationTest {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private AnalysisSettingRepository analysisSettingRepository;

    @Autowired
    private AnalysisSettingDetailRepository analysisSettingDetailRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTagRepository categoryTagRepository;

    @Autowired
    ApplicationEvents applicationEvents;

    @SpyBean
    AnalysisStartEventHandler analysisStartEventHandler;

    @SpyBean
    AnalysisAsyncExecutor analysisAsyncExecutor;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    private Group testMultiMemberGroup;
    private Group testSingleMemberGroup;
    private Analysis testAnalysisForMultiMemberGroup;
    private Analysis testAnalysisForSingleMemberGroup;
    private List<Member> testMultiMemberGroupMembers;
    private Member testSingleMemberGroupMember;
    private List<Category> testCategories;
    private List<CategoryTag> testCategoryTags;

    @BeforeEach
    void setUp() {
        testMultiMemberGroup = groupRepository.save(Group.builder()
                .isSingleMemberGroup(false)
                .build());

        testSingleMemberGroup = groupRepository.save(Group.builder()
                .isSingleMemberGroup(true)
                .build());

        testMultiMemberGroupMembers = List.of(
                memberRepository.save(Member.builder()
                        .group(testMultiMemberGroup)
                        .isGroupOwner(true)
                        .build()),
                memberRepository.save(Member.builder()
                        .group(testMultiMemberGroup)
                        .isGroupOwner(false)
                        .build()),
                memberRepository.save(Member.builder()
                        .group(testMultiMemberGroup)
                        .isGroupOwner(false)
                        .build())
        );

        testSingleMemberGroupMember = memberRepository.save(Member.builder()
                .group(testSingleMemberGroup)
                .isGroupOwner(true)
                .build());

        testAnalysisForMultiMemberGroup = analysisRepository.save(Analysis.builder()
                .group(testMultiMemberGroup)
                .analysisStatus(AnalysisStatus.NOT_STARTED)
                .build());

        testAnalysisForSingleMemberGroup = analysisRepository.save(Analysis.builder()
                .group(testSingleMemberGroup)
                .analysisStatus(AnalysisStatus.NOT_STARTED)
                .build());

        testCategories = List.of(
                categoryRepository.save(Category.builder()
                        .categoryName("한식")
                        .categoryOrder(1L)
                        .build()),
                categoryRepository.save(Category.builder()
                        .categoryName("일식")
                        .categoryOrder(2L)
                        .build()),
                categoryRepository.save(Category.builder()
                        .categoryName("양식")
                        .categoryOrder(3L)
                        .build())
        );

        testCategoryTags = List.of(
                categoryTagRepository.save(CategoryTag.builder()
                        .category(testCategories.get(0))
                        .categoryTagName("백반")
                        .categoryTagOrder(1L)
                        .categoryTagVersion("v2")
                        .build()),
                categoryTagRepository.save(CategoryTag.builder()
                        .category(testCategories.get(0))
                        .categoryTagName("삼겹살")
                        .categoryTagOrder(2L)
                        .categoryTagVersion("v2")
                        .build()),
                categoryTagRepository.save(CategoryTag.builder()
                        .category(testCategories.get(1))
                        .categoryTagName("스시")
                        .categoryTagOrder(3L)
                        .categoryTagVersion("v2")
                        .build())
        );
    }

    private void createMemberAnalysisSetting(
            Member member,
            Analysis analysis,
            Double xPosition,
            Double yPosition,
            String roadnameAddress,
            String inputText,
            List<CategoryPreferredSetting> categorySettings
    ) {
        AnalysisSetting analysisSetting = analysisSettingRepository.save(
                AnalysisSetting.builder()
                        .analysis(analysis)
                        .member(member)
                        .build());

        analysisSettingDetailRepository.save(LocationSetting.builder()
                .analysisSetting(analysisSetting)
                .xPosition(xPosition)
                .yPosition(yPosition)
                .roadnameAddress(roadnameAddress)
                .build());

        if (inputText != null) {
            analysisSettingDetailRepository.save(TextInputSetting.builder()
                    .analysisSetting(analysisSetting)
                    .inputText(inputText)
                    .build());
        }

        for (CategoryPreferredSetting categorySetting : categorySettings) {
            analysisSettingDetailRepository.save(CategorySetting.builder()
                    .analysisSetting(analysisSetting)
                    .category(categorySetting.category())
                    .categoryTag(categorySetting.categoryTag())
                    .isPreferred(categorySetting.isPreferred())
                    .build());
        }
    }

    private record CategoryPreferredSetting(
            Category category,
            CategoryTag categoryTag,
            boolean isPreferred
    ) {
    }

    @Nested
    @DisplayName("analysisStart - 분석시작")
    class AnalysisStart {

        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {

            @Test
            @DisplayName("다중 멤버 그룹에서 2명 이상의 설정이 제출된 경우 분석이 시작되어야 한다.")
            void analysisStart_success_tc_01() {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);
                final Member givenMember2 = testMultiMemberGroupMembers.get(1);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = "분위기 좋은 카페를 찾고 있어요";

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true),
                                new CategoryPreferredSetting(testCategories.get(1), testCategoryTags.get(2), false)
                        )
                );

                createMemberAnalysisSetting(
                        givenMember2,
                        testAnalysisForMultiMemberGroup,
                        givenMember2XPosition,
                        givenMember2YPosition,
                        givenMember2RoadnameAddress,
                        givenMember2InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(1), false)
                        )
                );

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                // when
                final AnalysisStartDTO.Response response = analysisService.analysisStart(givenRequest);

                // then
                assertThat(response.getGroupId()).isEqualTo(testMultiMemberGroup.getGroupId());
                assertThat(response.getAnalysisId()).isEqualTo(testAnalysisForMultiMemberGroup.getAnalysisId());
                assertThat(response.getAnalysisStatus()).isEqualTo(AnalysisStatus.IN_PROGRESS.toString());

                final Analysis savedAnalysis = analysisRepository.findById(testAnalysisForMultiMemberGroup.getAnalysisId())
                        .orElseThrow();
                assertThat(savedAnalysis.getAnalysisStatus()).isEqualTo(AnalysisStatus.IN_PROGRESS);

                final List<AnalysisStartEvent> events = applicationEvents.stream(AnalysisStartEvent.class).toList();
                assertThat(events).hasSize(1);

                final AnalysisStartEvent event = events.get(0);
                assertThat(event.groupId()).isEqualTo(testMultiMemberGroup.getGroupId());
                assertThat(event.analysisId()).isEqualTo(testAnalysisForMultiMemberGroup.getAnalysisId());
                assertThat(event.memberSettingList()).hasSize(2);
            }

            @Test
            @DisplayName("단일 멤버 그룹에서 1명의 설정이 제출된 경우 분석이 시작되어야 한다.")
            void analysisStart_success_tc_02() {
                // given
                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                createMemberAnalysisSetting(
                        testSingleMemberGroupMember,
                        testAnalysisForSingleMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true)
                        )
                );

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testSingleMemberGroup.getGroupId())
                        .build();

                // when
                final AnalysisStartDTO.Response response = analysisService.analysisStart(givenRequest);

                // then
                assertThat(response.getGroupId()).isEqualTo(testSingleMemberGroup.getGroupId());
                assertThat(response.getAnalysisId()).isEqualTo(testAnalysisForSingleMemberGroup.getAnalysisId());
                assertThat(response.getAnalysisStatus()).isEqualTo(AnalysisStatus.IN_PROGRESS.toString());

                final Analysis savedAnalysis = analysisRepository.findById(testAnalysisForSingleMemberGroup.getAnalysisId())
                        .orElseThrow();
                assertThat(savedAnalysis.getAnalysisStatus()).isEqualTo(AnalysisStatus.IN_PROGRESS);

                final List<AnalysisStartEvent> events = applicationEvents.stream(AnalysisStartEvent.class).toList();
                assertThat(events).hasSize(1);

                final AnalysisStartEvent event = events.get(0);
                assertThat(event.groupId()).isEqualTo(testSingleMemberGroup.getGroupId());
                assertThat(event.analysisId()).isEqualTo(testAnalysisForSingleMemberGroup.getAnalysisId());
                assertThat(event.memberSettingList()).hasSize(1);
            }

            @Test
            @DisplayName("분석시작 후 이벤트에 포함된 멤버 설정 정보가 실제 제출된 설정과 일치해야 한다.")
            void analysisStart_success_tc_03() {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);
                final Member givenMember2 = testMultiMemberGroupMembers.get(1);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = "분위기 좋은 카페를 찾고 있어요";

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true),
                                new CategoryPreferredSetting(testCategories.get(1), testCategoryTags.get(2), false)
                        )
                );

                createMemberAnalysisSetting(
                        givenMember2,
                        testAnalysisForMultiMemberGroup,
                        givenMember2XPosition,
                        givenMember2YPosition,
                        givenMember2RoadnameAddress,
                        givenMember2InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(1), false)
                        )
                );

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                // when
                analysisService.analysisStart(givenRequest);

                // then
                final AnalysisStartEvent event = applicationEvents.stream(AnalysisStartEvent.class)
                        .findFirst()
                        .orElseThrow();

                assertThat(event.memberSettingList()).hasSize(2);

                final AIAnalysisDTO.Request.MemberSetting member1Setting = event.memberSettingList().stream()
                        .filter(ms -> ms.getMemberId().equals(givenMember1.getMemberId()))
                        .findFirst()
                        .orElseThrow();

                assertThat(member1Setting.getMemberId()).isEqualTo(givenMember1.getMemberId());
                assertThat(member1Setting.getXPosition()).isEqualTo(givenMember1XPosition);
                assertThat(member1Setting.getYPosition()).isEqualTo(givenMember1YPosition);
                assertThat(member1Setting.getRoadnameAddress()).isEqualTo(givenMember1RoadnameAddress);
                assertThat(member1Setting.getInputText()).isEqualTo(givenMember1InputText);
                assertThat(member1Setting.getCategoryList()).hasSize(2);

                final AIAnalysisDTO.Request.MemberSetting member2Setting = event.memberSettingList().stream()
                        .filter(ms -> ms.getMemberId().equals(givenMember2.getMemberId()))
                        .findFirst()
                        .orElseThrow();

                assertThat(member2Setting.getMemberId()).isEqualTo(givenMember2.getMemberId());
                assertThat(member2Setting.getXPosition()).isEqualTo(givenMember2XPosition);
                assertThat(member2Setting.getYPosition()).isEqualTo(givenMember2YPosition);
                assertThat(member2Setting.getRoadnameAddress()).isEqualTo(givenMember2RoadnameAddress);
                assertThat(member2Setting.getInputText()).isEqualTo(givenMember2InputText);
                assertThat(member2Setting.getCategoryList()).hasSize(1);
            }

            @Test
            @DisplayName("텍스트 입력 설정이 없는 멤버의 설정도 이벤트에 포함되어야 한다.")
            void analysisStart_success_tc_04() {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);
                final Member givenMember2 = testMultiMemberGroupMembers.get(1);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = null;

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true)
                        )
                );

                createMemberAnalysisSetting(
                        givenMember2,
                        testAnalysisForMultiMemberGroup,
                        givenMember2XPosition,
                        givenMember2YPosition,
                        givenMember2RoadnameAddress,
                        givenMember2InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(1), false)
                        )
                );

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                // when
                analysisService.analysisStart(givenRequest);

                // then
                final AnalysisStartEvent event = applicationEvents.stream(AnalysisStartEvent.class)
                        .findFirst()
                        .orElseThrow();

                assertThat(event.memberSettingList()).hasSize(2);

                final AIAnalysisDTO.Request.MemberSetting member2Setting = event.memberSettingList().stream()
                        .filter(ms -> ms.getMemberId().equals(givenMember2.getMemberId()))
                        .findFirst()
                        .orElseThrow();

                assertThat(member2Setting.getInputText()).isNull();
            }

            @Test
            @DisplayName("분석시작이 정상적으로 완료되면 데이터베이스에 분석 상태가 저장되고 핸들러가 실행되어야 한다.")
            void analysisStart_success_tc_05() {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);
                final Member givenMember2 = testMultiMemberGroupMembers.get(1);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = "분위기 좋은 카페를 찾고 있어요";

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true)
                        )
                );

                createMemberAnalysisSetting(
                        givenMember2,
                        testAnalysisForMultiMemberGroup,
                        givenMember2XPosition,
                        givenMember2YPosition,
                        givenMember2RoadnameAddress,
                        givenMember2InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(1), false)
                        )
                );

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                doNothing().when(analysisAsyncExecutor)
                        .startAnalysisAsync(any(AIAnalysisDTO.Request.class));

                // when
                TestTransaction.flagForCommit();
                TestTransaction.end();

                TestTransaction.start();
                analysisService.analysisStart(givenRequest);
                TestTransaction.flagForCommit();
                TestTransaction.end();

                // then
                final Analysis savedAnalysis = analysisRepository.findById(testAnalysisForMultiMemberGroup.getAnalysisId())
                        .orElseThrow();
                assertThat(savedAnalysis.getAnalysisStatus()).isEqualTo(AnalysisStatus.IN_PROGRESS);

                final List<AnalysisStartEvent> events = applicationEvents.stream(AnalysisStartEvent.class).toList();
                assertThat(events).hasSize(1);

                verify(analysisAsyncExecutor, timeout(3000))
                        .startAnalysisAsync(any(AIAnalysisDTO.Request.class));
            }

            @Test
            @DisplayName("분석시작이 정상적으로 완료되어 트랜잭션이 커밋되면 이벤트 핸들러가 실행되어 AI 분석요청이 시작되어야 한다.")
            void analysisStart_success_tc_06() {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);
                final Member givenMember2 = testMultiMemberGroupMembers.get(1);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = "분위기 좋은 카페를 찾고 있어요";

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true),
                                new CategoryPreferredSetting(testCategories.get(1), testCategoryTags.get(2), false)
                        )
                );

                createMemberAnalysisSetting(
                        givenMember2,
                        testAnalysisForMultiMemberGroup,
                        givenMember2XPosition,
                        givenMember2YPosition,
                        givenMember2RoadnameAddress,
                        givenMember2InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(1), false)
                        )
                );

                doNothing().when(analysisAsyncExecutor)
                        .startAnalysisAsync(any(AIAnalysisDTO.Request.class));

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                // when
                TestTransaction.flagForCommit();
                TestTransaction.end();

                TestTransaction.start();
                analysisService.analysisStart(givenRequest);
                TestTransaction.flagForCommit();
                TestTransaction.end();

                // then
                final List<AnalysisStartEvent> events = applicationEvents.stream(AnalysisStartEvent.class).toList();
                assertThat(events).hasSize(1);

                final AnalysisStartEvent event = events.get(0);
                assertThat(event.groupId()).isEqualTo(testMultiMemberGroup.getGroupId());
                assertThat(event.analysisId()).isEqualTo(testAnalysisForMultiMemberGroup.getAnalysisId());

                final ArgumentCaptor<AIAnalysisDTO.Request> requestCaptor =
                        ArgumentCaptor.forClass(AIAnalysisDTO.Request.class);
                verify(analysisAsyncExecutor, timeout(3000))
                        .startAnalysisAsync(requestCaptor.capture());

                final AIAnalysisDTO.Request aiAnalysisRequest = requestCaptor.getValue();
                assertThat(aiAnalysisRequest.getGroupId()).isEqualTo(testMultiMemberGroup.getGroupId());
                assertThat(aiAnalysisRequest.getAnalysisId()).isEqualTo(testAnalysisForMultiMemberGroup.getAnalysisId());
                assertThat(aiAnalysisRequest.getMemberSettingList()).hasSize(2);
            }
        }

        @Nested
        @DisplayName("EXCEPTION")
        class ExceptionTest {

            @Test
            @DisplayName("존재하지 않는 그룹 식별자인 경우 `GroupNotFoundException` 예외가 발생해야 한다.")
            void analysisStart_exception_tc_01() {
                // given
                final String givenNonExistentGroupId = UUID.randomUUID().toString().replace("-", "");
                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(givenNonExistentGroupId)
                        .build();

                // when & then
                assertThrows(GroupNotFoundException.class, () ->
                        analysisService.analysisStart(givenRequest)
                );
            }

            @Test
            @DisplayName("그룹에 대한 분석정보가 사전에 생성되어 있지 않은 경우 `AnalysisNotFoundForGroupIdException` 예외가 발생하여야 한다.")
            void analysisStart_exception_tc_02() {
                // given
                final Group givenGroupWithoutAnalysis = groupRepository.save(Group.builder()
                        .isSingleMemberGroup(false)
                        .build());

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(givenGroupWithoutAnalysis.getGroupId())
                        .build();

                // when & then
                assertThrows(AnalysisNotFoundForGroupIdException.class, () ->
                        analysisService.analysisStart(givenRequest)
                );
            }

            @Test
            @DisplayName("이미 분석이 시작된 그룹인 경우 `AnalysisAlreadyStartedForGroupIdException` 예외가 발생해야 한다.")
            void analysisStart_exception_tc_03() {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);
                final Member givenMember2 = testMultiMemberGroupMembers.get(1);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = "분위기 좋은 카페를 찾고 있어요";

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true)
                        )
                );

                createMemberAnalysisSetting(
                        givenMember2,
                        testAnalysisForMultiMemberGroup,
                        givenMember2XPosition,
                        givenMember2YPosition,
                        givenMember2RoadnameAddress,
                        givenMember2InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(1), false)
                        )
                );

                final AnalysisStartDTO.Request givenFirstRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();
                analysisService.analysisStart(givenFirstRequest);

                final Analysis analysis = analysisRepository.findByGroupGroupId(testMultiMemberGroup.getGroupId())
                        .orElseThrow();
                analysis.setAnalysisStatus(AnalysisStatus.IN_PROGRESS);
                analysisRepository.save(analysis);

                // when & then
                final AnalysisStartDTO.Request givenSecondRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                assertThrows(AnalysisAlreadyStartedForGroupIdException.class, () ->
                        analysisService.analysisStart(givenSecondRequest)
                );
            }

            @Test
            @DisplayName("다중 멤버 그룹에서 설정 제출 조건을 만족하지 못한 경우 `AnalysisConditionNotSatisfiedForGroupIdException` 예외가 발생해야 한다.")
            void analysisStart_exception_tc_04() {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true)
                        )
                );

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                // when & then
                assertThrows(AnalysisConditionNotSatisfiedForGroupIdException.class, () ->
                        analysisService.analysisStart(givenRequest)
                );
            }

            @Test
            @DisplayName("단일 멤버 그룹에서 설정이 제출되지 않은 경우 `AnalysisConditionNotSatisfiedForGroupIdException` 예외가 발생해야 한다.")
            void analysisStart_exception_tc_05() {
                // given
                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testSingleMemberGroup.getGroupId())
                        .build();

                // when & then
                assertThrows(AnalysisConditionNotSatisfiedForGroupIdException.class, () ->
                        analysisService.analysisStart(givenRequest)
                );
            }

            @Test
            @DisplayName("트랜잭션 롤백 시 분석시작 이벤트는 발행되지 않아야하며 이벤트 핸들러와 AI 분석요청이 실행되지 않아야 한다.")
            void analysisStart_exception_tc_06() throws InterruptedException {
                // given
                final Member givenMember1 = testMultiMemberGroupMembers.get(0);

                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                createMemberAnalysisSetting(
                        givenMember1,
                        testAnalysisForMultiMemberGroup,
                        givenMember1XPosition,
                        givenMember1YPosition,
                        givenMember1RoadnameAddress,
                        givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(testCategories.get(0), testCategoryTags.get(0), true)
                        )
                );

                final AnalysisStartDTO.Request givenRequest = AnalysisStartDTO.Request.builder()
                        .groupId(testMultiMemberGroup.getGroupId())
                        .build();

                // when
                assertThrows(AnalysisConditionNotSatisfiedForGroupIdException.class, () ->
                        analysisService.analysisStart(givenRequest)
                );

                Thread.sleep(100);

                // then
                final List<AnalysisStartEvent> events = applicationEvents.stream(AnalysisStartEvent.class).toList();
                assertThat(events).isEmpty();

                verify(analysisStartEventHandler, never())
                        .handleAnalysisStartEvent(any(AnalysisStartEvent.class));

                verify(analysisAsyncExecutor, never())
                        .startAnalysisAsync(any(AIAnalysisDTO.Request.class));

                final Analysis savedAnalysis = analysisRepository.findById(testAnalysisForMultiMemberGroup.getAnalysisId())
                        .orElseThrow();
                assertThat(savedAnalysis.getAnalysisStatus()).isEqualTo(AnalysisStatus.NOT_STARTED);
            }
        }
    }
}

package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.dto.MemberAnalysisSettingDto;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import com.waguwagu.weat.global.TestContainersConfig;
import com.waguwagu.weat.global.config.QuerydslTestConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestContainersConfig.class, QuerydslTestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AnalysisSettingRepositoryImplTest {

    @Autowired
    private AnalysisSettingRepositoryImpl analysisSettingRepositoryImpl;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTagRepository categoryTagRepository;

    @Autowired
    private AnalysisSettingRepository analysisSettingRepository;

    @Autowired
    private LocationSettingRepository locationSettingRepository;

    @Autowired
    private TextInputSettingRepository textInputSettingRepository;

    @Autowired
    private CategorySettingRepository categorySettingRepository;

    private Group testGroup;
    private Analysis testAnalysis;
    private List<Member> testMemberList = new ArrayList<>();
    private List<Category> testCategoryList = new ArrayList<>();
    private List<CategoryTag> testCategoryTagList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 그룹
        String testGroupId = UUID.randomUUID().toString().replace("-", "");
        boolean testGroupIsSingleMemberGroup = false;

        testGroup = groupRepository.save(Group.builder()
                .groupId(testGroupId)
                .isSingleMemberGroup(testGroupIsSingleMemberGroup)
                .build());

        // 멤버
        testMemberList = List.of(
                memberRepository.save(Member.builder().group(testGroup).isGroupOwner(true).build()),
                memberRepository.save(Member.builder().group(testGroup).isGroupOwner(false).build()),
                memberRepository.save(Member.builder().group(testGroup).isGroupOwner(false).build())
        );

        // 분석
        testAnalysis = analysisRepository.save(Analysis.builder()
                .group(testGroup)
                .analysisStatus(AnalysisStatus.NOT_STARTED)
                .build());

        // 카테고리 및 태그
        testCategoryList = List.of(
                categoryRepository.save(Category.builder()
                        .categoryName("한식")
                        .categoryOrder(1L)
                        .build()),
                categoryRepository.save(Category.builder()
                        .categoryName("일식")
                        .categoryOrder(2L)
                        .build())
        );

        testCategoryTagList = List.of(
                categoryTagRepository.save(CategoryTag.builder()
                        .category(testCategoryList.get(0))
                        .categoryTagName("국밥")
                        .categoryTagOrder(1L)
                        .categoryTagVersion("v2")
                        .build()),
                categoryTagRepository.save(CategoryTag.builder()
                        .category(testCategoryList.get(0))
                        .categoryTagName("삼겹살")
                        .categoryTagOrder(2L)
                        .categoryTagVersion("v2")
                        .build()),
                categoryTagRepository.save(CategoryTag.builder()
                        .category(testCategoryList.get(1))
                        .categoryTagName("스시")
                        .categoryTagOrder(3L)
                        .categoryTagVersion("v2")
                        .build()),
                categoryTagRepository.save(CategoryTag.builder()
                        .category(testCategoryList.get(1))
                        .categoryTagName("라멘")
                        .categoryTagOrder(4L)
                        .categoryTagVersion("v2")
                        .build())
        );
    }

    private record CategoryPreferredSetting(Category category, CategoryTag tag, boolean isPreferred) {
    }

    private void initMemberSettings(
            Member member, Double x, Double y, String address, String inputText,
            Category category, CategoryTag categoryTag, boolean isPreferred
    ) {
        initMemberSettings(member, x, y, address, inputText,
                List.of(new CategoryPreferredSetting(category, categoryTag, isPreferred)));
    }


    private void initMemberSettings(
            Member member, Double x, Double y, String address, String inputText,
            List<CategoryPreferredSetting> categoryPreferredSettings
    ) {
        AnalysisSetting analysisSetting = analysisSettingRepository.save(
                AnalysisSetting.builder()
                        .analysis(testAnalysis)
                        .member(member)
                        .build());

        locationSettingRepository.save(LocationSetting.builder()
                .analysisSetting(analysisSetting)
                .xPosition(x)
                .yPosition(y)
                .roadnameAddress(address)
                .build());

        if (StringUtils.isNotBlank(inputText)) {
            textInputSettingRepository.save(TextInputSetting.builder()
                    .analysisSetting(analysisSetting)
                    .inputText(inputText)
                    .build());
        }

        for (CategoryPreferredSetting p : categoryPreferredSettings) {
            categorySettingRepository.save(CategorySetting.builder()
                    .analysisSetting(analysisSetting)
                    .category(p.category())
                    .categoryTag(p.tag())
                    .isPreferred(p.isPreferred())
                    .build());
        }
    }

    @Nested
    @DisplayName("findMemberAnalysisSettingsByGroupId - 그룹식별자로 그룹내의 회원들의 설정 제출 리스트 조회")
    class FindMemberAnalysisSettingsByGroupId {

        @Nested
        @DisplayName("SUCCESS")
        class Success {

            @Test
            @DisplayName("그룹 내의 멤버들이 제출한 설정의 내용과 동일한 값으로 조회되어야 한다.")
            void findMemberAnalysisSettingsByGroupId_success_tc_01() {
                // given
                final Member givenMember1 = testMemberList.get(0);
                final Member givenMember2 = testMemberList.get(1);
                final Member givenMember3 = testMemberList.get(2);

                // member1 설정
                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";

                final Category givenMember1Category1 = testCategoryList.get(0);
                final CategoryTag givenMember1CategoryTag1 = testCategoryTagList.get(0);
                final boolean givenMember1IsPreferredForCategoryTag1 = true;

                final Category givenMember1Category2 = testCategoryList.get(1);
                final CategoryTag givenMember1CategoryTag2 = testCategoryTagList.get(2);
                final boolean givenMember1IsPreferredForCategoryTag2 = false;

                initMemberSettings(
                        givenMember1,
                        givenMember1XPosition, givenMember1YPosition,
                        givenMember1RoadnameAddress, givenMember1InputText,
                        List.of(
                                new CategoryPreferredSetting(givenMember1Category1, givenMember1CategoryTag1, givenMember1IsPreferredForCategoryTag1),
                                new CategoryPreferredSetting(givenMember1Category2, givenMember1CategoryTag2, givenMember1IsPreferredForCategoryTag2)
                        )
                );

                // member2 설정
                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = "분위기 좋은 카페를 찾고 있어요";
                final Category givenMember2Category = testCategoryList.get(0);
                final CategoryTag givenMember2CategoryTag = testCategoryTagList.get(1);
                final boolean givenMember2IsPreferred = false;

                initMemberSettings(
                        givenMember2,
                        givenMember2XPosition, givenMember2YPosition,
                        givenMember2RoadnameAddress, givenMember2InputText,
                        List.of(new CategoryPreferredSetting(givenMember2Category, givenMember2CategoryTag, givenMember2IsPreferred))
                );

                // member3 설정
                final double givenMember3XPosition = 128.6014;
                final double givenMember3YPosition = 35.8714;
                final String givenMember3RoadnameAddress = "경기도 성남시 분당구 판교로210번길";
                final String givenMember3InputText = "분식 맛집을 추천해주세요";
                final Category givenMember3Category = testCategoryList.get(1);
                final CategoryTag givenMember3CategoryTag = testCategoryTagList.get(2);
                final boolean givenMember3IsPreferred = true;

                initMemberSettings(
                        givenMember3,
                        givenMember3XPosition, givenMember3YPosition,
                        givenMember3RoadnameAddress, givenMember3InputText,
                        List.of(new CategoryPreferredSetting(givenMember3Category, givenMember3CategoryTag, givenMember3IsPreferred))
                );

                // when
                final List<MemberAnalysisSettingDto> results =
                        analysisSettingRepositoryImpl.findMemberAnalysisSettingsByGroupId(testGroup.getGroupId());

                // then
                assertThat(results).isNotNull();
                assertThat(results).hasSize(3);

                // member1 검증 (기본 필드)
                MemberAnalysisSettingDto member1AnalysisSettingDto = results.stream()
                        .filter(dto -> dto.getMemberId().equals(givenMember1.getMemberId()))
                        .findFirst().orElseThrow();

                assertThat(member1AnalysisSettingDto)
                        .extracting(
                                MemberAnalysisSettingDto::getXPosition,
                                MemberAnalysisSettingDto::getYPosition,
                                MemberAnalysisSettingDto::getRoadnameAddress,
                                MemberAnalysisSettingDto::getInputText
                        )
                        .containsExactly(
                                givenMember1XPosition,
                                givenMember1YPosition,
                                givenMember1RoadnameAddress,
                                givenMember1InputText
                        );

                // member1 카테고리 검증
                assertThat(member1AnalysisSettingDto.getCategorySettings())
                        .hasSize(2)
                        .extracting(
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryName,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagName,
                                MemberAnalysisSettingDto.CategorySettingDto::getIsPreferred
                        )
                        .containsExactlyInAnyOrder(
                                tuple(
                                        givenMember1Category1.getCategoryId(),
                                        givenMember1Category1.getCategoryName(),
                                        givenMember1CategoryTag1.getCategoryTagId(),
                                        givenMember1CategoryTag1.getCategoryTagName(),
                                        givenMember1IsPreferredForCategoryTag1
                                ),
                                tuple(
                                        givenMember1Category2.getCategoryId(),
                                        givenMember1Category2.getCategoryName(),
                                        givenMember1CategoryTag2.getCategoryTagId(),
                                        givenMember1CategoryTag2.getCategoryTagName(),
                                        givenMember1IsPreferredForCategoryTag2
                                )
                        );

                // member2 검증
                MemberAnalysisSettingDto member2AnalysisSettingDto = results.stream()
                        .filter(dto -> dto.getMemberId().equals(givenMember2.getMemberId()))
                        .findFirst().orElseThrow();

                assertThat(member2AnalysisSettingDto)
                        .extracting(
                                MemberAnalysisSettingDto::getXPosition,
                                MemberAnalysisSettingDto::getYPosition,
                                MemberAnalysisSettingDto::getRoadnameAddress,
                                MemberAnalysisSettingDto::getInputText
                        )
                        .containsExactly(
                                givenMember2XPosition,
                                givenMember2YPosition,
                                givenMember2RoadnameAddress,
                                givenMember2InputText
                        );

                assertThat(member2AnalysisSettingDto.getCategorySettings())
                        .hasSize(1)
                        .extracting(
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryName,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagName,
                                MemberAnalysisSettingDto.CategorySettingDto::getIsPreferred
                        )
                        .containsExactly(tuple(
                                givenMember2Category.getCategoryId(),
                                givenMember2Category.getCategoryName(),
                                givenMember2CategoryTag.getCategoryTagId(),
                                givenMember2CategoryTag.getCategoryTagName(),
                                givenMember2IsPreferred
                        ));

                // member3 검증
                MemberAnalysisSettingDto member3AnalysisSettingDto = results.stream()
                        .filter(dto -> dto.getMemberId().equals(givenMember3.getMemberId()))
                        .findFirst().orElseThrow();

                assertThat(member3AnalysisSettingDto)
                        .extracting(
                                MemberAnalysisSettingDto::getXPosition,
                                MemberAnalysisSettingDto::getYPosition,
                                MemberAnalysisSettingDto::getRoadnameAddress,
                                MemberAnalysisSettingDto::getInputText
                        )
                        .containsExactly(
                                givenMember3XPosition,
                                givenMember3YPosition,
                                givenMember3RoadnameAddress,
                                givenMember3InputText
                        );

                assertThat(member3AnalysisSettingDto.getCategorySettings())
                        .hasSize(1)
                        .extracting(
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryName,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagName,
                                MemberAnalysisSettingDto.CategorySettingDto::getIsPreferred
                        )
                        .containsExactly(tuple(
                                givenMember3Category.getCategoryId(),
                                givenMember3Category.getCategoryName(),
                                givenMember3CategoryTag.getCategoryTagId(),
                                givenMember3CategoryTag.getCategoryTagName(),
                                givenMember3IsPreferred
                        ));
            }

            @Test
            @DisplayName("설정을 제출하지 않은 멤버에 대한 정보는 조회 결과에 포함되지 않아야 한다.")
            void findMemberAnalysisSettingsByGroupId_success_tc_02() {
                // given
                final Member givenMember1 = testMemberList.get(0);
                final Member givenMember2 = testMemberList.get(1);
                final Member givenMember3 = testMemberList.get(2);

                // member1 설정
                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = "맛있는 한식집을 찾고 있어요";
                final Category givenMember1Category = testCategoryList.get(0);
                final CategoryTag givenMember1CategoryTag = testCategoryTagList.get(0);
                final boolean givenMember1IsPreferred = true;

                // member2 설정
                final double givenMember2XPosition = 126.9784;
                final double givenMember2YPosition = 37.5665;
                final String givenMember2RoadnameAddress = "서울시 종로구 세종대로 1";
                final String givenMember2InputText = "분위기 좋은 카페를 찾고 있어요";
                final Category givenMember2Category = testCategoryList.get(0);
                final CategoryTag givenMember2CategoryTag = testCategoryTagList.get(1);
                final boolean givenMember2IsPreferred = false;

                // 멤버1, 2만 분석 설정을 가지고, 멤버3는 설정이 없음
                initMemberSettings(givenMember1, givenMember1XPosition, givenMember1YPosition,
                        givenMember1RoadnameAddress, givenMember1InputText,
                        givenMember1Category, givenMember1CategoryTag,
                        givenMember1IsPreferred);

                initMemberSettings(givenMember2, givenMember2XPosition, givenMember2YPosition,
                        givenMember2RoadnameAddress, givenMember2InputText,
                        givenMember2Category, givenMember2CategoryTag,
                        givenMember2IsPreferred);

                // when
                final List<MemberAnalysisSettingDto> results = analysisSettingRepositoryImpl
                        .findMemberAnalysisSettingsByGroupId(testGroup.getGroupId());

                // then
                assertThat(results)
                        .isNotNull()
                        .hasSize(2)
                        .extracting(MemberAnalysisSettingDto::getMemberId)
                        .containsExactlyInAnyOrder(
                                givenMember1.getMemberId(),
                                givenMember2.getMemberId()
                        )
                        .doesNotContain(givenMember3.getMemberId());
            }

            @Test
            @DisplayName("존재하지 않는 그룹 식별자로 조회하면 빈 리스트가 반환되어야 한다.")
            void findMemberAnalysisSettingsByGroupId_success_tc_03() {
                // when
                final List<MemberAnalysisSettingDto> results = analysisSettingRepositoryImpl
                        .findMemberAnalysisSettingsByGroupId("non-existent-group-id");

                // then
                assertThat(results).isNotNull();
                assertThat(results).isEmpty();
            }

            @Test
            @DisplayName("그룹내 멤버의 설정 중 비정형 입력 설정이 누락된 멤버의 설정도 조회할 수 있어야 한다.")
            void findMemberAnalysisSettingsByGroupId_success_tc_04() {
                // given
                final Member givenMember1 = testMemberList.get(0);

                // member1 설정 (비정형 입력 설정은 누락된 경우)
                final double givenMember1XPosition = 127.0123;
                final double givenMember1YPosition = 37.1234;
                final String givenMember1RoadnameAddress = "서울시 강남구 테헤란로 123";
                final String givenMember1InputText = null;
                final Category givenMember1Category = testCategoryList.get(0);
                final CategoryTag givenMember1CategoryTag = testCategoryTagList.get(0);
                final boolean givenMember1IsPreferred = true;

                initMemberSettings(givenMember1, givenMember1XPosition, givenMember1YPosition,
                        givenMember1RoadnameAddress, givenMember1InputText,
                        givenMember1Category, givenMember1CategoryTag,
                        givenMember1IsPreferred);

                // when
                final List<MemberAnalysisSettingDto> results = analysisSettingRepositoryImpl
                        .findMemberAnalysisSettingsByGroupId(testGroup.getGroupId());

                // then
                assertThat(results).hasSize(1);

                MemberAnalysisSettingDto member1AnalysisSettingDto = results.get(0);
                assertThat(member1AnalysisSettingDto)
                        .extracting(
                                MemberAnalysisSettingDto::getMemberId,
                                MemberAnalysisSettingDto::getXPosition,
                                MemberAnalysisSettingDto::getYPosition,
                                MemberAnalysisSettingDto::getRoadnameAddress,
                                MemberAnalysisSettingDto::getInputText
                        )
                        .containsExactly(
                                givenMember1.getMemberId(),
                                givenMember1XPosition,
                                givenMember1YPosition,
                                givenMember1RoadnameAddress,
                                givenMember1InputText
                        );

                assertThat(member1AnalysisSettingDto.getCategorySettings())
                        .hasSize(1)
                        .extracting(
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryName,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagId,
                                MemberAnalysisSettingDto.CategorySettingDto::getCategoryTagName,
                                MemberAnalysisSettingDto.CategorySettingDto::getIsPreferred
                        )
                        .containsExactly(
                                tuple(
                                        givenMember1Category.getCategoryId(),
                                        givenMember1Category.getCategoryName(),
                                        givenMember1CategoryTag.getCategoryTagId(),
                                        givenMember1CategoryTag.getCategoryTagName(),
                                        givenMember1IsPreferred
                                )
                        );
            }

            @Test
            @DisplayName("그룹 식별자를 null로 조회한 경우 빈 리스트가 반환되어야 한다.")
            void findMemberAnalysisSettingsByGroupId_success_tc_05() {
                // when
                final List<MemberAnalysisSettingDto> results = analysisSettingRepositoryImpl
                        .findMemberAnalysisSettingsByGroupId(null);

                // then
                assertThat(results)
                        .isNotNull()
                        .isEmpty();
            }

            @Test
            @DisplayName("그룹 식별자를 빈문자열로 조회하면 빈 리스트가 반환되어야 한다.")
            void findMemberAnalysisSettingsByGroupId_success_tc_06() {
                // when
                final List<MemberAnalysisSettingDto> results = analysisSettingRepositoryImpl
                        .findMemberAnalysisSettingsByGroupId("");

                // then
                assertThat(results)
                        .isNotNull()
                        .isEmpty();
            }
        }
    }
}
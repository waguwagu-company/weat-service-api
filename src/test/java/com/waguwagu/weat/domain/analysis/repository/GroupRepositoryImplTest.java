package com.waguwagu.weat.domain.analysis.repository;

import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.group.model.dto.GroupAnalysisBasisQueryDTO;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.GroupRepositoryImpl;
import com.waguwagu.weat.global.TestContainersConfig;
import com.waguwagu.weat.global.config.QuerydslTestConfig;
import jakarta.persistence.EntityManager;
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
import java.util.stream.Collectors;

import static com.waguwagu.weat.domain.analysis.model.entity.AnalysisStatus.COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestContainersConfig.class, QuerydslTestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private GroupRepositoryImpl groupRepositoryImpl;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private AnalysisResultDetailRepository analysisResultDetailRepository;

    @Autowired
    private AnalysisBasisRepository analysisBasisRepository;

    @Autowired
    private PlaceImageRepository placeImageRepository;

    private Group testGroup;

    @BeforeEach
    void setUp() {
        testGroup = groupRepository.save(Group.builder()
                .groupId(UUID.randomUUID().toString().replace("-", ""))
                .isSingleMemberGroup(false)
                .build());
    }


    private class TestDataFactory {
        Place createPlace(String name) {
            return placeRepository.save(Place.builder()
                    .placeName(name)
                    .placeRoadnameAddress("addr-" + name)
                    .placeUrl("https://place/" + name).build());
        }

        Analysis createAnalysis(Group g) {
            return analysisRepository.save(Analysis.builder()
                    .group(g)
                    .analysisStatus(COMPLETED)
                    .build());
        }

        AnalysisResult createAnalysisResult(Group g, Analysis a) {
            return analysisResultRepository.save(AnalysisResult.builder()
                    .group(g)
                    .analysis(a)
                    .build());
        }

        AnalysisResultDetail createResultDetail(AnalysisResult ar, Place p, List<String> kws) {
            return analysisResultDetailRepository.save(AnalysisResultDetail.builder()
                    .analysisResult(ar)
                    .place(p)
                    .analysisResultKeywords(kws)
                    .build());
        }

        AnalysisBasis createAnalysisBasis(AnalysisResultDetail ard, int score, String basisType, String content) {
            return analysisBasisRepository.save(AnalysisBasis.builder()
                    .analysisResultDetail(ard)
                    .analysisScore(score)
                    .analysisBasisType(basisType)
                    .analysisBasisContent(content)
                    .build());
        }

        PlaceImage createPlaceImage(Place p, String url) {
            return placeImageRepository.save(PlaceImage.builder()
                    .place(p)
                    .placeImageUrl(url)
                    .build());
        }
    }

    @Nested
    @DisplayName("findGroupAnalysisBasisByGroupId - 그룹 식별자로 그룹 분석 결과 및 근거 조회")
    class FindGroupAnalysisBasisByGroupId {

        @Nested
        @DisplayName("SUCCESS")
        class Success {
            @Test
            @DisplayName("place별로 AnalysisBasis의 최댓값 점수만 선택되어야 한다.")
            void selectsMaxScorePerPlace() {
                TestDataFactory factory = new TestDataFactory();

                // given: 장소 2개
                var analysis = factory.createAnalysis(testGroup);
                var result = factory.createAnalysisResult(testGroup, analysis);

                var p1 = factory.createPlace("소갈비");
                var p2 = factory.createPlace("갈비탕");

                // p1: [50, 95] → 95 선택되어야 함
                var p1d1 = factory.createResultDetail(result, p1, List.of());
                var p1d2 = factory.createResultDetail(result, p1, List.of());
                factory.createAnalysisBasis(p1d1, 50, "REVIEW", "보통이었어요.");
                factory.createAnalysisBasis(p1d2, 95, "REVIEW", "훌륭해요.");

                // p2: [40, 70] → 70 선택되어야 함
                var p2d1 = factory.createResultDetail(result, p2, List.of());
                var p2d2 = factory.createResultDetail(result, p2, List.of());
                factory.createAnalysisBasis(p2d1, 40, "REVIEW", "좀 아쉬워요.");
                factory.createAnalysisBasis(p2d2, 70, "REVIEW", "괜찮아요.");

                em.flush();
                em.clear();

                // when
                var rows = groupRepositoryImpl.findGroupAnalysisBasis(testGroup.getGroupId());

                // then
                // 1) placeId → score 맵으로 만들어 점수 확인
                var placeToScore = rows.stream()
                        .collect(Collectors.toMap(GroupAnalysisBasisQueryDTO::getPlaceId,
                                GroupAnalysisBasisQueryDTO::getAnalysisScore));

                assertThat(placeToScore)
                        .containsEntry(p1.getPlaceId(), 95)
                        .containsEntry(p2.getPlaceId(), 70);

                // 2) 각 장소의 상세도 max 점수를 가진 detail로 선택됐는지 확인
                var p1Row = rows.stream().filter(r -> r.getPlaceId().equals(p1.getPlaceId())).findFirst().orElseThrow();
                var p2Row = rows.stream().filter(r -> r.getPlaceId().equals(p2.getPlaceId())).findFirst().orElseThrow();

                assertThat(p1Row.getAnalysisResultDetailId()).isEqualTo(p1d2.getAnalysisResultDetailId());
                assertThat(p2Row.getAnalysisResultDetailId()).isEqualTo(p2d2.getAnalysisResultDetailId());

            }


            @Test
            @DisplayName("대표 이미지 1장만 조인된다(placeImageId의 min 선택)")
            void joinsOneImagePerPlace() {
                TestDataFactory factory = new TestDataFactory();

                // given
                var p = factory.createPlace("디저트 카페");
                var a = factory.createAnalysis(testGroup);
                var ar = factory.createAnalysisResult(testGroup, a);
                var ard = factory.createResultDetail(ar, p, List.of());

                factory.createAnalysisBasis(ard, 77, "AI", "이곳은 어떠신가요?");
                factory.createPlaceImage(p, "https://lh3.googleusercontent.com/placeImage1");
                factory.createPlaceImage(p, "https://lh3.googleusercontent.com/placeImage2");

                em.flush();
                em.clear();

                // when
                var row = groupRepositoryImpl.findGroupAnalysisBasis(testGroup.getGroupId()).get(0);

                // then
                assertThat(row.getPlaceImageUrl()).isEqualTo("https://lh3.googleusercontent.com/placeImage1");
            }

            // 3) 이미지가 없을 경우 null
            @Test
            @DisplayName("이미지가 없으면 placeImageUrl은 null로 조회된다")
            void nullWhenNoImage() {
                TestDataFactory factory = new TestDataFactory();

                // given
                var p = factory.createPlace("떡볶이");
                var a = factory.createAnalysis(testGroup);
                var ar = factory.createAnalysisResult(testGroup, a);
                var ard = factory.createResultDetail(ar, p, new ArrayList<>());
                factory.createAnalysisBasis(ard, 50, "REVIEW", "최고의 맛집!");

                em.flush();
                em.clear();

                // when
                var row = groupRepositoryImpl.findGroupAnalysisBasis(testGroup.getGroupId()).get(0);

                // then
                assertThat(row.getPlaceId()).isEqualTo(p.getPlaceId());
                assertThat(row.getPlaceImageUrl()).isNull();
            }

            @Test
            @DisplayName("해당 groupId로 조회 가능한 데이터가 없으면 빈 리스트를 반환한다")
            void emptyWhenGroupHasNoData() {
                // given
                var otherGroupId = "NON_EXIST";

                // when
                var rows = groupRepositoryImpl.findGroupAnalysisBasis(otherGroupId);

                // then
                assertThat(rows).isEmpty();
            }
        }


        @Nested
        @DisplayName("EXCEPTION")
        class Exception {

            @Test
            @DisplayName("그룹 식별자가 null인 경우 NullPointerException이 발생해야 한다.")
            void findMemberAnalysisSettingsByGroupId_exception_tc_01() {
                // given
                final String groupId = null;

                // when & then
                assertThrows(
                        NullPointerException.class,
                        () -> groupRepositoryImpl.findGroupAnalysisBasis(groupId)
                );
            }
        }
    }

}
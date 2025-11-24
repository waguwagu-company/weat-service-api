package com.waguwagu.weat.domain.analysis.service;


import com.waguwagu.weat.domain.analysis.adaptor.AIServiceAdaptor;
import com.waguwagu.weat.domain.analysis.model.dto.SubmitAnalysisSettingDTO;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.*;

@RecordApplicationEvents
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AnalysisService.class,
        MethodValidationPostProcessor.class
})
public class AnalysisServiceTest {

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
    CategoryRepository categoryRepository;
    @MockBean
    CategoryTagRepository categoryTagRepository;
    @MockBean
    AnalysisResultLikeRepository analysisResultLikeRepository;
    @MockBean
    AnalysisResultDetailRepository analysisResultDetailRepository;
    @MockBean
    AnalysisAsyncExecutor analysisAsyncExecutor;

    @Autowired
    AnalysisService analysisService;


    @Nested
    @DisplayName("submitAnalysisSetting - 멤버별 분석 설정 제출")
    class SubmitAnalysisSetting {

        private SubmitAnalysisSettingDTO.Request mockRequest;

        private GivenContext givenSubmitSuccess() {
            // --- given 값들 ---
            final Long givenMemberId = 1L;
            final String givenGroupId = "group-10";
            final Long givenAnalysisSettingId = 999L;

            final Double givenXPosition = 37.5666102;
            final Double givenYPosition = 126.9783881;
            final String givenRoadnameAddress = "서울특별시 중구 세종대로 110";

            final Long givenCategoryTagId1 = 75L;
            final Long givenCategoryTagId2 = 83L;
            final boolean givenIsPreferred1 = true;
            final boolean givenIsPreferred2 = false;

            final Long givenCategoryId1 = 10L;
            final Long givenCategoryId2 = 11L;
            final String givenCategoryName1 = "한식";
            final String givenCategoryName2 = "일식";
            final String givenInputText = "조용하고 덜 혼잡한 곳을 원해요";

            // --- Request DTO mock ---
            mockRequest = mock(SubmitAnalysisSettingDTO.Request.class);
            when(mockRequest.getMemberId()).thenReturn(givenMemberId);

            // 위치 설정
            final SubmitAnalysisSettingDTO.Request.LocationSetting mockLocationSetting =
                    mock(SubmitAnalysisSettingDTO.Request.LocationSetting.class);
            when(mockLocationSetting.getXPosition()).thenReturn(givenXPosition);
            when(mockLocationSetting.getYPosition()).thenReturn(givenYPosition);
            when(mockLocationSetting.getRoadnameAddress()).thenReturn(givenRoadnameAddress);
            when(mockRequest.getLocationSetting()).thenReturn(mockLocationSetting);

            // 카테고리 설정
            final SubmitAnalysisSettingDTO.Request.CategorySetting mockCategorySetting1 =
                    mock(SubmitAnalysisSettingDTO.Request.CategorySetting.class);
            when(mockCategorySetting1.getCategoryTagId()).thenReturn(givenCategoryTagId1);
            when(mockCategorySetting1.getIsPreferred()).thenReturn(givenIsPreferred1);

            final SubmitAnalysisSettingDTO.Request.CategorySetting mockCategorySetting2 =
                    mock(SubmitAnalysisSettingDTO.Request.CategorySetting.class);
            when(mockCategorySetting2.getCategoryTagId()).thenReturn(givenCategoryTagId2);
            when(mockCategorySetting2.getIsPreferred()).thenReturn(givenIsPreferred2);
            when(mockRequest.getCategorySettingList())
                    .thenReturn(List.of(mockCategorySetting1, mockCategorySetting2));

            // 텍스트 입력 설정
            final SubmitAnalysisSettingDTO.Request.TextInputSetting mockTextInputSetting =
                    mock(SubmitAnalysisSettingDTO.Request.TextInputSetting.class);
            when(mockTextInputSetting.getInputText()).thenReturn(givenInputText);
            when(mockRequest.getTextInputSetting()).thenReturn(mockTextInputSetting);

            // --- Group / Member / Analysis mock ---
            final Group mockGroup = mock(Group.class);
            when(mockGroup.getGroupId()).thenReturn(givenGroupId);

            final Member mockMember = mock(Member.class);
            when(mockMember.getMemberId()).thenReturn(givenMemberId);
            when(mockMember.getGroup()).thenReturn(mockGroup);

            final Analysis mockAnalysis = mock(Analysis.class);

            // --- Repository stubbing ---
            when(memberRepository.findById(givenMemberId))
                    .thenReturn(Optional.of(mockMember));

            // isMemberSubmitAnalysisSetting 내부에서 사용하는 중복 제출 여부
            when(analysisSettingRepository.existsByMemberMemberId(givenMemberId))
                    .thenReturn(false);

            when(analysisRepository.findByGroupGroupId(givenGroupId))
                    .thenReturn(Optional.of(mockAnalysis));

            final AnalysisSetting mockSavedAnalysisSetting = mock(AnalysisSetting.class);
            when(mockSavedAnalysisSetting.getAnalysisSettingId())
                    .thenReturn(givenAnalysisSettingId);

            when(analysisSettingRepository.save(any(AnalysisSetting.class)))
                    .thenReturn(mockSavedAnalysisSetting);

            // --- Category 엔티티 + CategoryTag mock ---
            final Category mockCategory1 = mock(Category.class);
            when(mockCategory1.getCategoryId()).thenReturn(givenCategoryId1);
            when(mockCategory1.getCategoryName()).thenReturn(givenCategoryName1);

            final Category mockCategory2 = mock(Category.class);
            when(mockCategory2.getCategoryId()).thenReturn(givenCategoryId2);
            when(mockCategory2.getCategoryName()).thenReturn(givenCategoryName2);

            final CategoryTag mockCategoryTag1 = mock(CategoryTag.class);
            when(mockCategoryTag1.getCategoryTagId()).thenReturn(givenCategoryTagId1);
            when(mockCategoryTag1.getCategory()).thenReturn(mockCategory1);

            final CategoryTag mockCategoryTag2 = mock(CategoryTag.class);
            when(mockCategoryTag2.getCategoryTagId()).thenReturn(givenCategoryTagId2);
            when(mockCategoryTag2.getCategory()).thenReturn(mockCategory2);

            when(categoryTagRepository.findByIdIn(List.of(givenCategoryTagId1, givenCategoryTagId2)))
                    .thenReturn(List.of(mockCategoryTag1, mockCategoryTag2));

            // 중복 제출 false
            when(analysisSettingRepository.existsByMemberMemberId(givenMemberId)).thenReturn(false);

            return new GivenContext(
                    givenMemberId,
                    givenAnalysisSettingId,
                    givenXPosition,
                    givenYPosition,
                    givenRoadnameAddress,
                    givenCategoryTagId1,
                    givenCategoryTagId2,
                    givenIsPreferred1,
                    givenIsPreferred2,
                    givenInputText
            );
        }

        private record GivenContext(
                Long memberId,
                Long analysisSettingId,
                Double x,
                Double y,
                String addr,
                Long tagId1,
                Long tagId2,
                Boolean pref1,
                Boolean pref2,
                String inputText
        ){ }


        @Nested
        @DisplayName("SUCCESS")
        class SuccessTest {

            @Test
            @DisplayName("정상 제출 시 응답이 올바르게 반환되어야 한다.")
            void submitAnalysisSetting_success_response_tc_01() {
                // given
                final GivenContext ctx = givenSubmitSuccess();

                // when
                final SubmitAnalysisSettingDTO.Response response =
                        analysisService.submitAnalysisSetting(mockRequest);

                // then
                assertThat(response)
                        .extracting(
                                SubmitAnalysisSettingDTO.Response::getMemberId,
                                SubmitAnalysisSettingDTO.Response::getAnalysisSettingId
                        )
                        .containsExactly(
                                ctx.memberId,
                                ctx.analysisSettingId
                        );
            }


            @Test
            @DisplayName("정상 제출 시 위치/카테고리/텍스트가 저장 요청 값과 동일해야 한다.")
            void submitAnalysisSetting_success_persisted_values_tc_02() {
                // given
                final GivenContext ctx = givenSubmitSuccess();

                // when
                analysisService.submitAnalysisSetting(mockRequest);

                // then (captor 검증)
                // Location
                ArgumentCaptor<LocationSetting> locationCaptor = ArgumentCaptor.forClass(LocationSetting.class);
                then(analysisSettingDetailRepository).should(times(1)).save(locationCaptor.capture());
                LocationSetting savedLoc = locationCaptor.getValue();

                assertThat(savedLoc)
                        .extracting(
                                LocationSetting::getXPosition,
                                LocationSetting::getYPosition,
                                LocationSetting::getRoadnameAddress
                        )
                        .containsExactly(ctx.x, ctx.y, ctx.addr);

                // Category
                ArgumentCaptor<List> categoryCaptor = ArgumentCaptor.forClass(List.class);
                then(analysisSettingDetailRepository).should(times(1)).saveAll(categoryCaptor.capture());

                @SuppressWarnings("unchecked")
                List<CategorySetting> savedCats = (List<CategorySetting>) categoryCaptor.getValue();

                assertThat(savedCats).hasSize(2);
                assertThat(savedCats)
                        .extracting(cs -> cs.getCategoryTag().getCategoryTagId(),
                                CategorySetting::getIsPreferred)
                        .containsExactlyInAnyOrder(
                                tuple(ctx.tagId1, ctx.pref1),
                                tuple(ctx.tagId2, ctx.pref2)
                        );

                // Text
                ArgumentCaptor<TextInputSetting> textCaptor = ArgumentCaptor.forClass(TextInputSetting.class);
                then(analysisSettingDetailRepository).should(times(1)).save(textCaptor.capture());
                assertThat(textCaptor.getValue().getInputText()).isEqualTo(ctx.inputText);
            }


        }

    }
}

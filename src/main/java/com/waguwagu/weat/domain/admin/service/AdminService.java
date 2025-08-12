package com.waguwagu.weat.domain.admin.service;

import com.waguwagu.weat.domain.admin.dto.CreateCategoryTagDTO;
import com.waguwagu.weat.domain.admin.dto.DeleteCategoryTagDTO;
import com.waguwagu.weat.domain.admin.dto.GetGroupListDTO;
import com.waguwagu.weat.domain.admin.dto.RenameCategoryTagDTO;
import com.waguwagu.weat.domain.analysis.exception.AnalysisNotFoundForGroupIdException;
import com.waguwagu.weat.domain.analysis.model.entity.*;
import com.waguwagu.weat.domain.analysis.repository.*;
import com.waguwagu.weat.domain.category.exception.CategoryNotFoundException;
import com.waguwagu.weat.domain.category.exception.CategoryTagNotFoundException;
import com.waguwagu.weat.domain.category.model.entity.Category;
import com.waguwagu.weat.domain.category.model.entity.CategoryTag;
import com.waguwagu.weat.domain.category.repository.CategoryRepository;
import com.waguwagu.weat.domain.category.repository.CategoryTagRepository;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AdminService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisSettingDetailRepository analysisSettingDetailRepository;
    private final CategorySettingRepository categorySettingRepository;
    private final LocationSettingRepository locationSettingRepository;
    private final TextInputSettingRepository textInputSettingRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisResultDetailRepository analysisResultDetailRepository;
    private final AnalysisBasisRepository analysisBasisRepository;
    private final CategoryTagRepository categoryTagRepository;
    private final CategoryRepository categoryRepository;

    public GetGroupListDTO.Response getGroupList() {
        List<Group> groupList = groupRepository.findAll();

        List<GetGroupListDTO.Response.Group> resultGroupList = new ArrayList<>();

        for (Group group : groupList) {
            Analysis analysis = analysisRepository.findByGroupGroupId(group.getGroupId()).orElseThrow(() -> new AnalysisNotFoundForGroupIdException(group.getGroupId()));

            String analysisStatus = analysis.getAnalysisStatus().toString();

            Long analysisSettingSubmitMemberCount = analysisSettingRepository.countByAnalysis(analysis);

            Long groupMemberCount = memberRepository.countByGroup(group);
            resultGroupList.add(GetGroupListDTO.Response.Group.builder()
                    .groupId(group.getGroupId())
                    .analysisId(analysis.getAnalysisId())
                    .groupMemberCount(groupMemberCount)
                    .analysisStatus(analysisStatus)
                    .isSingleMemberGroup(group.isSingleMemberGroup())
                    .analysisSettingSubmitMemberCount(analysisSettingSubmitMemberCount)
                    .createdAt(group.getCreatedAt())
                    .build());
        }

        return GetGroupListDTO.Response.builder().groupList(resultGroupList).build();
    }



    @Transactional(readOnly = true)
    public GetGroupListDTO.Response getGroupListWithPaging(Pageable pageable, String sort, String order) {
        Page<Group> pageResult = groupRepository.findAll(pageable);


        List<GetGroupListDTO.Response.Group> resultGroupList = new ArrayList<>();
        for (Group group : pageResult.getContent()) {
            Analysis analysis = analysisRepository.findByGroupGroupId(group.getGroupId())
                    .orElseThrow(() -> new AnalysisNotFoundForGroupIdException(group.getGroupId()));

            String analysisStatus = analysis.getAnalysisStatus().toString();
            Long analysisSettingSubmitMemberCount = analysisSettingRepository.countByAnalysis(analysis);
            Long groupMemberCount = memberRepository.countByGroup(group);

            resultGroupList.add(GetGroupListDTO.Response.Group.builder()
                    .groupId(group.getGroupId())
                    .analysisId(analysis.getAnalysisId())
                    .groupMemberCount(groupMemberCount)
                    .analysisStatus(analysisStatus)
                    .isSingleMemberGroup(group.isSingleMemberGroup())
                    .analysisSettingSubmitMemberCount(analysisSettingSubmitMemberCount)
                    .createdAt(group.getCreatedAt())
                    .build());
        }

        Sort sortObj = pageable.getSort();
        Sort.Order first = sortObj.stream().findFirst().orElse(null);
        String sortProp = (first == null) ? null : first.getProperty();
        String orderDir = (first == null) ? null : first.getDirection().name().toLowerCase();

        GetGroupListDTO.Response.PageInfo pageInfo = GetGroupListDTO.Response.PageInfo.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .hasNext(pageResult.hasNext())
                .hasPrevious(pageResult.hasPrevious())
                .sort(sortProp)
                .order(orderDir)
                .build();

        return GetGroupListDTO.Response.builder()
                .groupList(resultGroupList)
                .pageInfo(pageInfo)
                .build();
    }


    public Long getGroupCount() {
        return groupRepository.count();
    }

    @Transactional
    public void deleteGroupById(String groupId) {

        if (!groupRepository.existsById(groupId)) {
            return;
        }

        // 결과(result → detail → basis)
        analysisResultRepository.findByGroupGroupId(groupId).ifPresent(result -> {
            List<AnalysisResultDetail> resultDetails = analysisResultDetailRepository.findAllByAnalysisResult(result);

            if (!resultDetails.isEmpty()) {
                analysisBasisRepository.deleteAllByAnalysisResultDetailIn(resultDetails);
                analysisResultDetailRepository.deleteAll(resultDetails);
            }
            analysisResultRepository.delete(result);
        });

        // 설정(settingDetail → category/location/text → setting),
        // 멤버별 복수 setting 존재하는 경우 대응
        List<Member> members = memberRepository.findAllByGroupGroupId(groupId);
        for (Member member : members) {

            // 멤버별 모든 설정을 조회
            List<AnalysisSetting> settings = analysisSettingRepository.findAllByMember(member);
            if (settings.isEmpty()) continue;

            for (AnalysisSetting setting : settings) {
                List<AnalysisSettingDetail> details = analysisSettingDetailRepository.findAllByAnalysisSetting(setting);

                if (!details.isEmpty()) {
                    List<Long> detailIds = details.stream().map(AnalysisSettingDetail::getAnalysisSettingDetailId).toList();

                    // 하위 테이블들 조건 삭제
                    categorySettingRepository.deleteAllByAnalysisSettingDetailIdIn(detailIds);
                    locationSettingRepository.deleteAllByAnalysisSettingDetailIdIn(detailIds);
                    textInputSettingRepository.deleteAllByAnalysisSettingDetailIdIn(detailIds);

                    analysisSettingDetailRepository.deleteAll(details);
                }

                analysisSettingRepository.delete(setting);
            }
        }

        // 분석,멤버,그룹 제거
        analysisRepository.deleteAllByGroupGroupId(groupId);
        memberRepository.deleteAllByGroupGroupId(groupId);
        groupRepository.deleteById(groupId);
    }

    // 카테고리 태그 이름 변경
    public RenameCategoryTagDTO.Response renameCategoryTag(RenameCategoryTagDTO.Request request) {
        CategoryTag categoryTag = categoryTagRepository.findById(request.getCategoryTagId())
                .orElseThrow(() -> new CategoryTagNotFoundException(request.getCategoryTagId()));

        categoryTag.setCategoryTagName(request.getCategoryTagNewName());

        return RenameCategoryTagDTO.Response.builder()
                .categoryTagId(categoryTag.getCategoryTagId())
                .categoryTagNewName(categoryTag.getCategoryTagName())
                .build();
    }


   public DeleteCategoryTagDTO.Response deleteCategoryTag(Long categoryTagId){
        categoryTagRepository.deleteById(categoryTagId);
        return DeleteCategoryTagDTO.Response.builder()
                .deletedCategoryTagId(categoryTagId)
                .build();
    }

    public CreateCategoryTagDTO.Response createCategoryTag(Long categoryId, CreateCategoryTagDTO.Request request){

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // 해당 카테고리의 기존 태그들 중 가장 마지막 순서값 찾기
        Long lastOrder = categoryTagRepository.findByCategoryOrderByCategoryTagOrderDesc(category)
                .stream()
                .findFirst()
                .map(CategoryTag::getCategoryTagOrder)
                .orElse(1L); // 태그가 없으면 1부터 시작

        CategoryTag categoryTag = CategoryTag.builder()
                .category(category)
                .categoryTagName(request.getCategoryTagName())
                .categoryTagOrder(lastOrder + 1) // 기존 최대 순서값 + 1
                .build();

        CategoryTag savedTag = categoryTagRepository.save(categoryTag);

        return CreateCategoryTagDTO.Response.builder()
                .categoryTagId(savedTag.getCategoryTagId())
                .categoryTagName(savedTag.getCategoryTagName())
                .categoryTagOrder(savedTag.getCategoryTagOrder())
                .build();
    }
}

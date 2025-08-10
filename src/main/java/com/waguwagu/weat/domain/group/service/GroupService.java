package com.waguwagu.weat.domain.group.service;

import com.waguwagu.weat.domain.analysis.model.entity.Analysis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisStatus;
import com.waguwagu.weat.domain.analysis.repository.AnalysisRepository;
import com.waguwagu.weat.domain.group.exception.GroupMemberLimitExceededException;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.mapper.GroupMapper;
import com.waguwagu.weat.domain.group.model.dto.*;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupMapper groupMapper;
    private final AnalysisRepository analysisRepository;

    @Transactional
    public CreateGroupDTO.Response createGroup(CreateGroupDTO.Request request) {
        Group group = Group.builder()
                .isSingleMemberGroup(request.getIsSingleMemberGroup())
                .build();
        groupRepository.save(group);

        Analysis analysis = Analysis.builder()
                .analysisStatus(AnalysisStatus.NOT_STARTED)
                .group(group)
                .build();

        analysisRepository.save(analysis);

        Member owner = Member.builder()
                .group(group)
                .isGroupOwner(true)
                .build();
        memberRepository.save(owner);



        return CreateGroupDTO.Response.builder()
                .groupId(group.getGroupId())
                .memberId(owner.getMemberId())
                .build();

    }

    @Transactional
    public JoinGroupDTO.Response joinGroup(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        long memberCount = memberRepository.countByGroup(group);
        if (memberCount >= 9) {
            throw new GroupMemberLimitExceededException();
        }

        Member member = Member.builder()
                .group(group)
                .isGroupOwner(false)
                .build();
        Member savedMember = memberRepository.save(member);

        return JoinGroupDTO.Response.builder()
                .memberId(savedMember.getMemberId())
                .build();
    }


    public GroupResultDTO.Response getGroupResult(String groupId) {
        List<GroupAnalysisBasisQueryDTO> basisRows = groupMapper.selectGroupAnalysisBasis(groupId);
        List<PlaceImageQueryDTO> imageRows = groupMapper.selectGroupPlaceImages(groupId);

        // 이미지 맵 구성 (placeId -> 이미지 리스트)
        Map<Long, List<PlaceImageDTO>> imageMap = imageRows.stream()
                .collect(Collectors.groupingBy(
                        PlaceImageQueryDTO::getPlaceId,
                        Collectors.mapping(img -> new PlaceImageDTO(img.getPlaceImageUrl()), Collectors.toList())
                ));

        List<GroupResultDetailDTO> result = basisRows.stream()
                .collect(Collectors.groupingBy(GroupAnalysisBasisQueryDTO::getPlaceId, LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(group -> {
                    GroupAnalysisBasisQueryDTO first = group.get(0);

                    List<AnalysisBasisDTO> analysisBasisList = group.stream()
                            .map(g -> new AnalysisBasisDTO(g.getAnalysisBasisType(), g.getAnalysisBasisContent()))
                            .distinct()
                            .collect(Collectors.toList());

                    List<PlaceImageDTO> placeImageList = imageMap.getOrDefault(first.getPlaceId(), Collections.emptyList());

                    return GroupResultDetailDTO.builder()
                            .analysisResultDetailId(first.getAnalysisResultDetailId())
                            .placeId(first.getPlaceId())
                            .placeName(first.getPlaceName())
                            .placeAddress(first.getPlaceRoadnameAddress())
                            .analysisResultContent(first.getAnalysisResultDetailContent())
                            .analysisBasisList(analysisBasisList)
                            .placeImageList(placeImageList)
                            .build();
                })
                .toList();

        return new GroupResultDTO.Response(result);
    }

}

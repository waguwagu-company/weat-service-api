package com.waguwagu.weat.domain.group.service;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    private final GroupMapper groupMapper;

    @Transactional
    public CreateGroupDTO.Response createGroup() {
        Group group = new Group();
        groupRepository.save(group);

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
        Group groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        // 분석 결과 조회
        List<GroupResultQueryDTO> queryResult = groupMapper.selectGroupAnalysisResult(groupEntity.getGroupId());

        List<GroupResultDetailDTO> result = queryResult.stream()
                .collect(Collectors.groupingBy(GroupResultQueryDTO::getPlaceId, LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(group -> {
                    GroupResultQueryDTO first = group.get(0);

                    GroupResultDetailDTO detailDTO = GroupResultDetailDTO.builder()
                            .placeId(first.getPlaceId())
                            .placeName(first.getPlaceName())
                            .placeAddress(first.getPlaceRoadnameAddress())
                            .analysisBasisContent(first.getAnalysisBasisContent())
                            .analysisResultContent(first.getAnalysisResultDetailContent())
                            .build();

                    List<PlaceImageDTO> imageList = group.stream()
                            .map(g -> new PlaceImageDTO(g.getPlaceImageUrl()))
                            .collect(Collectors.toList());

                    detailDTO.setPlaceImageList(imageList);
                    return detailDTO;
                })
                .toList();


        return new GroupResultDTO.Response(result);
    }
}

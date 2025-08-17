package com.waguwagu.weat.domain.group.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.waguwagu.weat.domain.analysis.model.entity.Analysis;
import com.waguwagu.weat.domain.analysis.model.entity.AnalysisStatus;
import com.waguwagu.weat.domain.analysis.repository.AnalysisRepository;
import com.waguwagu.weat.domain.common.utils.JsonbUtils;
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

import java.util.*;
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
                .isSingleMemberGroup(group.isSingleMemberGroup())
                .build();
    }


    public GroupResultDTO.Response getGroupResult(String groupId) {
        List<GroupAnalysisBasisQueryDTO> queryResults = groupMapper.selectGroupAnalysisBasis(groupId);

        // 분석 근거 1개, 이미지 1개이므로 placeId 기준 1건만 사용
        List<GroupResultDetailDTO> result = queryResults.stream()
                .map(r -> GroupResultDetailDTO.builder()
                        .analysisResultDetailId(r.getAnalysisResultDetailId())
                        .placeId(r.getPlaceId())
                        .placeName(r.getPlaceName())
                        .placeAddress(r.getPlaceRoadnameAddress())
                        .placeUrl(r.getPlaceUrl())
                        .keywordList(JsonbUtils.parseOrDefault(
                                r.getAnalysisResultKeywords(),
                                new TypeReference<>() {},
                                List.of()))
                        .analysisBasisType(r.getAnalysisBasisType())
                        .analysisBasisContent(r.getAnalysisBasisContent())
                        .analysisScore(r.getAnalysisScore())
                        .imageUrl(r.getPlaceImageUrl())
                        .build())
                .toList();

        return new GroupResultDTO.Response(result);
    }

}

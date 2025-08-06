package com.waguwagu.weat.domain.group.service;

import com.waguwagu.weat.domain.common.dto.ResponseDTO;
import com.waguwagu.weat.domain.group.exception.GroupMemberLimitExceededException;
import com.waguwagu.weat.domain.group.exception.GroupNotFoundException;
import com.waguwagu.weat.domain.group.model.dto.CreateGroupDTO;
import com.waguwagu.weat.domain.group.model.dto.JoinGroupDTO;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

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
}

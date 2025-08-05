package com.waguwagu.weat.domain.group.service;

import com.waguwagu.weat.domain.group.model.dto.CreateGroupDTO;
import com.waguwagu.weat.domain.group.model.entity.Group;
import com.waguwagu.weat.domain.group.model.entity.Member;
import com.waguwagu.weat.domain.group.repository.GroupRepository;
import com.waguwagu.weat.domain.group.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
}

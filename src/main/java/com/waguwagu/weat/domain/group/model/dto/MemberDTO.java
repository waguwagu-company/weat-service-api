package com.waguwagu.weat.domain.group.model.dto;

import com.waguwagu.weat.domain.group.model.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberDTO {

    private Long memberId;
    private String groupId;
    private boolean isGroupOwner;

    public static MemberDTO of(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .groupId(member.getGroup().getGroupId())
                .isGroupOwner(member.isGroupOwner())
                .build();
    }
}
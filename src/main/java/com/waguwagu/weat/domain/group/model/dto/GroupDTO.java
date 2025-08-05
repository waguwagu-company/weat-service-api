package com.waguwagu.weat.domain.group.model.dto;

import com.waguwagu.weat.domain.group.model.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GroupDTO {
    private String groupId;
    private ZonedDateTime createdAt;

    public static GroupDTO fromEntity(Group group) {
        return GroupDTO.builder()
                .groupId(group.getGroupId())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
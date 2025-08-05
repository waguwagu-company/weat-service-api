package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Tag(name = "Create Group DTO")
public class CreateGroupDTO {

    @Builder
    @Getter
    public static class Response {
        private String groupId;
        private Long memberId;
    }
}

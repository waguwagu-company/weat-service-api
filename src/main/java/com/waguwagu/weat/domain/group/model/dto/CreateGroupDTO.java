package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "그룹 생성 DTO")
public class CreateGroupDTO {

    @Builder
    @Getter
    public static class Response {
        @Schema(description = "그룹 식별자")
        private String groupId;

        @Schema(description = "멤버 식별자")
        private Long memberId;
    }
}

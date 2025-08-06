package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(name = "Join Group DTO", description = "그룹 참여 DTO")
public class JoinGroupDTO {

    @Builder
    @Getter
    public static class Response {
        @Schema(description = "멤버 ID", example = "1")
        private Long memberId;
    }
}

package com.waguwagu.weat.domain.group.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "그룹 생성 DTO")
public class CreateGroupDTO {

    @Getter
    public static class Request {
        @JsonProperty("isSingleMemberGroup")
        @Schema(description = "1인 그룹 여부")
        private boolean isSingleMemberGroup;
    }

    @Builder
    @Getter
    public static class Response {
        @Schema(description = "그룹 식별자")
        private String groupId;

        @Schema(description = "멤버 식별자")
        private Long memberId;
    }
}

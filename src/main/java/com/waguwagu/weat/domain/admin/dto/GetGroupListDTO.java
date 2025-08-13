package com.waguwagu.weat.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class GetGroupListDTO {

    @Getter
    @Builder
    public static class Response {

        @Builder.Default
        private List<Group> groupList = new ArrayList<>();
        private PageInfo pageInfo;

        @Getter
        @Builder
        public static class Group {
            private String groupId;
            private Long analysisId;
            private Long groupMemberCount;
            private Long analysisSettingSubmitMemberCount;
            private String analysisStatus;
            private ZonedDateTime createdAt;
            @JsonProperty("isSingleMemberGroup")
            private boolean isSingleMemberGroup;
        }

        @Getter
        @Builder
        public static class PageInfo {
            private int page;              // 0-base
            private int size;
            private long totalElements;
            private int totalPages;
            private boolean hasNext;
            private boolean hasPrevious;
            private String sort;           // 정렬 필드
            private String order;          // asc/desc
        }
    }

}

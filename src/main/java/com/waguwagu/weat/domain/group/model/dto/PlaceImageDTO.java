package com.waguwagu.weat.domain.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "장소 이미지", description = "장소 이미지 DTO")
public class PlaceImageDTO {
    private String imageUrl;
}
